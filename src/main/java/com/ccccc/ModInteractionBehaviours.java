package com.ccccc;

import com.ccccc.computer.ComputerMovementBehaviour;
import com.ccccc.computer.ComputerMovingInteraction;
import com.ccccc.peripherals.speaker.SpeakerMovementBehaviour;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.registry.SimpleRegistry;
import dan200.computercraft.api.ComputerCraftTags;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.platform.RegistryEntry;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;


public class ModInteractionBehaviours {
    public static void registerDefaults() {
        MovingInteractionBehaviour.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(ComputerCraftTags.Blocks.COMPUTER, new ComputerMovingInteraction()));
        MovementBehaviour.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(ComputerCraftTags.Blocks.COMPUTER, new ComputerMovementBehaviour()));

        registerBlock(ModRegistry.Blocks.SPEAKER,new SpeakerMovementBehaviour());
    }



    static void registerBlock(RegistryEntry registryEntry, MovementBehaviour movementBehaviour){
        try{
//            ((RegistryEntry<Block>)registryEntry).get();
            MovementBehaviour.REGISTRY.register(((RegistryEntry<Block>)registryEntry).get(),movementBehaviour);
        }catch (Exception e){
            RegistryEntryAddedCallback.event(BuiltInRegistries.BLOCK).register((rawId, id, object) -> {
                if(registryEntry.id().equals(id)){
                    MovementBehaviour.REGISTRY.register(((RegistryEntry<Block>)registryEntry).get(),movementBehaviour);
                }
            });
        }

    }
}
