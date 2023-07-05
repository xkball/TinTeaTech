package com.xkball.tin_tea_tech.common.tile_entity;

import com.xkball.tin_tea_tech.common.blocks.te.TTTileEntityBlock;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TTTileEntityBase extends BlockEntity {
    
    final MetaTileEntity mte;
    
    public TTTileEntityBase(BlockPos pos, BlockState blockState) {
        super(AutoRegManager.TILE_ENTITY_BASE.get(), pos, blockState);
        if(blockState.getBlock() instanceof TTTileEntityBlock teBlock){
            this.mte = MetaTileEntity.mteMap.get(teBlock.getMteName()).newMetaTileEntity(pos,this);
        }
        else {
            throw new RuntimeException("");
        }
    }
    
    
}
