package com.ccccc.peripherals.monitor;

import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.computer.terminal.NetworkedTerminal;
import dan200.computercraft.shared.computer.terminal.TerminalState;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side store of the terminals belonging to monitors riding on contraptions, keyed by
 * the owning contraption entity's id and the monitor's position local to that contraption.
 * Populated by whatever network message a given loader uses to deliver {@link TerminalState}
 * updates from the server.
 */
public final class MonitorClientCache {
    private static final Map<Integer, Map<BlockPos, NetworkedTerminal>> CACHE = new ConcurrentHashMap<>();

    private MonitorClientCache() {
    }

    public static void applyState(MonitorSyncPayload payload) {
        applyState(payload.entityId(), payload.localPos(), payload.terminal());
    }

    public static void applyState(int entityId, BlockPos localPos, TerminalState state) {
        var perEntity = CACHE.computeIfAbsent(entityId, id -> new ConcurrentHashMap<>());
        var existing = perEntity.get(localPos);
        if (existing == null) {
            perEntity.put(localPos, state.create());
        } else {
            state.apply(existing);
        }
    }

    public static @Nullable Terminal get(int entityId, BlockPos localPos) {
        var perEntity = CACHE.get(entityId);
        return perEntity == null ? null : perEntity.get(localPos);
    }

    public static void remove(int entityId, BlockPos localPos) {
        var perEntity = CACHE.get(entityId);
        if (perEntity != null) perEntity.remove(localPos);
    }
}
