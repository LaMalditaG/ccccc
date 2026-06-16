package com.ccccc.peripherals.monitor;

import com.ccccc.peripherals.PeripheralActorInterface;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.config.ConfigSpec;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

/**
 * Per-block handle into a monitor's {@link MonitorGroup}. Only the group's top-left ("owner")
 * block exposes the actual {@link IPeripheral}; the remaining member blocks of a merged
 * monitor only exist so they don't keep retrying {@link #createActor} every tick.
 */
public class MonitorActor implements PeripheralActorInterface {
    private final boolean owner;
    private final @Nullable MonitorGroup group;

    private MonitorActor(boolean owner, @Nullable MonitorGroup group) {
        this.owner = owner;
        this.group = group;
    }

    public static @Nullable MonitorActor createActor(@NotNull MovementContext ctx) {
        if (!(ctx.world instanceof ServerLevel)) return null;
        if (!MonitorGeometry.isMonitorBlock(ctx.state.getBlock())) return null;

        var geometry = MonitorGeometry.compute(ctx, ConfigSpec.monitorWidth.get(), ConfigSpec.monitorHeight.get());

        MonitorActor actor;
        if (geometry.isOwner()) {
            var group = MonitorGroupRegistry.getOrCreate(ctx.contraption, geometry.anchor(), geometry.blockWidth(), geometry.blockHeight());
            actor = new MonitorActor(true, group);
        } else {
            actor = new MonitorActor(false, null);
        }

        ctx.temporaryData = actor;
        return actor;
    }

    void tick(@NotNull MovementContext ctx) {
        if (owner && group != null) group.tick(ctx);
    }

    @Override
    public @Nullable IPeripheral getPeripheral() {
        return owner && group != null ? group.getPeripheral() : null;
    }
}
