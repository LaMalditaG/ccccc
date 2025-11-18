package com.ccccc.peripherals.drive;

import com.ccccc.CCCCC;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.network.container.ContainerData;
import dan200.computercraft.shared.peripheral.diskdrive.DiskDriveMenu;
import dan200.computercraft.shared.peripheral.diskdrive.DiskDrivePeripheral;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;

public class DriveMovingInteraction extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos pos, AbstractContraptionEntity contraptionEntity) {
        Contraption contraption = contraptionEntity.getContraption();

        var ctx = contraption.getActorAt(pos).getRight();
        if(ctx.world.isClientSide) return true; // Trying again in the server

//        DriveActor diskDriveActor;
        IPeripheral diskDrivePeripheral;
        if(ctx.temporaryData instanceof DriveActor actor)
            diskDrivePeripheral = actor.getPeripheral();
        else{
            diskDrivePeripheral = DriveActor.createActor(ctx);
            if(diskDrivePeripheral==null) return false;
        }

//        PlatformHelper.get().openMenu(
//                player, Component.translatable("gui.computercraft.view_computer"),
//                (id, playerInv, entity) -> new DiskDriveMenu(id, playerInv, diskDriveActor.blockEntity),new DriveActor.DiskDriveContainerData(diskDriveActor.blockEntity, ItemStack.EMPTY));

//        diskDriveActor.blockEntity.startOpen(player);
//        diskDriveActor.blockEntity.stillValid()
//        player.openMenu(diskDriveActor.blockEntity);
//        ((DriveActor.Peripheral)diskDrivePeripheral).openMenu();
        player.openMenu((DriveActor.Peripheral)diskDrivePeripheral);


//        diskDriveActor.blockEntity.getCustomData();

        return false;
    }
}
