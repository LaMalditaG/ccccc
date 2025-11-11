package com.ccccc.movement;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.behaviour.SimpleBlockMovingInteraction;
import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.MutablePair;

public class ComputerMovingInteraction extends SimpleBlockMovingInteraction {
    @Override
    protected BlockState handle(Player player, Contraption contraption, BlockPos pos, BlockState currentState) {
 //       playSound(player, SoundEvents.LEVER_CLICK, 1.0f);
 //       return currentState;
        Level level = player.level();
        MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor = contraption.getActorAt(pos);


        if(BlockEntity.loadStatic(pos,actor.getLeft().state(),actor.getRight().blockEntityData) instanceof AbstractComputerBlockEntity computer){
            if (!level.isClientSide && computer.isUsable(player)) {
                var serverComputer = computer.createServerComputer();
                serverComputer.turnOn();
                PlatformHelper.get().openMenu(player, computer.getName(), computer, new ComputerContainerData(serverComputer, ItemStack.EMPTY));
            }
        }


//        if (!player.isCrouching() && level.getBlockEntity(pos) instanceof AbstractComputerBlockEntity computer) {
//            // Regular right click to activate computer
//            if (!level.isClientSide && computer.isUsable(player)) {
//                var serverComputer = computer.createServerComputer();
//                serverComputer.turnOn();
//
//                PlatformHelper.get().openMenu(player, computer.getName(), computer, new ComputerContainerData(serverComputer, getItem(computer)));
//            }
//            return InteractionResult.sidedSuccess(level.isClientSide);
//        }
        return currentState;
    }

}