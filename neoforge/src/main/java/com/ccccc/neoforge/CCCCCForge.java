package com.ccccc.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import com.ccccc.CCCCC;
import com.ccccc.neoforge.network.NetworkSetup;


@Mod(CCCCC.MOD_ID)
public final class CCCCCForge {
    public CCCCCForge(IEventBus modEventBus) {
        CCCCC.init();

        modEventBus.addListener(EventHandler::setup);
        modEventBus.addListener(NetworkSetup::register);
    }
}
