package com.ccccc.peripherals.monitor;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import dan200.computercraft.shared.computer.terminal.TerminalState;
import net.minecraft.core.BlockPos;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Platform hook for syncing a contraption-mounted monitor's terminal to clients.
 * Each loader's mod-init wires up a real implementation via {@link #register}; if none is
 * registered (e.g. on a loader that hasn't been wired up yet) sync is silently skipped.
 */
public interface MonitorNetworking {

    AtomicReference<MonitorNetworking> INSTANCE = new AtomicReference<>((entity, localPos, state) -> {});

    void sendSync(AbstractContraptionEntity entity, BlockPos localPos, TerminalState state);

    static void register(MonitorNetworking impl) {
        INSTANCE.set(impl);
    }

    static void send(AbstractContraptionEntity entity, BlockPos localPos, TerminalState state) {
        INSTANCE.get().sendSync(entity, localPos, state);
    }
}
