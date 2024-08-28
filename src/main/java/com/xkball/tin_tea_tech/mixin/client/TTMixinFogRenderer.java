package com.xkball.tin_tea_tech.mixin.client;

import net.minecraft.client.renderer.FogRenderer;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public class TTMixinFogRenderer {
    
    @Redirect(method = "setupColor",at = @At(value = "INVOKE",target = "Lorg/joml/Vector3f;dot(Lorg/joml/Vector3fc;)F",remap = false))
    private static float onHandleRedFog(Vector3f instance, Vector3fc v){
        return 0f;
    }
}
