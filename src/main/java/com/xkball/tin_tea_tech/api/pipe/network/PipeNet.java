package com.xkball.tin_tea_tech.api.pipe.network;

import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.MTEPipe;
import net.minecraft.core.BlockPos;

import java.util.Collection;
import java.util.Map;

public interface PipeNet {
    
    void tick();
    
    int size();
    
    Collection<MTEPipe> getConnected();
    
    Map<BlockPos,MTEPipe> getConnectedRaw();
    
    MTEPipe getCenter();
    
    PipeNet combine(BlockPos other);
    
    static PipeNet create(MTEPipe mte){
        return new PipeNetImpl(mte);
    }
}
