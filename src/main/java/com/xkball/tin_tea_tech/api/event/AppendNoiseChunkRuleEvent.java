package com.xkball.tin_tea_tech.api.event;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

public class AppendNoiseChunkRuleEvent extends Event {
    
    public static final ResourceLocation OVER_WORLD = new ResourceLocation("overworld");
    public static final ResourceLocation LARGE_BIOMES = new ResourceLocation("large_biomes");
    public static final ResourceLocation AMPLIFIED = new ResourceLocation("amplified");
    public static final ResourceLocation NETHER = new ResourceLocation("nether");
    public static final ResourceLocation END = new ResourceLocation("end");
    public static final ResourceLocation CAVES = new ResourceLocation("caves");
    public static final ResourceLocation FLOATING_ISLANDS = new ResourceLocation("floating_islands");
    
    
    public final ImmutableList.Builder<NoiseChunk.BlockStateFiller> builder;
    public final NoiseChunk noiseChunk;
    public final NoiseGeneratorSettings noiseGeneratorSettings;
    @Nullable
    public final ResourceLocation level;
    
    public AppendNoiseChunkRuleEvent(NoiseChunk noiseChunk, ImmutableList.Builder<NoiseChunk.BlockStateFiller> builder, NoiseGeneratorSettings noiseGeneratorSettings, @Nullable ResourceLocation level) {
        this.builder = builder;
        this.noiseChunk = noiseChunk;
        this.noiseGeneratorSettings = noiseGeneratorSettings;
        this.level = level;
    }
    
    @Override
    public boolean isCancelable() {
        return false;
    }
    
}
