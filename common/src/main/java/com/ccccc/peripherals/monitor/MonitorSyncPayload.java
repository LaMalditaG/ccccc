package com.ccccc.peripherals.monitor;

import com.ccccc.CCCCC;
import dan200.computercraft.shared.computer.terminal.TerminalState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Vanilla {@link CustomPacketPayload} carrying a monitor's terminal state to clients, addressed
 * by the owning contraption entity's id and the monitor's contraption-local position. The
 * payload type itself is loader-agnostic; only registration/sending/receiving is loader-specific.
 */
public record MonitorSyncPayload(int entityId, BlockPos localPos, TerminalState terminal) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MonitorSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CCCCC.MOD_ID, "monitor_sync"));

    public static final StreamCodec<net.minecraft.network.FriendlyByteBuf, MonitorSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, MonitorSyncPayload::entityId,
                    BlockPos.STREAM_CODEC, MonitorSyncPayload::localPos,
                    TerminalState.STREAM_CODEC, MonitorSyncPayload::terminal,
                    MonitorSyncPayload::new
            );

    @Override
    public CustomPacketPayload.Type<MonitorSyncPayload> type() {
        return TYPE;
    }
}
