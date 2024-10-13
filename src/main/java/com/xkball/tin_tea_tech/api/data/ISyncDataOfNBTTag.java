package com.xkball.tin_tea_tech.api.data;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

@SuppressWarnings("unused")
public interface ISyncDataOfNBTTag {
    
    default Codec<?> saveCodec(){
        return null;
    }
    
    default Codec<?> server2clientCodec(){
        return null;
    }
    
    default Codec<?> client2serverCodec(){
        return null;
    }
    
    default void save(CompoundTag tag, HolderLookup.Provider registries){
    
    }
    
    default void writeS2C(CompoundTag tag, HolderLookup.Provider registries){
    
    }
    
    default void writeC2S(CompoundTag tag, HolderLookup.Provider registries){
    
    }
    
    default void load(CompoundTag tag, HolderLookup.Provider registries){
    
    }
    
    default void readS2C(CompoundTag tag, HolderLookup.Provider registries){
    
    }
    
    default void readC2S(CompoundTag tag, HolderLookup.Provider registries){
    
    }
}
