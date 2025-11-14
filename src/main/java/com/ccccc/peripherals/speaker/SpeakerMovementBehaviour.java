package com.ccccc.peripherals.speaker;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;

public class SpeakerMovementBehaviour implements MovementBehaviour {
    @Override
    public void tick(MovementContext context) {
        if(context.temporaryData instanceof SpeakerActor actor){
            actor.updatePos(context);
            actor.tick();
        }else{
            SpeakerActor.createActor(context);
        }
    }
}
