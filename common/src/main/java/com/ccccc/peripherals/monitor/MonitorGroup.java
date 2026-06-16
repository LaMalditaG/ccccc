package com.ccccc.peripherals.monitor;

import com.ccccc.CCCCC;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.apis.TermMethods;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.computer.terminal.NetworkedTerminal;
import dan200.computercraft.shared.computer.terminal.TerminalState;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlockEntity;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Shared state for one merged (possibly multiblock) monitor display on a contraption,
 * mirroring how a real {@code ServerMonitor} is owned by a single origin block once monitors
 * merge. Owned exclusively by the group's top-left ("owner") {@link MonitorActor}.
 */
final class MonitorGroup {
    private static final long RESYNC_INTERVAL_TICKS = 100;

    private final AtomicBoolean changed = new AtomicBoolean(true);
    private final Peripheral peripheral = new Peripheral();
    private volatile NetworkedTerminal terminal;
    private int textScale = 2;
    private int blockWidth;
    private int blockHeight;
    private long nextResyncTick = 0;

    MonitorGroup(int blockWidth, int blockHeight) {
        resize(blockWidth, blockHeight);
    }

    int blockWidth() {
        return blockWidth;
    }

    int blockHeight() {
        return blockHeight;
    }

    void resize(int blockWidth, int blockHeight) {
        this.blockWidth = blockWidth;
        this.blockHeight = blockHeight;

        var size = computeSize(textScale, blockWidth, blockHeight);
        if (terminal == null) {
            terminal = new NetworkedTerminal(size[0], size[1], true, () -> changed.set(true));
        } else {
            terminal.resize(size[0], size[1]);
        }
        changed.set(true);
    }

    /**
     * Mirrors {@code ServerMonitor.rebuild()}'s sizing formula.
     */
    private static int[] computeSize(int textScale, int blockWidth, int blockHeight) {
        double fontScale = textScale * 0.5;
        double pixelScale = MonitorBlockEntity.RENDER_PIXEL_SCALE;
        double border = 2 * (MonitorBlockEntity.RENDER_BORDER + MonitorBlockEntity.RENDER_MARGIN);
        int columns = (int) Math.max(1, Math.round((blockWidth - border) / (fontScale * 6.0 * pixelScale)));
        int rows = (int) Math.max(1, Math.round((blockHeight - border) / (fontScale * 9.0 * pixelScale)));
        return new int[]{columns, rows};
    }

    IPeripheral getPeripheral() {
        return peripheral;
    }

    void tick(@NotNull MovementContext ctx) {
        if (ctx.world.isClientSide) return;

        var entity = ctx.contraption.entity;
        if (entity == null) {
            if (changed.get()) {
                CCCCC.LOGGER.info("Monitor group at {} has no contraption entity yet, skipping sync (will retry on next tick)", ctx.localPos);
            }
            return;
        }

        var gameTime = ctx.world.getGameTime();
        var forcedResync = gameTime >= nextResyncTick;
        if (!changed.compareAndSet(true, false) && !forcedResync) return;

        if (forcedResync) nextResyncTick = gameTime + RESYNC_INTERVAL_TICKS;

        CCCCC.LOGGER.info("Sending monitor sync: entity={} pos={} size={}x{} forced={}",
                entity.getId(), ctx.localPos, terminal.getWidth(), terminal.getHeight(), forcedResync);
        MonitorNetworking.send(entity, ctx.localPos, TerminalState.create(terminal));
    }

    final class Peripheral extends TermMethods implements IPeripheral {
        @Override
        public Terminal getTerminal() {
            return terminal;
        }

        @Override
        public @NotNull String getType() {
            return "monitor";
        }

        @Override
        public boolean equals(@Nullable IPeripheral other) {
            return this == other;
        }

        @Override
        public void attach(@NotNull IComputerAccess computer) {
            CCCCC.LOGGER.debug("Monitor attached to {}", computer.getAttachmentName());
        }

        @LuaFunction(mainThread = true)
        public void setTextScale(double scale) throws LuaException {
            if (scale < 0.5 || scale > 5) throw new LuaException("Expected number in range 0.5-5");
            textScale = (int) Math.round(Mth.clamp(scale, 0.5, 5) * 2);
            resize(blockWidth, blockHeight);
        }

        @LuaFunction
        public double getTextScale() {
            return textScale / 2.0;
        }
    }
}
