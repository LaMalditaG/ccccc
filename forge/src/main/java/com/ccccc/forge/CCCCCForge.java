package com.ccccc.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.ccccc.CCCCC;


@Mod(CCCCC.MOD_ID)
public final class CCCCCForge {
    @SuppressWarnings("removal")
    public CCCCCForge() {

        EventBuses.registerModEventBus( CCCCC.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        CCCCC.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventHandler::setup);




    }
}
