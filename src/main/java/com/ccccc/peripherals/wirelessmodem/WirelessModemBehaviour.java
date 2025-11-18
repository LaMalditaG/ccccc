package com.ccccc.peripherals.wirelessmodem;

import com.ccccc.peripherals.speaker.SpeakerActor;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;

public class WirelessModemBehaviour implements MovementBehaviour {
    @Override
    public void tick(MovementContext context) {
        if(context.temporaryData instanceof WirelessModemActor actor){
            actor.updatePos(context);
        }else{
            WirelessModemActor.createActor(context);
        }
    }
}
