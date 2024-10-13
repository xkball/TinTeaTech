package com.xkball.tin_tea_tech.common.mte.ticker;

import com.xkball.tin_tea_tech.api.annotation.NonNullByDefault;
import com.xkball.tin_tea_tech.api.block.te.TTTileEntityBase;
import com.xkball.tin_tea_tech.api.mte.IMTEBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@NonNullByDefault
public class MTETicker implements BlockEntityTicker<TTTileEntityBase> {
    
    private final List<IMTEBehaviour<?>> tickBehaviours;
    private List<IMTEBehaviour<?>> needInitBehaviours;
    
    public MTETicker(List<IMTEBehaviour<?>> tickBehaviours, List<IMTEBehaviour<?>> needInitBehaviours) {
        this.tickBehaviours = tickBehaviours;
        this.needInitBehaviours = needInitBehaviours;
    }
    
    @Override
    public void tick(Level level, BlockPos pos, BlockState state, TTTileEntityBase blockEntity) {
        if(needInitBehaviours != null){
            needInitBehaviours.forEach(IMTEBehaviour::firstTick);
            needInitBehaviours = null;
        }
        tickBehaviours.forEach(IMTEBehaviour::tick);
    }
}
