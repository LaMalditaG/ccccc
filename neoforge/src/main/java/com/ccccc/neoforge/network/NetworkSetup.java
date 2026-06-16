package com.ccccc.neoforge.network;

import com.ccccc.CCCCC;
import com.ccccc.computer.ComputerClientState;
import com.ccccc.computer.ComputerNetworking;
import com.ccccc.computer.ComputerSyncPayload;
import com.ccccc.peripherals.monitor.MonitorClientCache;
import com.ccccc.peripherals.monitor.MonitorNetworking;
import com.ccccc.peripherals.monitor.MonitorSyncPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class NetworkSetup {
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToClient(
                MonitorSyncPayload.TYPE,
                MonitorSyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    CCCCC.LOGGER.info("Received monitor sync: entity={} pos={}", payload.entityId(), payload.localPos());
                    MonitorClientCache.applyState(payload);
                })
        );
        registrar.playToClient(
                ComputerSyncPayload.TYPE,
                ComputerSyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    CCCCC.LOGGER.info("Received computer state sync: entity={} pos={} state={}",
                            payload.entityId(), payload.localPos(), payload.state());
                    ComputerClientState.applyState(payload);
                })
        );

        MonitorNetworking.register(new NeoForgeMonitorNetworking());
        ComputerNetworking.register(new NeoForgeComputerNetworking());
        CCCCC.LOGGER.info("Registered monitor and computer sync network payloads");
    }
}
