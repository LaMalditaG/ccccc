package com.ccccc;

import com.ccccc.computer.ComputerMovementBehaviour;
import com.ccccc.computer.ComputerMovingInteraction;
import com.ccccc.peripherals.drive.DriveMovementBehaviour;
import com.ccccc.peripherals.drive.DriveMovingInteraction;
import com.ccccc.peripherals.speaker.SpeakerMovementBehaviour;
import com.ccccc.peripherals.wirelessmodem.WirelessModemBehaviour;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
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

        registerBlockMovementBehaviour(ModRegistry.Blocks.SPEAKER,new SpeakerMovementBehaviour());
        registerBlockMovementBehaviour(ModRegistry.Blocks.WIRELESS_MODEM_ADVANCED,new WirelessModemBehaviour());
        registerBlockMovementBehaviour(ModRegistry.Blocks.WIRELESS_MODEM_NORMAL,new WirelessModemBehaviour());
        registerBlockMovementBehaviour(ModRegistry.Blocks.DISK_DRIVE,new DriveMovementBehaviour());

        registerBlockMovingInteraction(ModRegistry.Blocks.DISK_DRIVE, new DriveMovingInteraction());
        
    }



    static void registerBlockMovementBehaviour(RegistryEntry registryEntry, MovementBehaviour movementBehaviour){
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

    static void registerBlockMovingInteraction(RegistryEntry registryEntry, MovingInteractionBehaviour movementBehaviour){
        try{
            MovingInteractionBehaviour.REGISTRY.register(((RegistryEntry<Block>)registryEntry).get(),movementBehaviour);
        }catch (Exception e){
            RegistryEntryAddedCallback.event(BuiltInRegistries.BLOCK).register((rawId, id, object) -> {
                if(registryEntry.id().equals(id)){
                    MovingInteractionBehaviour.REGISTRY.register(((RegistryEntry<Block>)registryEntry).get(),movementBehaviour);
                }
            });
        }

    }
}
