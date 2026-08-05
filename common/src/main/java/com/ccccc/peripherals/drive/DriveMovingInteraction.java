package com.ccccc.peripherals.drive;

import com.ccccc.PlatformHandler;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import dan200.computercraft.api.peripheral.IPeripheral;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class DriveMovingInteraction extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos pos, AbstractContraptionEntity contraptionEntity) {
        Contraption contraption = PlatformHandler.getContraption(contraptionEntity);

        var ctx = contraption.getActorAt(pos).getRight();
        if(ctx.world.isClientSide) return true; // Trying again in the server

        IPeripheral diskDrivePeripheral;
        if(ctx.temporaryData instanceof DriveActor actor)
            diskDrivePeripheral = actor.getPeripheral();
        else{
            diskDrivePeripheral = DriveActor.createActor(ctx);
            if(diskDrivePeripheral==null) return false;
        }

        player.openMenu((DriveActor.Peripheral)diskDrivePeripheral);

        return false;
    }
}
