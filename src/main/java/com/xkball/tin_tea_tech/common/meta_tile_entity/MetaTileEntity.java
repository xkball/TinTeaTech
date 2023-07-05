package com.xkball.tin_tea_tech.common.meta_tile_entity;

import com.xkball.tin_tea_tech.api.mte.IMTEBehaviour;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

//第一次创建MTE的时候Pos和te都为null
public abstract class MetaTileEntity implements IMTEBehaviour {
    public static Map<String,MetaTileEntity> mteMap = new HashMap<>();
    
    public MetaTileEntity(BlockPos pos, TTTileEntityBase te){
    
    }
    
}
