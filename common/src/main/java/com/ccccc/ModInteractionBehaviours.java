package com.ccccc;

import com.ccccc.computer.ComputerMovementBehaviour;
import com.ccccc.computer.ComputerMovingInteraction;
import com.ccccc.peripherals.drive.DriveMovementBehaviour;
import com.ccccc.peripherals.drive.DriveMovingInteraction;
import com.ccccc.peripherals.speaker.SpeakerMovementBehaviour;
import com.ccccc.peripherals.wirelessmodem.WirelessModemBehaviour;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.registry.SimpleRegistry;
import dan200.computercraft.api.ComputerCraftTags;
import dan200.computercraft.shared.ModRegistry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;

public class ModInteractionBehaviours {
    public static void registerDefaults() {
        MovingInteractionBehaviour.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(ComputerCraftTags.Blocks.COMPUTER, new ComputerMovingInteraction()));
        MovementBehaviour.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(ComputerCraftTags.Blocks.COMPUTER, new ComputerMovementBehaviour()));

        registerBlockMovementBehaviour(ModRegistry.Blocks.SPEAKER.id(),new SpeakerMovementBehaviour());
        registerBlockMovementBehaviour(ModRegistry.Blocks.WIRELESS_MODEM_ADVANCED.id(),new WirelessModemBehaviour());
        registerBlockMovementBehaviour(ModRegistry.Blocks.WIRELESS_MODEM_NORMAL.id(),new WirelessModemBehaviour());
        registerBlockMovementBehaviour(ModRegistry.Blocks.DISK_DRIVE.id(),new DriveMovementBehaviour());

        registerBlockMovingInteraction(ModRegistry.Blocks.DISK_DRIVE.id(), new DriveMovingInteraction());

    }

    @ExpectPlatform
    static void registerBlockMovementBehaviour(ResourceLocation rl, MovementBehaviour movementBehaviour){
        throw new AssertionError();
    }

    @ExpectPlatform
    static void registerBlockMovingInteraction(ResourceLocation rl, MovingInteractionBehaviour movementBehaviour){
        throw new AssertionError();
    }
}
