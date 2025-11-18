package com.ccccc.mixin;

import dan200.computercraft.shared.peripheral.diskdrive.DiskDriveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DiskDriveBlockEntity.class)
public abstract class DiskDriveBlockEntityMixin extends BlockEntity {


    public DiskDriveBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Shadow
    abstract void updateMedia();

    /**
     * @author LaMalditaG
     * @reason Avoid crash when assembling in contraption
     */
    @Overwrite
    public void setChanged(){
        if (level != null && !level.isClientSide){
            updateMedia();
            super.setChanged();
        }
    }
}
