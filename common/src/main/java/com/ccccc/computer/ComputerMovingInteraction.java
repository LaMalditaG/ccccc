package com.ccccc.computer;

import com.ccccc.CCCCC;
import com.ccccc.PlatformHandler;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ComputerMovingInteraction extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos pos, AbstractContraptionEntity contraptionEntity) {
        Contraption contraption = PlatformHandler.getContraption(contraptionEntity);

        var ctx = contraption.getActorAt(pos).getRight();
        if(ctx.world.isClientSide) return true; // Trying again in the server

        ServerComputer serverComputer;
        if(ctx.temporaryData instanceof ComputerActor actor)
            serverComputer = actor.getServerComputer();
        else{
            var actor = ComputerActor.createActor(ctx);
            if(actor==null) {
                CCCCC.LOGGER.info("Failed to create server computer actor");
                return false;
            }
            serverComputer = actor.getServerComputer();

            if(serverComputer==null) {
                CCCCC.LOGGER.info("Failed to create server computer");
                return false;
            }
        }

        serverComputer.turnOn();
        serverComputer.keepAlive();
        PlatformHelper.get().openMenu(
                player, Component.translatable("gui.computercraft.view_computer"),
                (id, playerInv, entity) -> new ComputerMenuWithoutInventory(ModRegistry.Menus.COMPUTER.get(), id, playerInv, p -> true, serverComputer),
                new ComputerContainerData(serverComputer, ItemStack.EMPTY));

        return false;
    }
}




