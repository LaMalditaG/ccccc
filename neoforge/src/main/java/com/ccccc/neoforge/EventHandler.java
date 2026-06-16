package com.ccccc.neoforge;

import com.ccccc.ModInteractionBehaviours;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class EventHandler {
    public static void setup(FMLCommonSetupEvent event) {
        ModInteractionBehaviours.registerDefaults();
    }
}
