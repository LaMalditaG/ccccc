package com.ccccc.fabric;

import com.ccccc.CCCCC;
import com.ccccc.ModInteractionBehaviours;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.fabricmc.api.ModInitializer;

public class CCCCCFabric implements ModInitializer {

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CCCCC.MOD_ID);

	@Override
	public void onInitialize() {
        CCCCC.init();
        REGISTRATE.register();
        ModInteractionBehaviours.registerDefaults();
	}
}