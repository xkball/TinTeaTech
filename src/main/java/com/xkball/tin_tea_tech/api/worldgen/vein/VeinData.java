package com.xkball.tin_tea_tech.api.worldgen.vein;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;

public record VeinData(String name,
                       int id,
                       List<ResourceLocation> level,
                       int maxY,
                       int minY,
                       DensityFunctionData density,
                       DensityFunctionData size,
                       float ridgedScale,
                       float ridgedThreshold,
                       Collection<BiPredicate<BlockState,DensityFunction.FunctionContext>> pattern,
                       Collection<BlockState> ores) {
}
