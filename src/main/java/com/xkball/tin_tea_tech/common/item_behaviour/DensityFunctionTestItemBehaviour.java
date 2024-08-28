package com.xkball.tin_tea_tech.common.item_behaviour;

import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.api.worldgen.vein.DensityFunctionData;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

@AutomaticRegistration
@AutomaticRegistration.Item
@CreativeTag(tab = TTCreativeTab.TTMiscTab.class)
//@Model(resources = {"tin_tea_tech:item/icon"})
@I18N(chinese = "密度函数测试物品",english = "DensityFunctionTestItem")
public class DensityFunctionTestItemBehaviour implements IItemBehaviour {
    
    @Override
    public int stackSizeLimit() {
        return 1;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        var item = pPlayer.getItemInHand(pUsedHand);
        var tag = item.getOrCreateTag();
        var xz = tag.contains("xz")?tag.getDouble("xz"):1d;
        var y = tag.contains("y")?tag.getDouble("y"):1d;
        var octave = tag.contains("octave")?tag.getInt("octave"):-5;
        var densityFunction = DensityFunctionData.create(xz,y,octave)
                .toDensityFunction(RandomSource.create());
        var pos = pPlayer.getOnPos();
        for(int i=0;i<40;i++){
            for(int j=0;j<200;j++){
                for(int k=0;k<40;k++){
                    var toSet = pos.offset(i-20,j-100,k-20);
                    if(toSet.getY()<=-64 || toSet.getY()>=384) continue;
                    var value = densityFunction.compute(at(toSet));
                    pLevel.setBlock(toSet, doubleToGlass(value),3);
                    //else if(value > 0.0d) pLevel.setBlock(toSet,Blocks.RED_STAINED_GLASS.defaultBlockState(), 3);
                }
            }
        }
        
        return IItemBehaviour.super.use(pLevel, pPlayer, pUsedHand);
    }
    
    
    public static BlockState doubleToGlass(double value){
        if(value > 0.9d) return Blocks.GLASS.defaultBlockState();
        else if(value > 0.8d) return Blocks.WHITE_STAINED_GLASS.defaultBlockState();
        else if(value > 0.7d) return Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState();
        else if(value > 0.6d) return Blocks.GRAY_STAINED_GLASS.defaultBlockState();
        else if(value > 0.5d) return Blocks.CYAN_STAINED_GLASS.defaultBlockState();
        else if(value > 0.4d) return Blocks.BLUE_STAINED_GLASS.defaultBlockState();
        else if(value > 0.3d) return Blocks.LIME_STAINED_GLASS.defaultBlockState();
        else if(value > 0.2d) return Blocks.GREEN_STAINED_GLASS.defaultBlockState();
        else if(value > 0.1d) return Blocks.CYAN_STAINED_GLASS.defaultBlockState();
        return Blocks.AIR.defaultBlockState();
    }
    
    public static DensityFunction.FunctionContext at(BlockPos pos){
        return new DensityFunction.FunctionContext() {
            @Override
            public int blockX() {
                return pos.getX();
            }
            
            @Override
            public int blockY() {
                return pos.getY();
            }
            
            @Override
            public int blockZ() {
                return pos.getZ();
            }
        };
    }
}
