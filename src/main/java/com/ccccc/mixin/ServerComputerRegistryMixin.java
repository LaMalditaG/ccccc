package com.ccccc.mixin;

import com.ccccc.CCCCC;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerComputerRegistry.class)
public class ServerComputerRegistryMixin {

    @Shadow
    private final Int2ObjectMap<ServerComputer> computersByInstanceId = new Int2ObjectOpenHashMap<>();

    @Shadow
//    private final Map<UUID, ServerComputer> computersByInstanceUuid = new HashMap<>();
    private int nextInstanceId;

    @Inject(at = @At(value = "INVOKE", target = "Ldan200/computercraft/shared/computer/core/ServerComputer;unload()V"), method = "update")
    void unload(CallbackInfo ci) {
        CCCCC.LOGGER.info("unload");
    }

    @Inject(at = @At(value = "HEAD"),method = "add")
    void add(ServerComputer computer, CallbackInfo ci){
        CCCCC.LOGGER.info("add");
    }

    @Inject(at = @At(value = "HEAD"),method = "remove")
    void remove(ServerComputer computer, CallbackInfo ci){
        CCCCC.LOGGER.info("remove");
    }

    @Inject(at = @At(value = "HEAD"),method = "close")
    void close(CallbackInfo ci){
        CCCCC.LOGGER.info("close");
    }

//    @Inject(at = @At(value = "HEAD"),method = "update")
//    void update(CallbackInfo ci){
//
//        if(computersByInstanceId.size()>0){
//            CCCCC.LOGGER.info("update {}",computersByInstanceId.keySet().toArray()[0]);
//        }else{
//            CCCCC.LOGGER.info("update");
//        }
//    }
}
