package com.ccccc.peripherals.monitor;

import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Caches one {@link MonitorGroup} per (contraption, anchor position), so every block in a
 * merged monitor structure shares the same backing terminal/peripheral. Keyed weakly on the
 * {@link Contraption} so groups are reclaimed once it (and the assembled structure it
 * represents) is no longer referenced.
 */
final class MonitorGroupRegistry {
    private static final Map<Contraption, Map<BlockPos, MonitorGroup>> GROUPS = new WeakHashMap<>();

    private MonitorGroupRegistry() {
    }

    static MonitorGroup getOrCreate(Contraption contraption, BlockPos anchor, int blockWidth, int blockHeight) {
        var perContraption = GROUPS.computeIfAbsent(contraption, c -> new HashMap<>());
        var group = perContraption.get(anchor);
        if (group == null) {
            group = new MonitorGroup(blockWidth, blockHeight);
            perContraption.put(anchor, group);
        } else if (group.blockWidth() != blockWidth || group.blockHeight() != blockHeight) {
            group.resize(blockWidth, blockHeight);
        }
        return group;
    }
}
