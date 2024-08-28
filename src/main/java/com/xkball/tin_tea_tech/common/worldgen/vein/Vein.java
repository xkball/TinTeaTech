package com.xkball.tin_tea_tech.common.worldgen.vein;

import com.xkball.tin_tea_tech.api.worldgen.vein.DensityFunctionData;
import com.xkball.tin_tea_tech.api.worldgen.vein.VeinData;
import com.xkball.tin_tea_tech.common.item_behaviour.DensityFunctionTestItemBehaviour;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;

public class Vein {
    public static final DensityFunction Y = DensityFunctions.yClampedGradient(-2048,2048,-2048,2048);
    
    public final int id;
    public final String name;
    private final RandomSource random;
    public final VeinData veinData;
    
    private final DensityFunction density;
    private final DensityFunction size;
    private final DensityFunction ridged;
    
    public Vein(VeinData veinData, RandomSource random) {
        this.id = veinData.id();
        this.name = veinData.name();
        this.veinData = veinData;
        this.random = random;
        this.density = yAnd0Interpolatable(veinData.density().toDensityFunction(nextRandom()));
        this.size = yAnd0Interpolatable(veinData.size().toDensityFunction(nextRandom()));
        
        var ridged1 = yAnd0Interpolatable(DensityFunctionData.create(veinData.ridgedScale(), veinData.ridgedScale(), -7).toDensityFunction(nextRandom())).abs();
        var ridged2 = yAnd0Interpolatable(DensityFunctionData.create(veinData.ridgedScale(), veinData.ridgedScale(), -7).toDensityFunction(nextRandom())).abs();
        this.ridged = DensityFunctions.add(DensityFunctions.constant(veinData.ridgedThreshold()),DensityFunctions.max(ridged1,ridged2));
        
    }
    
    private RandomSource nextRandom(){
        return RandomSource.create(random.nextLong());
    }
    
    public NoiseChunk.BlockStateFiller getVeinRule(){
        return (context) -> {
            var blockState = Blocks.AIR.defaultBlockState();
            //Y过滤
            if(context.blockY()>=veinData.maxY() || context.blockY()< veinData.minY()) return blockState;
            //密度过滤
            if(!veinData.density().validator().test(density.compute(context))) return blockState;
            //if(veinData.size().validator().test(size.compute(context))) return null;
            //脊部过滤
            if(ridged.compute(context)>=0d) return blockState;
            return DensityFunctionTestItemBehaviour.doubleToGlass(density.compute(context));
        };
    }
    
    private DensityFunction yAnd0Interpolatable(DensityFunction inRange){
        return yLimitedInterpolatable(inRange, veinData.minY(),veinData.maxY(),0);
    }
    
    public static DensityFunction yLimitedInterpolatable( DensityFunction inRange, int minInclusive, int maxExclusive, int outRange){
        return DensityFunctions.interpolated(DensityFunctions.rangeChoice(Y, minInclusive, maxExclusive + 1, inRange, DensityFunctions.constant(outRange)));
    }
    
    
}
