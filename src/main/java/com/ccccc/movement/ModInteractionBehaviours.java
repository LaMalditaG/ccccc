package com.ccccc.movement;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.registry.SimpleRegistry;
import dan200.computercraft.api.ComputerCraftTags;

public class ModInteractionBehaviours {
    public static void registerDefaults() {
        MovingInteractionBehaviour.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(ComputerCraftTags.Blocks.COMPUTER, new ComputerMovingInteraction()));
        MovementBehaviour.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(ComputerCraftTags.Blocks.COMPUTER, new ComputerMovementBehaviour()));
    }
}
