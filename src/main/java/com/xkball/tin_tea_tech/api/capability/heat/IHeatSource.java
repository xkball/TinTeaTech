package com.xkball.tin_tea_tech.api.capability.heat;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IHeatSource extends INBTSerializable<CompoundTag> {
    //返回当前温度
    //[-1000,+∞)
    //对玩家可见
    int heatValue();
    
    //温度直接需要的缓存值
    //对玩家不直接可见
    int heatBuff();
    
    //两个温度间的差值
    //对玩家不直接可见
    int heatGap();
    
    //升温所需倍率
    //对玩家不直接可见
    int getCapacity();
    
    void setCapacity(int capacity);
    
    void increase(int heat);
    
    void decrease(int heat);
    
    void reset(int heatValue,int heatBuff);
    
    IHeatSource combine(IHeatSource another);
}
