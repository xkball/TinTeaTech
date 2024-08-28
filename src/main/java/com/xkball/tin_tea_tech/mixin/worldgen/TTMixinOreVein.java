package com.xkball.tin_tea_tech.mixin.worldgen;

import com.xkball.tin_tea_tech.common.item_behaviour.DensityFunctionTestItemBehaviour;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.OreVeinifier;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OreVeinifier.class)
public abstract class TTMixinOreVein {
    
    @Inject(method = "create",at = @At("HEAD"),cancellable = true)
    private static void onGenEnd(DensityFunction veinToggle, DensityFunction vienRidged, DensityFunction veinGap, PositionalRandomFactory pPositionalRandomFactory, CallbackInfoReturnable<NoiseChunk.BlockStateFiller> cir){
        BlockState glass = Blocks.GLASS.defaultBlockState();
        NoiseChunk.BlockStateFiller result = (p_209666_) -> {
            double d0 = veinToggle.compute(p_209666_);
            return null;
            //return DensityFunctionTestItemBehaviour.doubleToGlass(d0);
//            int i = p_209666_.blockY();
//            OreVeinifier.VeinType oreveinifier$veintype = d0 > 0.0D ? OreVeinifier.VeinType.COPPER : OreVeinifier.VeinType.IRON;
//            double d1 = Math.abs(d0);
//            int j = oreveinifier$veintype.maxY - i;
//            int k = i - oreveinifier$veintype.minY;
//            if (k >= 0 && j >= 0) {
//                int l = Math.min(j, k);
//                double d2 = Mth.clampedMap(l, 0.0D, 20.0D, -0.2D, 0.0D);
//                //控制密度?
//                if (d1 + d2 < (double)0.4F) {
//                    return null;
//                } else {
//                    RandomSource randomsource = pPositionalRandomFactory.at(p_209666_.blockX(), i, p_209666_.blockZ());
//                    if (randomsource.nextFloat() > 0.7F) {
//                        return null;
//                    } else if (vienRidged.compute(p_209666_) >= 0.0D) {
//                        return null;
//                    } else {
//                        return glass;
//                    }
//                }
//            } else {
//                return null;
//            }
        };
        cir.setReturnValue(result);
        cir.cancel();
    }
}
