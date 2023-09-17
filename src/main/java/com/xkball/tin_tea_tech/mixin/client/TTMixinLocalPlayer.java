package com.xkball.tin_tea_tech.mixin.client;

import com.mojang.authlib.GameProfile;
import com.xkball.tin_tea_tech.TinTeaTech;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class TTMixinLocalPlayer extends AbstractClientPlayer {
    
    public TTMixinLocalPlayer(ClientLevel pClientLevel, GameProfile pGameProfile) {
        super(pClientLevel, pGameProfile);
    }
    
    @Inject(method = "aiStep",at = @At("RETURN"))
    public void onAiStep(CallbackInfo ci){
        if(this.getDeltaMovement().length()>20) {
            var l = this.getDeltaMovement().length();
            var x = 20/l;
            this.setDeltaMovement(this.getDeltaMovement().multiply(x,x,x));
        }
        var f = TinTeaTech.ClientForgeEvents.speedFactor;
        this.setDeltaMovement(this.getDeltaMovement().multiply(f,f,f));
        var l = this.getDeltaMovement().length();
        if(l > 20){
            var x = 20/l;
            this.setDeltaMovement(this.getDeltaMovement().multiply(x,x,x));
        }
    }
}
