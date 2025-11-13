package com.ccccc.mixin;

import com.simibubi.create.content.contraptions.Contraption;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Contraption.class)
public abstract class ContraptionMixin {
//
//
//    @Shadow
//    private Set<SuperGlueEntity> glueToRemove;
//
//    @Shadow
//    protected List<AABB> superglue;
//
//    @Shadow
//    public BlockPos anchor;
//
//    @Shadow
//    protected Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks;
//
//    @Shadow
//    protected abstract boolean customBlockRemoval(LevelAccessor world, BlockPos pos, BlockState state);
//
//    @Inject(at = @At("HEAD"), method = "addBlock")
//    private void addBlock(Level level, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair, CallbackInfo ci) {
//        CCCCC.LOGGER.info("ADD BLOCK AT {}", pos);
//    }
//
////    @Inject(method = "removeBlocksFromWorld",at = @At(
////            target = "Lnet/minecraft/core/BlockPos;offset(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/core/BlockPos;",
////            value = "INVOKE"
////    ))
////    private void removeBlocksFromWorld(Level world, BlockPos offset, CallbackInfo ci) {
////        CCCCC.LOGGER.info("ADD BLOCK AT {}");
////
////        BlockPos add = block.pos().offset(anchor)
////                .offset(offset);
////        BlockState oldState = world.getBlockState(add);
////    }
//
//
//    /**
//     * @author LaMalditaG
//     * @reason Need to modify method
//     */
//    @Overwrite
//    public void removeBlocksFromWorld(Level world, BlockPos offset) {
//        glueToRemove.forEach(glue -> {
//            superglue.add(glue.getBoundingBox()
//                    .move(Vec3.atLowerCornerOf(offset.offset(anchor))
//                            .scale(-1)));
//            glue.discard();
//        });
//
//        List<BoundingBox> minimisedGlue = new ArrayList<>();
//        for (int i = 0; i < superglue.size(); i++)
//            minimisedGlue.add(null);
//
//        for (boolean brittles : Iterate.trueAndFalse) {
//            for (Iterator<StructureTemplate.StructureBlockInfo> iterator = blocks.values()
//                    .iterator(); iterator.hasNext(); ) {
//                StructureTemplate.StructureBlockInfo block = iterator.next();
//                if (brittles != BlockMovementChecks.isBrittle(block.state()))
//                    continue;
//
//                for (int i = 0; i < superglue.size(); i++) {
//                    AABB aabb = superglue.get(i);
//                    if (aabb == null
//                            || !aabb.contains(block.pos().getX() + .5, block.pos().getY() + .5, block.pos().getZ() + .5))
//                        continue;
//                    if (minimisedGlue.get(i) == null)
//                        minimisedGlue.set(i, new BoundingBox(block.pos()));
//                    else
//                        minimisedGlue.set(i, BBHelper.encapsulate(minimisedGlue.get(i), block.pos()));
//                }
//                BlockPos add = block.pos().offset(anchor)
//                        .offset(offset);
//                if (customBlockRemoval(world, add, block.state()))
//                    continue;
//                BlockState oldState = world.getBlockState(add);
//                Block blockIn = oldState.getBlock();
//                boolean blockMismatch = block.state().getBlock() != blockIn;
//                blockMismatch &= !AllBlocks.POWERED_SHAFT.is(blockIn) || !AllBlocks.SHAFT.has(block.state());
//                if (blockMismatch)
//                    iterator.remove();
//
//                if(isComputer(oldState)){
//                    if(world.getBlockEntity(add) instanceof AbstractComputerBlockEntityMixinAccessor be){
//                        be.cCCCC$setIgnoreRemoval();
//                    }
//                }
//
//                world.removeBlockEntity(add);
//                int flags = Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_KNOWN_SHAPE
//                        | Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE;
//                if (blockIn instanceof SimpleWaterloggedBlock && oldState.hasProperty(BlockStateProperties.WATERLOGGED)
//                        && oldState.getValue(BlockStateProperties.WATERLOGGED)) {
//                    world.setBlock(add, Blocks.WATER.defaultBlockState(), flags);
//                    continue;
//                }
//                world.setBlock(add, Blocks.AIR.defaultBlockState(), flags);
//            }
//        }
//        superglue.clear();
//        for (BoundingBox box : minimisedGlue) {
//            if (box == null)
//                continue;
//            AABB bb = new AABB(box.minX(), box.minY(), box.minZ(), box.maxX() + 1, box.maxY() + 1, box.maxZ() + 1);
//            if (bb.getSize() > 1.01)
//                superglue.add(bb);
//        }
//        for (StructureTemplate.StructureBlockInfo block : blocks.values()) {
//            BlockPos add = block.pos().offset(anchor)
//                    .offset(offset);
////			if (!shouldUpdateAfterMovement(block))
////				continue;
//
//            int flags = Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL;
//            world.sendBlockUpdated(add, block.state(), Blocks.AIR.defaultBlockState(), flags);
//
//            // when the blockstate is set to air, the block's POI data is removed, but
//            // markAndNotifyBlock tries to
//            // remove it again, so to prevent an error from being logged by double-removal
//            // we add the POI data back now
//            // (code copied from ServerWorld.onBlockStateChange)
//            ServerLevel serverWorld = (ServerLevel) world;
//            PoiTypes.forState(block.state())
//                    .ifPresent(poiType -> {
//                        world.getServer()
//                                .execute(() -> {
//                                    serverWorld.getPoiManager()
//                                            .add(add, poiType);
//                                    DebugPackets.sendPoiAddedPacket(serverWorld, add);
//                                });
//                    });
//
//            world.markAndNotifyBlock(add, world.getChunkAt(add), block.state(), Blocks.AIR.defaultBlockState(), flags,
//                    512);
//            block.state().updateIndirectNeighbourShapes(world, add, flags & -2);
//        }
//        CCCCC.LOGGER.info("BLOCKS REMOVED ");
//    }
//
//    private boolean isComputer(BlockState blockState){
//        return (blockState.is(ModRegistry.Blocks.COMPUTER_NORMAL.get()));
//    }
}
