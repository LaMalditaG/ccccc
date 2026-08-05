package com.ccccc.fabric;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ModInteractionBehavioursImpl {
    public static void registerBlockMovementBehaviour(ResourceLocation rl, MovementBehaviour movementBehaviour){
        Block block = BuiltInRegistries.BLOCK.get(rl);
        if(!block.equals(Blocks.AIR)){
            MovementBehaviour.REGISTRY.register(block,movementBehaviour);
        }else{
            RegistryEntryAddedCallback.event(BuiltInRegistries.BLOCK).register((rawId, id, object) -> {
                if(rl.equals(id)){
                    MovementBehaviour.REGISTRY.register(BuiltInRegistries.BLOCK.get(rl),movementBehaviour);
                }
            });
        }
    }

    public static void registerBlockMovingInteraction(ResourceLocation rl, MovingInteractionBehaviour movementBehaviour){
        Block block = BuiltInRegistries.BLOCK.get(rl);
        if(!block.equals(Blocks.AIR)){
            MovingInteractionBehaviour.REGISTRY.register(block,movementBehaviour);
        }else{
            RegistryEntryAddedCallback.event(BuiltInRegistries.BLOCK).register((rawId, id, object) -> {
                if(rl.equals(id)){
                    MovingInteractionBehaviour.REGISTRY.register(BuiltInRegistries.BLOCK.get(rl),movementBehaviour);
                }
            });
        }
    }

//    public static void registerBlockMovementBehaviour(RegistryEntry<Block> entry, MovementBehaviour movementBehaviour){
//        try{
//            MovementBehaviour.REGISTRY.register(entry.get(),movementBehaviour);
//        }catch (Exception e){
//            RegistryEntryAddedCallback.event(BuiltInRegistries.BLOCK).register((rawId, id, object) -> {
//                if(entry.id().equals(id)){
//                    MovementBehaviour.REGISTRY.register(entry.get(),movementBehaviour);
//                }
//            });
//        }
//    }
//
//    public static void registerBlockMovingInteraction(RegistryEntry<Block> entry, MovingInteractionBehaviour movementBehaviour){
//        try{
//            MovingInteractionBehaviour.REGISTRY.register(entry.get(),movementBehaviour);
//        }catch (Exception e){
//            RegistryEntryAddedCallback.event(BuiltInRegistries.BLOCK).register((rawId, id, object) -> {
//                if(entry.id().equals(id)){
//                    MovingInteractionBehaviour.REGISTRY.register(entry.get(),movementBehaviour);
//                }
//            });
//        }
//    }

}
