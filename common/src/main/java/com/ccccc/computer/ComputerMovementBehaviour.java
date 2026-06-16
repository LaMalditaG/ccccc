package com.ccccc.computer;

import com.ccccc.CCCCC;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dan200.computercraft.shared.computer.blocks.ComputerBlock;
import dan200.computercraft.shared.computer.core.ComputerState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class ComputerMovementBehaviour implements MovementBehaviour {
    @Override
    public void tick(MovementContext context) {
        if(context.world.isClientSide) return;

        if(context.temporaryData instanceof ComputerActor actor){
            if(actor.hasToChangeState()){
                CCCCC.LOGGER.info("Change state NOW!");
                boolean isOn = actor.isOn();
                context.blockEntityData.putBoolean("On",isOn);
                actor.cleanChangeState();
            }

            actor.addPeripherals(context);
            actor.tick(); // Ticks after the peripherals were added so startup.lua can access them (actually, this does not work)
            syncRenderedState(context, actor.getServerComputer().getState());
        }else{
            var actor = ComputerActor.createActor(context);
            if(actor==null){
                CCCCC.LOGGER.info("Failed to create computer actor");
            }else {
                actor.addPeripherals(context);
                syncRenderedState(context, actor.getServerComputer().getState());
            }
        }

    }

    private static void syncRenderedState(MovementContext context, ComputerState desiredState) {
        if (!context.state.hasProperty(ComputerBlock.STATE)) return;
        if (context.state.getValue(ComputerBlock.STATE) == desiredState) return;

        updateRenderedState(context, context.state.setValue(ComputerBlock.STATE, desiredState));
    }

    private static void updateRenderedState(MovementContext context, BlockState state) {
        context.state = state;

        var info = new StructureBlockInfo(context.localPos, state, context.blockEntityData);
        context.contraption.getBlocks().put(context.localPos, info);

        var actor = context.contraption.getActorAt(context.localPos);
        if (actor != null) actor.setLeft(info);

        if (context.contraption.entity != null && state.hasProperty(ComputerBlock.STATE)) {
            ComputerNetworking.send(context.contraption.entity, context.localPos, state.getValue(ComputerBlock.STATE));
        }
    }
}
