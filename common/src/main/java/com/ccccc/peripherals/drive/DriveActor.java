package com.ccccc.peripherals.drive;

import com.ccccc.CCCCC;
import com.ccccc.peripherals.PeripheralActorInterface;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.filesystem.WritableMount;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.util.StringUtil;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.peripheral.diskdrive.DiskDriveMenu;
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
            if(storage==null){
                CCCCC.LOGGER.error("Disk drive is not tagged as Simple Mounted Storage. Did some mod remove it?");
                return null;
            }

            actor.peripheral = new Peripheral(storage,serverLevel);

            ctx.temporaryData = actor;
            actor.pos = ctx.position;

            return actor.getPeripheral();
        }
        return null;
    }

    public void tick(){}

    /**
     * A modified version of CC-Tweaked's DiskDrivePeripheral adapted to use a different storage.
     * The rest is copied.
     */
    static class Peripheral implements MenuProvider, IPeripheral {
        FakeContainer diskDrive;

        Peripheral(MountedItemStorage storage, ServerLevel level){
            diskDrive = new FakeContainer(storage, level);
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
            return Component.translatable("create.contraptions.moving_container", s);
        }

        @Override
        public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
            return new DiskDriveMenu(i, inventory, this.diskDrive);
        }

        @LuaFunction
        public final boolean isDiskPresent() {
            return !diskDrive.getMedia().stack().isEmpty();
        }

        @LuaFunction
        public final @Nullable Object @Nullable [] getDiskLabel() {
            var media = diskDrive.getMedia();
            return media.media() == null ? null : new Object[]{ media.media().getLabel(diskDrive.level.registryAccess(), media.stack()) };
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
            return diskDrive.getMedia().getAudio(diskDrive.level.registryAccess()) != null;
        }

        @LuaFunction
        @Nullable
        public final Object getAudioTitle() {
            var media = diskDrive.getMedia();
            if (media.media() == null) return false;

            var audio = media.getAudio(diskDrive.level.registryAccess());
            return audio == null ? null : audio.value().description().getString();
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
            var id = diskDrive.getMedia().stack().get(ModRegistry.DataComponents.DISK_ID.get());
            return id != null ? new Object[]{ id.id() } : null;
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
         * A class with the same custom methods that CC-Tweaked's DiskDriveBlockEntity has, but without being a BlockEntity.
         */
        static class FakeContainer implements Container {
            MountedItemStorage storage;
            ServerLevel level;

            private static final class MountInfo {
                @Nullable
                String mountPath;
            }

            private final Map<IComputerAccess, MountInfo> computers = new HashMap<>();
            private MediaStack media = MediaStack.EMPTY;
            private @Nullable Mount mount;

            private boolean recordPlaying = false;
            private final AtomicReference<RecordCommand> recordQueued = new AtomicReference<>(null);
            private final AtomicBoolean ejectQueued = new AtomicBoolean(false);

            private final AtomicBoolean stackDirty = new AtomicBoolean(false);

            FakeContainer(MountedItemStorage storage, ServerLevel level){
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
                return removeItem(i, getMaxStackSize());
            }

            @Override
            public void setItem(int i, @NotNull ItemStack itemStack) {
                storage.setStackInSlot(i, itemStack);
            }

            @Override
            public void setChanged() {
                if(!level.isClientSide) updateMedia();
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
                if (ItemStack.isSameItemSameComponents(newStack, media.stack())) return;

                var newMedia = MediaStack.of(newStack);

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
                setItem(0, media.stack().copy());
            }

            private void updateMediaStack(ItemStack stack, boolean immediate) {
                if (ItemStack.isSameItemSameComponents(media.stack(), stack)) return;
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
                        mountDisk(computer, info, getOrCreateMount(true));
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

                var stack = media.stack().copy();
                if (!media.media().setLabel(stack, label)) return MountResult.NOT_ALLOWED;
                updateMediaStack(stack, true);

                return MountResult.CHANGED;
            }

            private @Nullable Mount getOrCreateMount(boolean immediate) {
                if (media.media() == null) return null;
                if (mount != null) return mount;

                var stack = media.stack().copy();
                mount = media.media().createDataMount(stack, level);
                updateMediaStack(stack, immediate);

                return mount;
            }

            private static void mountDisk(IComputerAccess computer, MountInfo info, @Nullable Mount mount) {
                if (mount instanceof WritableMount writable) {
                    var n = 1;
                    while (info.mountPath == null) {
                        info.mountPath = computer.mountWritable(n == 1 ? "disk" : "disk" + n, writable);
                        n++;
                    }
                } else if (mount != null) {
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
                // Not implemented for contraption drives
            }

            private void stopRecord() {
                // Not implemented for contraption drives
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
