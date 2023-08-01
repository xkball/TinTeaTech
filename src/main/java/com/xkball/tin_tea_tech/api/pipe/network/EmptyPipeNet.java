package com.xkball.tin_tea_tech.api.pipe.network;

import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.MTEPipe;
import net.minecraft.core.BlockPos;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
public class EmptyPipeNet implements PipeNet{
    
    protected final MTEPipe center;
    
    public EmptyPipeNet(MTEPipe center) {
        this.center = center;
    }
    
    @Override
    public void tick(BlockPos pos) {
    
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    @Override
    public Collection<MTEPipe> getConnected() {
        return Collections.emptySet();
    }
    
    @Override
    public Map<BlockPos, MTEPipe> getConnectedRaw() {
        return Collections.emptyMap();
    }
    
    @Override
    public MTEPipe getCenter() {
        return center;
    }
    
    @Override
    public PipeNet combine(BlockPos other) {
        return this;
    }
    
    @Override
    public void cut(BlockPos pos) {
    
    }
    
    @Override
    public boolean workable() {
        return false;
    }
}
