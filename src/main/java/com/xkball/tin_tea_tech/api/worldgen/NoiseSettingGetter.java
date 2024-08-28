package com.xkball.tin_tea_tech.api.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.List;

public interface NoiseSettingGetter {
   
     //NoiseGeneratorSettings tin_tea_tech$get(ResourceLocation resourceLocation);
    
    List<ResourceKey<NoiseGeneratorSettings>> tin_tea_tech$getNoiseLevelList();
    
    void tin_tea_tech$setResourceLocation(ResourceLocation resourceLocation);
    
    ResourceLocation tin_tea_tech$getResourceLocation();
}
