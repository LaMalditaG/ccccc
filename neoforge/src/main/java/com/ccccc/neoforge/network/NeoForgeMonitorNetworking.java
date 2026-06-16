package com.ccccc.neoforge.network;

import com.ccccc.CCCCC;
import com.ccccc.peripherals.monitor.MonitorNetworking;
import com.ccccc.peripherals.monitor.MonitorSyncPayload;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import dan200.computercraft.shared.computer.terminal.TerminalState;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.PacketDistributor;

public class NeoForgeMonitorNetworking implements MonitorNetworking {
    @Override
    public void sendSync(AbstractContraptionEntity entity, BlockPos localPos, TerminalState state) {
        CCCCC.LOGGER.info("Dispatching monitor sync packet for entity {} (tracking players will receive it)", entity.getId());
        PacketDistributor.sendToPlayersTrackingEntity(entity, new MonitorSyncPayload(entity.getId(), localPos, state));
    }
}
