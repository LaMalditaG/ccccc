package com.ccccc.mixin;

import com.ccccc.accessors.ServerComputerMixinAccessor;
import dan200.computercraft.shared.computer.core.ServerComputer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerComputer.class)
public abstract class ServerComputerMixin implements ServerComputerMixinAccessor {
    @Shadow
    protected abstract void tickServer();

    @Shadow
    public abstract boolean isOn();

    @Shadow
    public abstract void keepAlive();

    @Unique
    public void cCCCC$tickActor(){
        tickServer();
        keepAlive();
    }
}
