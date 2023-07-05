package com.xkball.tin_tea_tech.common.blocks.te;

import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNullableByDefault;

@ParametersAreNullableByDefault
@MethodsReturnNonnullByDefault
public class TTTileEntityBlock extends BaseEntityBlock {
    
    final String mteName;
    protected TTTileEntityBlock(String mteName) {
        super(BlockBehaviour.Properties.of());
        this.mteName = mteName;
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new TTTileEntityBase(pos,blockState);
    }
    
    public String getMteName() {
        return mteName;
    }
    
}
