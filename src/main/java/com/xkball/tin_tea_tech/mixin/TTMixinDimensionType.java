package com.xkball.tin_tea_tech.mixin;

import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionType.class)
public class TTMixinDimensionType {
    
    @Inject(method = "timeOfDay",at = @At("HEAD"),cancellable = true)
    public void onGetTimeOfDay(long pDayTime, CallbackInfoReturnable<Float> cir){
        cir.cancel();
        float t = (pDayTime%24000f)/24000f-0.25f;
        t = t<0?1+t:t;
        cir.setReturnValue(t);
    }
}
