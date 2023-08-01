package com.xkball.tin_tea_tech.capability.heat;

import com.xkball.tin_tea_tech.api.capability.heat.IHeatSource;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public class HeatHandler implements IHeatSource {
    
    int heat = 1000;
    
    int heatBuff = 0;
    
    //实际上是个系数
    //找不到好的命名了
    int capacity = 1;
    
    public HeatHandler(){
    }
    
    public HeatHandler(int heat){
        this.heat = heat;
    }
    
    public HeatHandler(int heat,int heatBuff){
        this.heat = heat;
        this.increase(heatBuff);
    }
    
    
    @Override
    public int heatValue() {
        return heat-1000;
    }
    
    @Override
    public int heatBuff() {
        return heatBuff;
    }
    
    @Override
    public int heatGap() {
        var abs = Math.abs(heat-1000);
        if (heat < 1000) {
            return ((int) Math.pow(4,Math.log(abs+1)+2)+1)*capacity;
        }
        return (abs*20+1)*capacity;
    }
    
    @Override
    public void increase(int heat) {
        if(heat == 0) return;
        if(heat<0) decrease(-heat);
        var buff = heatBuff+heat;
        var gap = heatGap();
        if(buff>gap){
            buff = buff-gap;
            this.heat++;
            this.heatBuff=0;
            increase(buff);
        }
        else {
            this.heatBuff = buff;
        }
    }
    
    @Override
    public void decrease(int heat) {
        if(heat == 0) return;
        if(heat<0) increase(-heat);
        var buff = heatBuff - heat;
        if(buff<0){
            this.heat--;
            this.heatBuff=0;
            var gap=heatGap();
            buff = -buff;
            decrease(buff-gap);
        }
        else {
            this.heatBuff = buff;
        }
    }
    
    @Override
    public void reset(int heatValue, int heatBuff) {
        this.heat = heatValue+1000;
        this.heatBuff = heatBuff;
    }
    
    //并不精准
    //为了防止玩家偷鸡 所以直接取较低温作为结果
    @Override
    public IHeatSource combine(IHeatSource another) {
        var result = this.heat<another.heatValue() ? this : another;
        result.setCapacity(this.getCapacity()+another.getCapacity());
        return result;
    }
    
    @Override
    public CompoundTag serializeNBT() {
        var result = new CompoundTag();
        result.putInt("h",heat);
        result.putInt("hb",heatBuff);
        result.putInt("c",capacity);
        return result;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("h")) this.heat = nbt.getInt("h");
        if(nbt.contains("hb")) this.heatBuff = nbt.getInt("hb");
        if(nbt.contains("c")) this.capacity = nbt.getInt("c");
    }
    
    @Override
    public int getCapacity() {
        return capacity;
    }
    
    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    @Override
    public String toString() {
        return "heatValue: "+(heat-1000)+" heatBuff: "+heatBuff+" capacity: "+capacity;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeatHandler that = (HeatHandler) o;
        return heat == that.heat && heatBuff == that.heatBuff && capacity == that.capacity;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(heat, heatBuff, capacity);
    }
}
