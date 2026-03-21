package com.ccccc.computer;

import com.ccccc.CCCCC;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dan200.computercraft.core.computer.Computer;
import dan200.computercraft.shared.computer.blocks.ComputerBlock;
import dan200.computercraft.shared.computer.core.ComputerState;

public class ComputerMovementBehaviour implements MovementBehaviour {
    @Override
    public void tick(MovementContext context) {
        if(context.world.isClientSide) return;

        if(context.temporaryData instanceof ComputerActor actor){
            if(actor.hasToChangeState()){
                CCCCC.LOGGER.info("Change state NOW!");
                boolean isOn = actor.isOn();
                context.blockEntityData.putBoolean("On",isOn);
                actor.addPeripherals(context);

                context.state.setValue(ComputerBlock.STATE, isOn ? ComputerState.BLINKING : ComputerState.OFF);
                actor.cleanChangeState();
            }
            actor.tick(); // Ticks after the peripherals were added so startup.lua can access them (actually, this does not work)
        }else{
            var actor = ComputerActor.createActor(context);
            if(actor==null){
                CCCCC.LOGGER.info("Failed to create computer actor");
            }else
                actor.addPeripherals(context);
        }

    }
}
