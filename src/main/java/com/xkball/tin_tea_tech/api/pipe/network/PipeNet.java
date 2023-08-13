package com.xkball.tin_tea_tech.api.pipe.network;

import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.net.MTEPipeWithNet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public interface PipeNet {
    
    void tick(BlockPos pos);
    
    int size();
    
    Collection<MTEPipeWithNet> getConnected();
    
    Map<BlockPos,MTEPipeWithNet> getConnectedRaw();
    
    MTEPipeWithNet getCenter();
    
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
    
    default <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
        return LazyOptional.empty();
    }
    
    default boolean canCombine(PipeNet other){
        return !other.workable() || other.getClass() == this.getClass();
    }
    
    default boolean workable(){
        return true;
    }
    
    static PipeNet create(MTEPipeWithNet mte){
        return new PipeNetImpl(mte);
    }
    
}
