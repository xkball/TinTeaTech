package com.xkball.tin_tea_tech.mixin.client;

import com.xkball.tin_tea_tech.common.player.PlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class TTMixinMinecraft {
    
    @Inject(method = "shouldEntityAppearGlowing",at = @At("RETURN"),cancellable = true)
    public void onCheckGlowing(Entity pEntity, CallbackInfoReturnable<Boolean> cir){
        //var superV = cir.getReturnValue();
        if(PlayerData.get().modeAvailable(3) && pEntity instanceof Player){
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
