package com.ccccc.computer;

import com.ccccc.CCCCC;
import dan200.computercraft.shared.computer.core.ComputerState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Syncs the rendered block-state front of a contraption-mounted computer to clients. Create
 * does not automatically resend local contraption block states after assembly, so live
 * OFF/ON/BLINKING transitions need their own tiny update.
 */
public record ComputerSyncPayload(int entityId, BlockPos localPos, ComputerState state) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ComputerSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CCCCC.MOD_ID, "computer_sync"));

    private static final ComputerState[] STATES = ComputerState.values();
    private static final StreamCodec<io.netty.buffer.ByteBuf, ComputerState> STATE_CODEC =
            ByteBufCodecs.idMapper(index -> STATES[index], ComputerState::ordinal);

    public static final StreamCodec<FriendlyByteBuf, ComputerSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ComputerSyncPayload::entityId,
                    BlockPos.STREAM_CODEC, ComputerSyncPayload::localPos,
                    STATE_CODEC, ComputerSyncPayload::state,
                    ComputerSyncPayload::new
            );

    @Override
    public CustomPacketPayload.Type<ComputerSyncPayload> type() {
        return TYPE;
    }
}
