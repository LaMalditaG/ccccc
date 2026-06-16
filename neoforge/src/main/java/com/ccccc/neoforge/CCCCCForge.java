package com.ccccc.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import com.ccccc.CCCCC;


@Mod(CCCCC.MOD_ID)
public final class CCCCCForge {
    public CCCCCForge(IEventBus modEventBus) {
        CCCCC.init();

        modEventBus.addListener(EventHandler::setup);
    }
}
