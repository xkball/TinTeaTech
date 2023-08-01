package com.xkball.tin_tea_tech.capability.steam;

import com.xkball.tin_tea_tech.api.capability.steam.ISteamHolder;
import net.minecraft.nbt.CompoundTag;

public class SteamHolder implements ISteamHolder {
    
    protected double volume;
    protected double pressure;
    protected double storage;
    protected int maxStorage;
    
    public SteamHolder(double volume,int maxStorage){
        this.volume = volume;
        this.maxStorage = maxStorage;
        this.pressure = 101f;
        this.storage = volume*pressure;
    }
    
    public SteamHolder(double volume){
        this(volume,Integer.MAX_VALUE);
    }
    
    
    @Override
    public double getPressure() {
        return pressure;
    }
    
    @Override
    public double getVolume() {
        return volume;
    }
    
    @Override
    public int add(int originalVolume, boolean simulate) {
        var b = getMaxStorage()-storage;
        if(!simulate) this.setStorage(Math.min(getStorage()+originalVolume,getMaxStorage()));
        return (int) Math.min(b,originalVolume);
    }
    
    @Override
    public int remove(int originalVolume, boolean simulate) {
        var b = storage;
        var c = Math.max(storage-originalVolume,pressure*volume);
        if(!simulate) this.setStorage(c);
        return (int) ((int) b-c);
    }
    
    @Override
    public void onChange(){
    
    }
    
    public void setVolume(double volume) {
        this.volume = volume;
        this.pressure = Math.max(101f,storage/volume);
        this.storage = volume*pressure;
    }
    
    public double getStorage() {
        return storage;
    }
    
    public void setStorage(double storage) {
        this.storage = storage;
        this.pressure = storage/volume;
    }
    
    public int getMaxStorage() {
        return maxStorage;
    }
    
    public void setMaxStorage(int maxStorage) {
        this.maxStorage = maxStorage;
    }
    
    
    @Override
    public CompoundTag serializeNBT() {
        var result = new CompoundTag();
        result.putDouble("volume",volume);
        result.putDouble("pressure",pressure);
        result.putInt("capacity",maxStorage);
        return result;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("volume")){
            volume = nbt.getDouble("volume");
            pressure = nbt.getDouble("pressure");
            maxStorage = nbt.getInt("capacity");
        }
    }
}
