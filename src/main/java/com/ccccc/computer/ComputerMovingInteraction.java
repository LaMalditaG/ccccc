package com.ccccc.computer;

import com.ccccc.accessors.ServerComputerMixinAccessor;
import com.ccccc.peripherals.PeripheralActorInterface;
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
import dan200.computercraft.shared.util.IDAssigner;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
        if(ctx.temporaryData instanceof ComputerActor actor)
            serverComputer = actor.getServerComputer();
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
}




