package com.xkball.tin_tea_tech.mixin.worldgen;

import com.xkball.tin_tea_tech.api.worldgen.NoiseSettingGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(NoiseGeneratorSettings.class)
public class TTMixinNoiseGenSettings implements NoiseSettingGetter {
    
    @Shadow @Final public static ResourceKey<NoiseGeneratorSettings> OVERWORLD;
    @Shadow @Final public static ResourceKey<NoiseGeneratorSettings> LARGE_BIOMES;
    @Shadow @Final public static ResourceKey<NoiseGeneratorSettings> AMPLIFIED;
    @Shadow @Final public static ResourceKey<NoiseGeneratorSettings> NETHER;
    @Shadow @Final public static ResourceKey<NoiseGeneratorSettings> END;
    @Shadow @Final public static ResourceKey<NoiseGeneratorSettings> CAVES;
    @Shadow @Final public static ResourceKey<NoiseGeneratorSettings> FLOATING_ISLANDS;
    @Unique
    private static final List<ResourceKey<NoiseGeneratorSettings>> tin_tea_tech$noiseLevelList = List.of(OVERWORLD,LARGE_BIOMES,AMPLIFIED,NETHER,END,CAVES,FLOATING_ISLANDS);
    
    @Unique
    @Nullable
    public ResourceLocation tin_tea_tech$level = null;
//    @Inject(method = "bootstrap",at = @At("RETURN"))
//    private static void onBootstrap(BootstapContext<NoiseGeneratorSettings> pContext, CallbackInfo ci){
//        var table = pContext.lookup(Registries.NOISE_SETTINGS);
//        var overWorld = table.getOrThrow(OVERWORLD).value();
//        var largeBiomes = table.getOrThrow(LARGE_BIOMES).value();
//        var amplified = table.getOrThrow(AMPLIFIED).value();
//        var nether = table.getOrThrow(NETHER).value();
//        var end = table.getOrThrow(END).value();
//        var caves = table.getOrThrow(CAVES).value();
//        var floatingIsland = table.getOrThrow(FLOATING_ISLANDS).value();
//        tin_tea_tech$noiseLevelMap.put(AppendNoiseChunkRuleEvent.OVER_WORLD,overWorld);
//        tin_tea_tech$noiseLevelMap.put(AppendNoiseChunkRuleEvent.LARGE_BIOMES,largeBiomes);
//        tin_tea_tech$noiseLevelMap.put(AppendNoiseChunkRuleEvent.AMPLIFIED,amplified);
//        tin_tea_tech$noiseLevelMap.put(AppendNoiseChunkRuleEvent.NETHER,nether);
//        tin_tea_tech$noiseLevelMap.put(AppendNoiseChunkRuleEvent.END,end);
//        tin_tea_tech$noiseLevelMap.put(AppendNoiseChunkRuleEvent.CAVES,caves);
//        tin_tea_tech$noiseLevelMap.put(AppendNoiseChunkRuleEvent.FLOATING_ISLANDS,floatingIsland);
//        ((NoiseSettingGetter)(Object)overWorld).tin_tea_tech$setResourceLocation(AppendNoiseChunkRuleEvent.OVER_WORLD);
//        ((NoiseSettingGetter)(Object)largeBiomes).tin_tea_tech$setResourceLocation(AppendNoiseChunkRuleEvent.LARGE_BIOMES);
//        ((NoiseSettingGetter)(Object)amplified).tin_tea_tech$setResourceLocation(AppendNoiseChunkRuleEvent.AMPLIFIED);
//        ((NoiseSettingGetter)(Object)nether).tin_tea_tech$setResourceLocation(AppendNoiseChunkRuleEvent.NETHER);
//        ((NoiseSettingGetter)(Object)end).tin_tea_tech$setResourceLocation(AppendNoiseChunkRuleEvent.END);
//        ((NoiseSettingGetter)(Object)caves).tin_tea_tech$setResourceLocation(AppendNoiseChunkRuleEvent.CAVES);
//        ((NoiseSettingGetter)(Object)floatingIsland).tin_tea_tech$setResourceLocation(AppendNoiseChunkRuleEvent.FLOATING_ISLANDS);
//
//    }
    
    
//    @Override
//    @Unique
//    public NoiseGeneratorSettings tin_tea_tech$get(ResourceLocation resourceLocation){
//        return tin_tea_tech$noiseLevelMap.get(resourceLocation);
//    }
    
    @Override
    @Unique
    public List<ResourceKey<NoiseGeneratorSettings>> tin_tea_tech$getNoiseLevelList(){
        return tin_tea_tech$noiseLevelList;
    }
    
    @Override
    public void tin_tea_tech$setResourceLocation(ResourceLocation resourceLocation) {
        tin_tea_tech$level = resourceLocation;
    }
    
    @Override
    public ResourceLocation tin_tea_tech$getResourceLocation() {
        return tin_tea_tech$level;
    }
}
