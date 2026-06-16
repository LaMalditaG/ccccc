package com.ccccc.peripherals.monitor;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dan200.computercraft.shared.config.ConfigSpec;
import net.minecraft.client.renderer.MultiBufferSource;

public class MonitorMovementBehaviour implements MovementBehaviour {
    @Override
    public void tick(MovementContext context) {
        if (context.world.isClientSide) return;

        if (context.temporaryData instanceof MonitorActor actor) {
            actor.tick(context);
        } else {
            MonitorActor.createActor(context);
        }
    }

    @Override
    public void stopMoving(MovementContext context) {
        var entity = context.contraption.entity;
        if (entity == null || !MonitorGeometry.isMonitorBlock(context.state.getBlock())) return;

        var geometry = MonitorGeometry.compute(context, ConfigSpec.monitorWidth.get(), ConfigSpec.monitorHeight.get());
        MonitorClientCache.remove(entity.getId(), geometry.anchor());
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        MonitorContraptionRenderer.render(context, matrices, buffer);
    }
}
