package com.xkball.tin_tea_tech.mixin.client;

import com.xkball.tin_tea_tech.common.player.PlayerData;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class TTMixinKeyboardInput extends Input {
    
    @Inject(method = "tick",at = @At("RETURN"))
    public void onTick(boolean pIsSneaking, float pSneakingSpeedMultiplier, CallbackInfo ci){
        if(PlayerData.get().controlled){
            this.leftImpulse = PlayerData.get().leftImpulse;
            this.forwardImpulse = PlayerData.get().forwardImpulse;
            if (pIsSneaking) {
                this.leftImpulse *= pSneakingSpeedMultiplier;
                this.forwardImpulse *= pSneakingSpeedMultiplier;
            }
        }
//        else if(PlayerData.get().modeAvailable(7)){
//            if(pIsSneaking){
//                this.leftImpulse /= pSneakingSpeedMultiplier;
//                this.forwardImpulse /= pSneakingSpeedMultiplier;
//            }
//            this.leftImpulse = this.leftImpulse * TinTeaTech.ClientForgeEvents.speedFactor;
//            this.forwardImpulse = this.forwardImpulse * TinTeaTech.ClientForgeEvents.speedFactor;
//        }
    }
    
}
