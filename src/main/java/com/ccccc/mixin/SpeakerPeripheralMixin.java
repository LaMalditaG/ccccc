package com.ccccc.mixin;

import com.ccccc.CCCCC;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.speaker.SpeakerPeripheral;
import dan200.computercraft.shared.peripheral.speaker.SpeakerPosition;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpeakerPeripheral.class)
public abstract class SpeakerPeripheralMixin {
    @Shadow
    public abstract SpeakerPosition getPosition() ;

    @Inject(at = @At(value = "HEAD"),method = "update")
    void update(CallbackInfo ci){
//        CCCCC.LOGGER.info(getPosition().position().toString());
    }


}
