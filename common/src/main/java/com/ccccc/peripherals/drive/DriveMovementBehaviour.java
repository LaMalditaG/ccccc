package com.ccccc.peripherals.drive;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;

public class DriveMovementBehaviour implements MovementBehaviour {
    @Override
    public void tick(MovementContext context) {
        if(context.temporaryData instanceof DriveActor actor){

        }else{
            DriveActor.createActor(context);
        }
    }
}
