package com.xkball.tin_tea_tech.common.cover;

import com.xkball.tin_tea_tech.api.mte.cover.Cover;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import net.minecraft.core.Direction;

public class CoverFactory {
    
    public static Cover getCover(MetaTileEntity mte, int type, Direction direction){
        if(type == 1){
            return new ItemInputCover(mte,direction);
        }
        if(type == 2){
            return new FluidInputCover(mte,direction);
        }
        throw new RuntimeException("not support cover type");
    }
}
