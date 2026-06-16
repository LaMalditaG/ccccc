package com.ccccc.computer;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import dan200.computercraft.shared.computer.core.ComputerState;
import net.minecraft.core.BlockPos;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Platform hook for syncing a contraption-mounted computer's rendered block state to clients.
 */
public interface ComputerNetworking {
    AtomicReference<ComputerNetworking> INSTANCE = new AtomicReference<>((entity, localPos, state) -> {});

    void sendSync(AbstractContraptionEntity entity, BlockPos localPos, ComputerState state);

    static void register(ComputerNetworking impl) {
        INSTANCE.set(impl);
    }

    static void send(AbstractContraptionEntity entity, BlockPos localPos, ComputerState state) {
        INSTANCE.get().sendSync(entity, localPos, state);
    }
}
