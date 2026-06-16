package com.ccccc.peripherals.monitor;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Map;

/**
 * Computes a contraption-mounted monitor block's place within its merged multiblock group,
 * purely from the contraption's static block structure (no networking/persistent state
 * involved). Both the server (to size/own the shared terminal) and the client (to know which
 * slice of the group it's rendering) run this same deterministic computation, so they always
 * agree on group boundaries without needing to sync layout metadata.
 */
public final class MonitorGeometry {
    private MonitorGeometry() {
    }

    public record Result(BlockPos anchor, int blockWidth, int blockHeight, int xIndex, int yIndex, Direction front, Direction right, Direction up) {
        public boolean isOwner() {
            return xIndex == 0 && yIndex == 0;
        }
    }

    public static boolean isMonitorBlock(Block block) {
        return block == ModRegistry.Blocks.MONITOR_NORMAL.get() || block == ModRegistry.Blocks.MONITOR_ADVANCED.get();
    }

    public static Result compute(MovementContext ctx, int maxWidth, int maxHeight) {
        var state = ctx.state;
        Direction orientation = state.getValue(MonitorBlock.ORIENTATION);
        Direction facing = state.getValue(MonitorBlock.FACING);
        Direction front = getFront(orientation, facing);
        Direction right = getRight(facing);
        Direction up = getUp(orientation, facing);

        var blocks = ctx.contraption.getBlocks();
        Block block = state.getBlock();

        int left = extent(blocks, ctx.localPos, right.getOpposite(), block, orientation, facing, maxWidth - 1);
        int rightExtent = extent(blocks, ctx.localPos, right, block, orientation, facing, maxWidth - 1 - left);
        int top = extent(blocks, ctx.localPos, up, block, orientation, facing, maxHeight - 1);
        int bottom = extent(blocks, ctx.localPos, up.getOpposite(), block, orientation, facing, maxHeight - 1 - top);

        int blockWidth = left + rightExtent + 1;
        int blockHeight = top + bottom + 1;

        BlockPos anchor = ctx.localPos.relative(right.getOpposite(), left).relative(up, top);

        return new Result(anchor, blockWidth, blockHeight, left, top, front, right, up);
    }

    private static int extent(Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks, BlockPos from, Direction dir,
                               Block block, Direction orientation, Direction facing, int max) {
        int count = 0;
        BlockPos pos = from;
        while (count < max) {
            BlockPos next = pos.relative(dir);
            var info = blocks.get(next);
            if (info == null || !matches(info.state(), block, orientation, facing)) break;
            pos = next;
            count++;
        }
        return count;
    }

    private static boolean matches(BlockState state, Block block, Direction orientation, Direction facing) {
        return state.getBlock() == block
                && state.hasProperty(MonitorBlock.ORIENTATION) && state.getValue(MonitorBlock.ORIENTATION) == orientation
                && state.hasProperty(MonitorBlock.FACING) && state.getValue(MonitorBlock.FACING) == facing;
    }

    private static Direction getFront(Direction orientation, Direction facing) {
        return orientation == Direction.NORTH ? facing : orientation;
    }

    private static Direction getRight(Direction facing) {
        return facing.getCounterClockWise();
    }

    private static Direction getUp(Direction orientation, Direction facing) {
        if (orientation == Direction.NORTH) return Direction.UP;
        return orientation == Direction.DOWN ? facing : facing.getOpposite();
    }
}
