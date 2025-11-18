package com.ccccc.mixin;

import com.ccccc.CCCCC;
import dan200.computercraft.shared.peripheral.generic.methods.InventoryMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(InventoryMethods.class)
public class InventoryMethodsMixin {
    @Inject(at = @At("HEAD"),method = "extractContainer")
    private static void extractContainer(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity, Direction direction, CallbackInfoReturnable<InventoryMethods.StorageWrapper> cir){
//        CCCCC.LOGGER.info("LEVEL {}",level);
//        CCCCC.LOGGER.info("POS {}",pos);
    }

}
