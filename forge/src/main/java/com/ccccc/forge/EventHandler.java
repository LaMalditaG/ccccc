package com.ccccc.forge;

import com.ccccc.CCCCC;
import com.ccccc.ModInteractionBehaviours;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = CCCCC.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class EventHandler {
    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event){
        ModInteractionBehaviours.registerDefaults();
    }
}
