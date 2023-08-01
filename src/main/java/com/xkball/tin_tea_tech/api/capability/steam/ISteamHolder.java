package com.xkball.tin_tea_tech.api.capability.steam;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface ISteamHolder extends INBTSerializable<CompoundTag> {
    
    double getPressure();
    double getVolume();
    
    //return added
    int add(int originalVolume,boolean simulate);
    int remove(int originalVolume,boolean simulate);
    
    void onChange();
}
