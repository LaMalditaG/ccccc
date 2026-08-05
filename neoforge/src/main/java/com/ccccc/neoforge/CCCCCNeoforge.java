package com.ccccc.neoforge;

import com.ccccc.ModInteractionBehaviours;
//import dev.architectury.platform.forge.EventBuses;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.ccccc.CCCCC;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;


@Mod(CCCCC.MOD_ID)
@EventBusSubscriber(modid = CCCCC.MOD_ID)
public final class CCCCCNeoforge {
    public CCCCCNeoforge() {
        CCCCC.init();
    }
    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event){
        ModInteractionBehaviours.registerDefaults();
    }
}
