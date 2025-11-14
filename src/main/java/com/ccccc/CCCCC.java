package com.ccccc;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.events.CommonEvents;
import dan200.computercraft.shared.ModRegistry;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCCCC implements ModInitializer {
	public static final String MOD_ID = "ccccc";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
        REGISTRATE.register();


        if(FabricLoader.getInstance().isModLoaded("computercraft"))
            ModInteractionBehaviours.registerDefaults();
        else
            LOGGER.error("Cant load CCCCC!");
	}


}