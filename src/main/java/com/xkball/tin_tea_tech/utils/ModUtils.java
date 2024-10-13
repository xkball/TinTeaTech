package com.xkball.tin_tea_tech.utils;

import com.xkball.tin_tea_tech.api.mte.IMTEBehaviour;
import com.xkball.tin_tea_tech.api.mte.MTEType;
import com.xkball.tin_tea_tech.registry.TinTeaTechRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public class ModUtils {
    
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends IMTEBehaviour<T>> MTEType<T> getMTEType(ResourceLocation location){
        return (MTEType<T>) TinTeaTechRegistries.MTE_TYPE_REGISTRY.get(location);
    }
    
    public static void updateBlockEntityTicker(@Nullable Level level, BlockPos pos){
        if(level == null) return;
        var chunk = level.getChunk(pos);
        var be = level.getBlockEntity(pos);
        if(be == null) return;
        if(chunk instanceof LevelChunk levelChunk) levelChunk.updateBlockEntityTicker(be);
    }
}
