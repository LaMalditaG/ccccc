package com.ccccc.mixin;

import com.ccccc.CCCCC;
import com.ccccc.accesors.AbstractComputerBlockEntityMixinAccesor;
import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity;
import io.netty.util.SuppressForbidden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(AbstractComputerBlockEntity.class)
public abstract class AbstractComputerBlockEntityMixin implements AbstractComputerBlockEntityMixinAccesor {
    @Shadow
    public abstract int getComputerID();

    @Unique
    private static final Set<Integer> ignoreRemovalIDS = new HashSet<>();

    @Override
    public void cCCCC$setIgnoreRemoval(){
        int id = getComputerID();
        CCCCC.LOGGER.info("CHECKING {}",id);
        if(id < 0) return;
        ignoreRemovalIDS.add(id);
        CCCCC.LOGGER.info("IGNORING {}",id);
    }

	@Inject(at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/BlockEntity;setRemoved()V"
    ),method = "setRemoved", cancellable = true)
	private void setRemoved(CallbackInfo info) {
        CCCCC.LOGGER.info(Integer.toString(this.getComputerID()));
        CCCCC.LOGGER.info(this.toString());

        if(ignoreRemovalIDS.contains(getComputerID())){
            info.cancel();
            CCCCC.LOGGER.info("CANCELLING");
            return;
        }
        CCCCC.LOGGER.info("SHIT");
	}


    @Inject(at = @At("HEAD"), method="unload")
    private void unload(CallbackInfo info){
        CCCCC.LOGGER.info("FUCK");
    }
}