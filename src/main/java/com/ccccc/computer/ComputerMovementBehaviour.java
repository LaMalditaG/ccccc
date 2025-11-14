package com.ccccc.computer;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;

public class ComputerMovementBehaviour implements MovementBehaviour {
    @Override
    public void tick(MovementContext context) {
        if(context.temporaryData instanceof ComputerActor actor){
            actor.tick();

            if(actor.hasToChangeState()){
                context.blockEntityData.putBoolean("On",actor.isOn());
                actor.addPeripherals(context);
            }

        }else
            ComputerActor.createActor(context);

    }
}
