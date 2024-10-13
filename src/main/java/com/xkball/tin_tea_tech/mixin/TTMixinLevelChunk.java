package com.xkball.tin_tea_tech.mixin;

import com.xkball.tin_tea_tech.api.block.te.TTTileEntityBase;
import com.xkball.tin_tea_tech.common.mte.ticker.CreateMTETickerContext;
import com.xkball.tin_tea_tech.utils.ModUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class TTMixinLevelChunk {
    
    @Shadow @Final private Level level;
    @Unique
    private TTTileEntityBase tin_tea_tech$contextTileEntity = null;
    
    @Inject(method = "updateBlockEntityTicker",at = @At("HEAD"))
    public <T extends BlockEntity> void onUpdateBlockEntityTicker(T blockEntity, CallbackInfo ci){
        if(blockEntity instanceof TTTileEntityBase) tin_tea_tech$contextTileEntity = (TTTileEntityBase)blockEntity;
    }
    
    @SuppressWarnings("unchecked")
    @ModifyVariable(method = "updateBlockEntityTicker",at = @At("STORE"))
    public <T extends BlockEntity> BlockEntityTicker<T> onCreateBlockEntityTicker(BlockEntityTicker<T> value){
        if(value instanceof CreateMTETickerContext<T> context){
            if(tin_tea_tech$contextTileEntity != null && tin_tea_tech$contextTileEntity.haveMTE(ModUtils.getMTEType(context.mteLoc()))) return (BlockEntityTicker<T>) tin_tea_tech$contextTileEntity.getTicker(this.level);
        }
        return value;
    }
}
