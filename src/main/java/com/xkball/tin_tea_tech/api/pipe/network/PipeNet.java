package com.xkball.tin_tea_tech.api.pipe.network;

import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.MTEPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.Collection;
import java.util.Map;

public interface PipeNet {
    
    void tick(BlockPos pos);
    
    int size();
    
    Collection<MTEPipe> getConnected();
    
    Map<BlockPos,MTEPipe> getConnectedRaw();
    
    MTEPipe getCenter();
    
    //实在不知道用不用的到
    @SuppressWarnings("UnusedReturnValue")
    PipeNet combine(BlockPos other);
    
    void cut(BlockPos pos);
    
    default void checkNet(boolean force){}
    
    default CompoundTag save(BlockPos pos){
        return new CompoundTag();
    }
    
    default void load(CompoundTag tag){
    
    }
    
    default boolean canCombine(PipeNet other){
        return !other.workable() || other.getClass() == this.getClass();
    }
    
    default boolean workable(){
        return true;
    }
    
    static PipeNet create(MTEPipe mte){
        return new PipeNetImpl(mte);
    }
    
}