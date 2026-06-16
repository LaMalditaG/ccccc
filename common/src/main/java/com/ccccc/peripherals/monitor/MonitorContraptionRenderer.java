package com.ccccc.peripherals.monitor;

import com.ccccc.CCCCC;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import dan200.computercraft.client.render.text.FixedWidthFontRenderer;
import dan200.computercraft.shared.config.ConfigSpec;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Draws a contraption-mounted (possibly merged/multiblock) monitor's terminal in-world while
 * it's part of a moving Create contraption. Only the merged group's top-left ("owner") block
 * renders - it draws a single quad-grid spanning the whole group's footprint, the same way the
 * group's blocks are physically coplanar and contiguous. This isn't a byte-for-byte port of
 * CC:Tweaked's own {@code MonitorBlockEntityRenderer} (most of its blit/TBO machinery is
 * private and bound to a real {@code BlockEntity}/world position) - instead it fits the
 * terminal's character grid onto the group's footprint using {@link FixedWidthFontRenderer}'s
 * public quad-emitting API.
 */
public final class MonitorContraptionRenderer {
    private static final float CONTENT_INSET = (float) (MonitorBlockEntity.RENDER_BORDER + MonitorBlockEntity.RENDER_MARGIN);
    private static final float BACKGROUND_MARGIN = (float) (MonitorBlockEntity.RENDER_MARGIN * 1.1);
    private static volatile long lastLog = 0;

    private MonitorContraptionRenderer() {
    }

    private static void logThrottled(String message, Object... args) {
        long now = System.currentTimeMillis();
        if (now - lastLog > 2000) {
            lastLog = now;
            CCCCC.LOGGER.info(message, args);
        }
    }

    public static void render(MovementContext context, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!MonitorGeometry.isMonitorBlock(context.state.getBlock())) return;

        var entity = context.contraption.entity;
        if (entity == null) {
            logThrottled("Monitor render: no contraption entity");
            return;
        }

        var geometry = MonitorGeometry.compute(context, ConfigSpec.monitorWidth.get(), ConfigSpec.monitorHeight.get());
        if (!geometry.isOwner()) return;

        var terminal = MonitorClientCache.get(entity.getId(), geometry.anchor());
        if (terminal == null) {
            logThrottled("Monitor render: no cached terminal yet for entity={} anchor={}", entity.getId(), geometry.anchor());
            return;
        }

        logThrottled("Monitor render: drawing entity={} anchor={} size={}x{} blocks={}x{}",
                entity.getId(), geometry.anchor(), terminal.getWidth(), terminal.getHeight(), geometry.blockWidth(), geometry.blockHeight());

        Direction front = geometry.front();
        Direction right = geometry.right();
        Direction up = geometry.up();
        int blockWidth = geometry.blockWidth();
        int blockHeight = geometry.blockHeight();

        int cols = terminal.getWidth();
        int rows = terminal.getHeight();

        float usableW = blockWidth - 2 * CONTENT_INSET;
        float usableH = blockHeight - 2 * CONTENT_INSET;
        float scaleX = (usableW / cols) / FixedWidthFontRenderer.FONT_WIDTH;
        float scaleY = (usableH / rows) / FixedWidthFontRenderer.FONT_HEIGHT;

        PoseStack poseStack = matrices.getModel();
        poseStack.pushPose();

        var rightVec = new Vector3f(right.getStepX(), right.getStepY(), right.getStepZ());
        var upVec = new Vector3f(up.getStepX(), up.getStepY(), up.getStepZ());
        var forwardVec = new Vector3f(front.getStepX(), front.getStepY(), front.getStepZ());

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(new Quaternionf().setFromUnnormalized(new Matrix3f(rightVec, upVec, forwardVec)));
        poseStack.translate(0, 0, 0.5 - 0.001);

        // The owner block is the group's top-left corner; shift so the whole footprint
        // (extending right/down from here) is centered the same way a single block would be.
        poseStack.translate((blockWidth - 1) / 2f, -(blockHeight - 1) / 2f, 0);

        poseStack.translate(-usableW / 2f, usableH / 2f, 0);
        poseStack.scale(scaleX, -scaleY, 1f);

        var drawStack = new PoseStack();
        ContraptionMatrices.transform(drawStack, matrices.getViewProjection());
        ContraptionMatrices.transform(drawStack, poseStack);

        var consumer = buffer.getBuffer(RenderType.text(FixedWidthFontRenderer.FONT));
        var emitter = FixedWidthFontRenderer.toVertexConsumer(drawStack, consumer);

        FixedWidthFontRenderer.drawTerminal(
                emitter, 0, 0, terminal,
                BACKGROUND_MARGIN / scaleY, BACKGROUND_MARGIN / scaleY,
                BACKGROUND_MARGIN / scaleX, BACKGROUND_MARGIN / scaleX
        );

        poseStack.popPose();
    }
}
