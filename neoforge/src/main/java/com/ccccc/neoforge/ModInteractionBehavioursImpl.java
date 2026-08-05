package com.ccccc.neoforge;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;


public class ModInteractionBehavioursImpl {
    public static void registerBlockMovementBehaviour(ResourceLocation rl, MovementBehaviour movementBehaviour){
        MovementBehaviour.REGISTRY.register(BuiltInRegistries.BLOCK.get(rl),movementBehaviour);
    }

    public static void registerBlockMovingInteraction(ResourceLocation rl, MovingInteractionBehaviour movementBehaviour){
        MovingInteractionBehaviour.REGISTRY.register(BuiltInRegistries.BLOCK.get(rl),movementBehaviour);
    }


}
