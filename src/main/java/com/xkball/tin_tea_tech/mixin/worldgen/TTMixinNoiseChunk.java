package com.xkball.tin_tea_tech.mixin.worldgen;

import com.google.common.collect.ImmutableList;
import com.xkball.tin_tea_tech.api.event.AppendNoiseChunkRuleEvent;
import com.xkball.tin_tea_tech.api.worldgen.NoiseSettingGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.material.MaterialRuleList;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseChunk.class)
public abstract class TTMixinNoiseChunk {
    
    
    @Shadow public NoiseChunk.BlockStateFiller blockStateRule;
    
    @Inject(method = "<init>",
            at = @At("RETURN"))
    public void onInit(int pCellCountXZ, RandomState pRandom, int p_224345_, int p_224346_, NoiseSettings pNoiseSettings, DensityFunctions.BeardifierOrMarker pBeardifier, NoiseGeneratorSettings pNoiseGeneratorSettings, Aquifer.FluidPicker pFluidPicker, Blender pBlendifier, CallbackInfo ci){
        
        ImmutableList.Builder<NoiseChunk.BlockStateFiller> builder = ImmutableList.builder();
        //builder.add(this.blockStateRule);
        @SuppressWarnings("DataFlowIssue")
        var level = ((NoiseSettingGetter)(Object)pNoiseGeneratorSettings).tin_tea_tech$getResourceLocation();
        if(level == null){
            if(pNoiseSettings.equals(NoiseSettings.NETHER_NOISE_SETTINGS)){
                level = AppendNoiseChunkRuleEvent.NETHER;
            }
            else if(pNoiseSettings.equals(NoiseSettings.END_NOISE_SETTINGS)){
                level = AppendNoiseChunkRuleEvent.END;
            }
            else if(pNoiseSettings.equals(NoiseSettings.CAVES_NOISE_SETTINGS)){
                level = AppendNoiseChunkRuleEvent.CAVES;
            } else if (pNoiseSettings.equals(NoiseSettings.FLOATING_ISLANDS_NOISE_SETTINGS)) {
                level = AppendNoiseChunkRuleEvent.FLOATING_ISLANDS;
            }
            else {
                level = AppendNoiseChunkRuleEvent.OVER_WORLD;
            }
            ((NoiseSettingGetter)(Object)pNoiseGeneratorSettings).tin_tea_tech$setResourceLocation(level);
        }
        MinecraftForge.EVENT_BUS.post(new AppendNoiseChunkRuleEvent((NoiseChunk)(Object)this,builder,pNoiseGeneratorSettings,level));
        this.blockStateRule = new MaterialRuleList(builder.build());
    }
}
