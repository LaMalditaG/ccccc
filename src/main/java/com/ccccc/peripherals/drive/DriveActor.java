package com.ccccc.peripherals.drive;

import com.ccccc.peripherals.PeripheralActorInterface;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.filesystem.WritableMount;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.util.StringUtil;
import dan200.computercraft.shared.media.items.DiskItem;
import dan200.computercraft.shared.network.client.PlayRecordClientMessage;
import dan200.computercraft.shared.peripheral.diskdrive.DiskDriveBlockEntity;
import dan200.computercraft.shared.peripheral.diskdrive.DiskDriveMenu;
import dan200.computercraft.shared.peripheral.diskdrive.DiskDriveState;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DriveActor implements PeripheralActorInterface {
    Peripheral peripheral;

    private Vec3 pos;

    @Override
    public IPeripheral getPeripheral() {
        return peripheral;
    }

    static IPeripheral createActor(@NotNull MovementContext ctx){
        if (ctx.world instanceof ServerLevel serverLevel) {
            var actor = new DriveActor();

            var storage = ctx.contraption.getStorage().getMountedItems().storages.get(ctx.localPos);
            actor.peripheral = new Peripheral(storage,serverLevel);

            ctx.temporaryData = actor;
            actor.pos = ctx.position;

            return actor.getPeripheral();
        }
        return null;
    }

    public void tick(){}

    /**
     * A modified version of CC-Tweaked's{@link dan200.computercraft.shared.peripheral.diskdrive.DiskDrivePeripheral} adapted to use a different storage
     * The rest is copied
     */
    static class Peripheral implements MenuProvider, IPeripheral {
        FakeContainer diskDrive;

        Peripheral(MountedItemStorage storage,ServerLevel level){
            diskDrive = new FakeContainer(storage,level);
            diskDrive.setChanged();
        }

        @Override
        public @NotNull String getType() {
            return "drive";
        }

        @Override
        public boolean equals(@Nullable IPeripheral other) {
            return false;
        }

        @Override
        public @NotNull Component getDisplayName() {
            var s = Component.translatable("block.computercraft.disk_drive").getString();
            return Component.translatable("create.contraptions.moving_container",s);
        }

        @Override
        public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
            return new DiskDriveMenu(i, inventory, this.diskDrive);
        }

        @LuaFunction
        public final boolean isDiskPresent() {
            return diskDrive.getMedia().stack().isEmpty();
        }

        @LuaFunction
        public final @Nullable Object @Nullable [] getDiskLabel() {
            var media = diskDrive.getMedia();
            return media.media() == null ? null : new Object[]{ media.media().getLabel(media.stack()) };
        }

        @LuaFunction(mainThread = true)
        public final void setDiskLabel(Optional<String> label) throws LuaException {
            switch (diskDrive.setDiskLabel(label.map(StringUtil::normaliseLabel).orElse(null))) {
                case NOT_ALLOWED -> throw new LuaException("Disk label cannot be changed");
                case CHANGED, NO_MEDIA -> {
                }
            }
        }

        @LuaFunction
        public final boolean hasData(IComputerAccess computer) {
            return diskDrive.getDiskMountPath(computer) != null;
        }

        @LuaFunction
        @Nullable
        public final String getMountPath(IComputerAccess computer) {
            return diskDrive.getDiskMountPath(computer);
        }

        @LuaFunction
        public final boolean hasAudio() {
            return diskDrive.getMedia().getAudio() != null;
        }

        @LuaFunction
        @Nullable
        public final Object getAudioTitle() {
            var stack = diskDrive.getMedia();
            return stack.media() != null ? stack.getAudioTitle() : false;
        }

        @LuaFunction
        public final void playAudio() {
            diskDrive.playDiskAudio();
        }

        @LuaFunction
        public final void stopAudio() {
            diskDrive.stopDiskAudio();
        }

        @LuaFunction
        public final void ejectDisk() {
            diskDrive.ejectDisk();
        }

        @LuaFunction
        public final @Nullable Object @Nullable [] getDiskID() {
            var disk = diskDrive.getMedia().stack();
            return disk.getItem() instanceof DiskItem ? new Object[]{ DiskItem.getDiskID(disk) } : null;
        }


        @Override
        public void attach(@NotNull IComputerAccess computer) {
            diskDrive.attach(computer);
        }

        @Override
        public void detach(@NotNull IComputerAccess computer) {
            diskDrive.detach(computer);
        }

        //------

        /**
         * A class with the same custom methods that CC-Tweaked's{@link DiskDriveBlockEntity}class has, but without being a BlockEntity
         * The rest is copied
         * I still need to discover what some of these methods actually do
         */
        static class FakeContainer implements Container {
            MountedItemStorage storage;
            ServerLevel level;

            private static final class MountInfo {
                @Nullable
                String mountPath;
            }

            @GuardedBy("this")
            private final Map<IComputerAccess, MountInfo> computers = new HashMap<>();
            @GuardedBy("this")
            private MediaStack media = MediaStack.EMPTY;
            @GuardedBy("this")
            private @Nullable Mount mount;

            private boolean recordPlaying = false;
            // In order to avoid main-thread calls in the peripheral, we set flags to mark which operation should be performed,
            // then read them when ticking.
            private final AtomicReference<RecordCommand> recordQueued = new AtomicReference<>(null);
            private final AtomicBoolean ejectQueued = new AtomicBoolean(false);

            private final AtomicBoolean stackDirty = new AtomicBoolean(false);

            FakeContainer(MountedItemStorage storage,ServerLevel level){
                this.level = level;
                this.storage = storage;
            }

            @Override
            public int getContainerSize() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return getItem(0).isEmpty();
            }

            @Override
            public @NotNull ItemStack getItem(int i) {
                return storage.getStackInSlot(i);
            }

            @Override
            public @NotNull ItemStack removeItem(int i, int j) {
                var itemStack = storage.getStackInSlot(i);
                return itemStack.split(j);
            }

            @Override
            public @NotNull ItemStack removeItemNoUpdate(int i) {
                return removeItem(i,getMaxStackSize());
            }

            @Override
            public void setItem(int i, @NotNull ItemStack itemStack) {
                storage.setStackInSlot(i,itemStack);
            }

            @Override
            public void setChanged() {
                if(!level.isClientSide) updateMedia();
//                blockEntity.setChanged();
            }

            @Override
            public boolean stillValid(@NotNull Player player) {
                return true;
            }

            @Override
            public void clearContent() {
                removeItemNoUpdate(0);
            }

            private synchronized void updateMedia() {
                var newStack = getDiskStack();
                if (ItemStack.isSameItemSameTags(newStack, media.stack())) return;

                var newMedia = MediaStack.of(newStack);

//                if (newStack.isEmpty()) {
//                    updateBlockState(DiskDriveState.EMPTY);
//                } else {
//                    updateBlockState(newMedia.media() != null ? DiskDriveState.FULL : DiskDriveState.INVALID);
//                }

                // Unmount old disk
                if (!media.stack().isEmpty()) {
                    for (var computer : computers.entrySet()) unmountDisk(computer.getKey(), computer.getValue());
                }

                // Stop music
                if (recordPlaying) {
                    stopRecord();
                    recordPlaying = false;
                }

                // Use our new media, and (if needed) mount the new disk.
                mount = null;
                media = newMedia;
                stackDirty.set(false);

                if (!newStack.isEmpty() && !computers.isEmpty()) {
                    var mount = getOrCreateMount(true);
                    for (var entry : computers.entrySet()) {
                        mountDisk(entry.getKey(), entry.getValue(), mount);
                    }
                }
            }

            ItemStack getDiskStack() {
                return getItem(0);
            }

            synchronized MediaStack getMedia() {
                return media;
            }

            void setDiskStack(ItemStack stack) {
                setItem(0, stack);
                setChanged();
            }

            private synchronized void updateDiskFromMedia() {
                // Write back the item to the main inventory, and then mark it as dirty.
                setItem(0, media.stack().copy());
            }

            @GuardedBy("this")
            private void updateMediaStack(ItemStack stack, boolean immediate) {
                if (ItemStack.isSameItemSameTags(media.stack(), stack)) return;
                media = new MediaStack(stack, media.media());

                if (immediate) {
                    updateDiskFromMedia();
                } else {
                    stackDirty.set(true);
                }
            }

            @Nullable
            String getDiskMountPath(IComputerAccess computer) {
                synchronized (this) {
                    var info = computers.get(computer);
                    return info != null ? info.mountPath : null;
                }
            }

            void attach(IComputerAccess computer) {
                synchronized (this) {
                    var info = new MountInfo();
                    computers.put(computer, info);
                    if (!media.stack().isEmpty()) {
//                        mountDisk(computer, info, getOrCreateMount(level instanceof ServerLevel l && l.getServer().isSameThread()));
                        mountDisk(computer,info,getOrCreateMount(true));
                    }
                }
            }

            void detach(IComputerAccess computer) {
                synchronized (this) {
                    unmountDisk(computer, computers.remove(computer));
                }
            }

            void playDiskAudio() {
                recordQueued.set(RecordCommand.PLAY);
            }

            void stopDiskAudio() {
                recordQueued.set(RecordCommand.STOP);
            }

            void ejectDisk() {
                ejectQueued.set(true);
            }

            synchronized MountResult setDiskLabel(@Nullable String label) {
                if (media.media() == null) return MountResult.NO_MEDIA;

                // Set the label, and write it back to the media stack.
                var stack = media.stack().copy();
                if (!media.media().setLabel(stack, label)) return MountResult.NOT_ALLOWED;
                updateMediaStack(stack, true);

                return MountResult.CHANGED;
            }

            @GuardedBy("this")
            private @Nullable Mount getOrCreateMount(boolean immediate) {
                if (media.media() == null) return null;
                if (mount != null) return mount;

                // Set the id (if needed) and write it back to the media stack.
                var stack = media.stack().copy();
                mount = media.media().createDataMount(stack, level);
                updateMediaStack(stack, immediate);

                return mount;
            }

            private static void mountDisk(IComputerAccess computer, MountInfo info, @Nullable Mount mount) {
                if (mount instanceof WritableMount writable) {
                    // Try mounting at the lowest numbered "disk" name we can
                    var n = 1;
                    while (info.mountPath == null) {
                        info.mountPath = computer.mountWritable(n == 1 ? "disk" : "disk" + n, writable);
                        n++;
                    }
                } else if (mount != null) {
                    // Try mounting at the lowest numbered "disk" name we can
                    var n = 1;
                    while (info.mountPath == null) {
                        info.mountPath = computer.mount(n == 1 ? "disk" : "disk" + n, mount);
                        n++;
                    }
                } else {
                    assert info.mountPath == null : "Mount path should be null";
                }

                computer.queueEvent("disk", computer.getAttachmentName());
            }

            private static void unmountDisk(IComputerAccess computer, MountInfo info) {
                if (info.mountPath != null) {
                    computer.unmount(info.mountPath);
                    info.mountPath = null;
                }

                computer.queueEvent("disk_eject", computer.getAttachmentName());
            }

            private void ejectContents() {
//                if (getLevel().isClientSide) return;
//
//                var stack = getDiskStack();
//                if (stack.isEmpty()) return;
//                setDiskStack(ItemStack.EMPTY);
//
//                WorldUtil.dropItemStack(getLevel(), getBlockPos(), getDirection(), stack);
//                getLevel().levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, getBlockPos(), 0);
            }

            private void stopRecord() {
//                sendMessage(new PlayRecordClientMessage(getBlockPos()));
            }

            private void sendMessage(PlayRecordClientMessage message) {
//                ServerNetworking.sendToAllAround(message, (ServerLevel) getLevel(), Vec3.atCenterOf(getBlockPos()), 64);
            }


            private enum RecordCommand {
                PLAY,
                STOP,
            }

            enum MountResult {
                NO_MEDIA,
                NOT_ALLOWED,
                CHANGED,
            }
        }

    }
}

