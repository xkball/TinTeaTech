package com.xkball.tin_tea_tech.mixin.client;

import com.mojang.authlib.GameProfile;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import com.xkball.tin_tea_tech.network.packet.SyncLatitudeAndLongitudePacket;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.stats.StatsCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

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
