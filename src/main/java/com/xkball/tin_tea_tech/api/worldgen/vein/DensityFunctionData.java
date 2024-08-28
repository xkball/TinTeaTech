package com.xkball.tin_tea_tech.api.worldgen.vein;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.DoublePredicate;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record DensityFunctionData(double xzScale, double yScale, NormalNoise.NoiseParameters noiseParameters,
                                  DoublePredicate validator) {
    
    public DensityFunctionData(NormalNoise.NoiseParameters noiseParameters){
        this(1d,1d,noiseParameters,(f)->true);
    }
    
    public static DensityFunctionData largerThan(DensityFunctionData source,float threshold){
        return new DensityFunctionData(source.xzScale,source.yScale,source.noiseParameters,(f) -> f>threshold);
    }
    
    public static DensityFunctionData create(double xzScale,double yScale,int octave){
        return new DensityFunctionData(xzScale,yScale,new NormalNoise.NoiseParameters(octave,1d),(d)->true);
    }
    
    public DensityFunction toDensityFunction(RandomSource randomSource){
        var holder = new DensityFunction.NoiseHolder(Holder.direct(noiseParameters),NormalNoise.create(randomSource,noiseParameters));
        return new DensityFunctions.Noise(holder,xzScale,yScale);
    }
    
    
//暂时用不上了
//    public record Noise(DensityFunction.NoiseHolder noise, double xzScale, double yScale) implements DensityFunction {
//        public static final MapCodec<DensityFunctions.Noise> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208798_) -> p_208798_.group(NoiseHolder.CODEC.fieldOf("noise").forGetter(DensityFunctions.Noise::noise), Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctions.Noise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctions.Noise::yScale)).apply(p_208798_, DensityFunctions.Noise::new));
//        public static final KeyDispatchDataCodec<DensityFunctions.Noise> CODEC = DensityFunctions.makeCodec(DATA_CODEC);
//
//        @Override
//        public double compute(DensityFunction.FunctionContext pContext) {
//            return this.noise.getValue((double)pContext.blockX() * this.xzScale, (double)pContext.blockY() * this.yScale, (double)pContext.blockZ() * this.xzScale);
//        }
//
//        @Override
//        public void fillArray(double[] pArray, DensityFunction.ContextProvider pContextProvider) {
//            pContextProvider.fillAllDirectly(pArray, this);
//        }
//
//        @Override
//        public DensityFunction mapAll(DensityFunction.Visitor pVisitor) {
//            return pVisitor.apply(new Noise(pVisitor.visitNoise(this.noise), this.xzScale, this.yScale));
//        }
//
//        @Override
//        public double minValue() {
//            return -this.maxValue();
//        }
//
//        @Override
//        public double maxValue() {
//            return this.noise.maxValue();
//        }
//
//        @Override
//        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
//            return CODEC;
//        }
//    }
    
}
