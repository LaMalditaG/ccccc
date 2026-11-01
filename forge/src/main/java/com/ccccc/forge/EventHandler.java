package com.ccccc.forge;

import com.ccccc.CCCCC;
import com.ccccc.ModInteractionBehaviours;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class EventHandler {
    public static void setup(FMLCommonSetupEvent event){
        ModInteractionBehaviours.registerDefaults();
    }
}
