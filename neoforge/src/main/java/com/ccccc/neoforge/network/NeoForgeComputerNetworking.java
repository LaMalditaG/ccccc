package com.ccccc.neoforge.network;

import com.ccccc.CCCCC;
import com.ccccc.computer.ComputerNetworking;
import com.ccccc.computer.ComputerSyncPayload;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import dan200.computercraft.shared.computer.core.ComputerState;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.PacketDistributor;

public class NeoForgeComputerNetworking implements ComputerNetworking {
    @Override
    public void sendSync(AbstractContraptionEntity entity, BlockPos localPos, ComputerState state) {
        CCCCC.LOGGER.info("Dispatching computer state sync packet for entity {} pos={} state={}",
                entity.getId(), localPos, state);
        PacketDistributor.sendToPlayersTrackingEntity(entity, new ComputerSyncPayload(entity.getId(), localPos, state));
    }
}
