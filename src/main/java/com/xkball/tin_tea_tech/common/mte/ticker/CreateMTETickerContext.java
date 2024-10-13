package com.xkball.tin_tea_tech.common.mte.ticker;

import com.xkball.tin_tea_tech.api.annotation.NonNullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

@NonNullByDefault
public record CreateMTETickerContext<T extends BlockEntity>(ResourceLocation mteLoc) implements BlockEntityTicker<T> {
    
    @Override
    public void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
    
    }
}
