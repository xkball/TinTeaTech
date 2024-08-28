package com.xkball.tin_tea_tech.mixin.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.xkball.tin_tea_tech.utils.RenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public class TTMixinRenderSky {
    
    @Shadow @Nullable private ClientLevel level;
    
    @Shadow @Nullable private VertexBuffer skyBuffer;
    
    @Shadow @Nullable private VertexBuffer starBuffer;
    
    @Inject(method = "renderSky",at = @At("HEAD"),cancellable = true)
    public void onRenderSky(PoseStack pPoseStack, Matrix4f pProjectionMatrix, float pPartialTick, Camera pCamera, boolean pIsFoggy, Runnable pSkyFogSetup, CallbackInfo ci){
        if (this.level == null || this.level.effects().skyType() != DimensionSpecialEffects.SkyType.NORMAL || this.skyBuffer == null) {
            return;
        }
        RenderUtil.renderSky((LevelRenderer)(Object)this,this.level,pPoseStack,pProjectionMatrix,pPartialTick,this.skyBuffer,this.starBuffer,pSkyFogSetup);
        ci.cancel();
    }
    
    @Redirect(method = "drawStars",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;end()Lcom/mojang/blaze3d/vertex/BufferBuilder$RenderedBuffer;"))
    public BufferBuilder.RenderedBuffer addStars(BufferBuilder buffer){
        buffer.vertex(1,1,180);
        buffer.vertex(1,-1,180);
        buffer.vertex(-1,-1,180);
        buffer.vertex(-1,1,180);
        return buffer.end();
    }
    
}
