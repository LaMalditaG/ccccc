package com.ccccc.movement;

import com.ccccc.CCCCC;
import com.ccccc.accessors.ServerComputerMixinAccessor;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.config.ConfigSpec;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import dan200.computercraft.shared.platform.PlatformHelperImpl;
import dan200.computercraft.shared.util.IDAssigner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ComputerMovingInteraction extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos pos, AbstractContraptionEntity contraptionEntity) {
        Contraption contraption = contraptionEntity.getContraption();

        var ctx = contraption.getActorAt(pos).getRight();
        if(ctx.world.isClientSide) return true; // Trying again in the server

        ServerComputer serverComputer;
        if(ctx.temporaryData instanceof ComputerMovingInteraction.ComputerActor actor)
            serverComputer = actor.serverComputer;
        else{
            serverComputer = ComputerActor.createActor(ctx);
            if(serverComputer==null) return false;
        }

        serverComputer.turnOn();
        serverComputer.keepAlive();
        PlatformHelper.get().openMenu(
                player, Component.translatable("gui.computercraft.view_computer"),
                (id, playerInv, entity) -> new ComputerMenuWithoutInventory(ModRegistry.Menus.COMPUTER.get(), id, playerInv, p -> true, serverComputer),
                new ComputerContainerData(serverComputer, ItemStack.EMPTY));

        return false;
    }

    static class ComputerActor {

        private ServerComputer serverComputer;
        private UUID instanceID;
        private int computerID;
        private final ServerLevel level;
        private final ComputerFamily family;
        private boolean wasOn = false;
        private boolean hasToChangeState = false;

        public ComputerActor(int computerID,ComputerFamily family,ServerLevel level){
            this.computerID = computerID;
            this.level = level;
            this.family = family;
        }

        @Contract("null -> fail")
        public final @NotNull ServerComputer createServerComputer(MinecraftServer server) {
            if (server == null) throw new IllegalStateException("Cannot access server computer on the client.");

            var computer = ServerContext.get(server).registry().get(instanceID);
            if (computer == null) {
                if (computerID < 0)
                    computerID = ComputerCraftAPI.createUniqueNumberedSaveDir(server, IDAssigner.COMPUTER);
                computer = createComputer(computerID);
                instanceID = computer.register();
            }

            serverComputer = computer;

            return computer;
        }

        protected ServerComputer createComputer(int id) {
            return new ServerComputer( level, new BlockPos(0,0,0), ServerComputer.properties(id, family)
                .terminalSize(ConfigSpec.computerTermWidth.get(), ConfigSpec.computerTermHeight.get())
            );
        }

        public void tick(){
            if(serverComputer instanceof ServerComputerMixinAccessor accessor){
                accessor.cCCCC$tickActor();
                hasToChangeState = wasOn != serverComputer.isOn();
                wasOn = serverComputer.isOn();
            }
        }

        public void turnOn(){
            serverComputer.turnOn();
        }

        public void shutdown(){
            serverComputer.shutdown();
        }

        public boolean isOn(){
            return serverComputer.isOn();
        }

        public boolean hasToChangeState(){
            return hasToChangeState;
        }


        static @Nullable ServerComputer createActor(@NotNull MovementContext ctx){
            if(ctx.world instanceof ServerLevel serverLevel) {
                if (!ctx.blockEntityData.contains("ComputerId")) return null;
                int computerID = ctx.blockEntityData.getInt("ComputerId");

                String label = null;
                if (ctx.blockEntityData.contains("label"))
                    label = ctx.blockEntityData.getString("label");

                ComputerActor computerActor;
                ServerComputer serverComputer;
                if ((ctx.temporaryData instanceof ComputerActor sa)) {
                    computerActor = sa;
                    serverComputer = computerActor.serverComputer;
                } else {
                    if (!ctx.blockEntityData.contains("id")) return null;

                    var blockId = ctx.blockEntityData.getString("id");
                    if (blockId.equals("computercraft:computer_advanced"))
                        computerActor = new ComputerActor(computerID, ComputerFamily.ADVANCED, serverLevel);
                    else if (blockId.equals("computercraft:computer_command"))
                        computerActor = new ComputerActor(computerID, ComputerFamily.COMMAND, serverLevel);
                    else
                        computerActor = new ComputerActor(computerID, ComputerFamily.NORMAL, serverLevel);


                    ctx.temporaryData = computerActor;
                    serverComputer = computerActor.createServerComputer(serverLevel.getServer());
                    if (label != null)
                        serverComputer.setLabel(label);
                }

                if(ctx.blockEntityData.contains("On")){
                    if(ctx.blockEntityData.getBoolean("On"))
                        serverComputer.turnOn();
                }

                return serverComputer;
            }
            return null;
        }
    }




}