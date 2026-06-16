package com.ccccc.computer;

import com.ccccc.CCCCC;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import dan200.computercraft.shared.computer.blocks.ComputerBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/**
 * Client-side application of computer front-state sync packets.
 */
public final class ComputerClientState {
    private ComputerClientState() {
    }

    public static void applyState(ComputerSyncPayload payload) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        var entity = level.getEntity(payload.entityId());
        if (!(entity instanceof AbstractContraptionEntity contraptionEntity)) return;

        var contraption = contraptionEntity.getContraption();
        var oldInfo = contraption.getBlocks().get(payload.localPos());
        if (oldInfo == null || !oldInfo.state().hasProperty(ComputerBlock.STATE)) return;
        if (oldInfo.state().getValue(ComputerBlock.STATE) == payload.state()) return;

        var state = oldInfo.state().setValue(ComputerBlock.STATE, payload.state());
        var newInfo = new StructureBlockInfo(oldInfo.pos(), state, oldInfo.nbt());
        contraption.getBlocks().put(payload.localPos(), newInfo);
        contraption.resetClientContraption();

        var actor = contraption.getActorAt(payload.localPos());
        if (actor != null) {
            actor.setLeft(newInfo);
            actor.getRight().state = state;
        }

        CCCCC.LOGGER.info("Applied computer state sync: entity={} pos={} state={}",
                payload.entityId(), payload.localPos(), payload.state());
    }
}
