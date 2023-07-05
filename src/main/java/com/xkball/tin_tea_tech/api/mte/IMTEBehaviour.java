package com.xkball.tin_tea_tech.api.mte;

import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import net.minecraft.core.BlockPos;

public interface IMTEBehaviour {
    MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te);
}
