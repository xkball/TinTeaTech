package com.xkball.tin_tea_tech.mixin;

import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.common.player.PlayerData;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class TTMixinItemBlock {
    
    
    @Inject(method = "updatePlacementContext",at = @At("HEAD"),cancellable = true)
    public void onUpdatePlaceContext(BlockPlaceContext context, CallbackInfoReturnable<BlockPlaceContext> cir){
        if(context.getPlayer() != null && PlayerData.get(context.getPlayer()).buildingMode){
            var originalPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
            var con = LevelUtils.useConnection(context);
            var pos = tin_tea_tech$getPos(originalPos,con);
            if(context.getLevel().getBlockState(pos).canBeReplaced()){
                cir.setReturnValue(BlockPlaceContext.at(context,pos,context.getClickedFace()));
                cir.cancel();
            }
            else {
                cir.setReturnValue(null);
                cir.cancel();
            }
        }
    }
    
    @Unique
    public BlockPos tin_tea_tech$getPos(BlockPos blockPos, Connections connections){
        var v = connections.getRelativePos();
        return blockPos.offset(v.getX(),v.getY(),v.getZ());
    }
    
}
