package com.xkball.tin_tea_tech.capability.energy;

import net.minecraftforge.energy.EnergyStorage;

public class TTEnergyStorage extends EnergyStorage {
    
    public TTEnergyStorage(int capacity) {
        super(capacity);
    }
    
    public TTEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }
    
    public TTEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }
    
    public TTEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }
    
    public void setCapacity(int capacity){
        this.capacity = capacity;
        this.energy = Math.max(0,Math.min(capacity,energy));
    }
    
    public void setMaxReceive(int maxReceive){
        this.maxReceive = maxReceive;
    }
    
    public void setMaxExtract(int maxExtract){
        this.maxExtract = maxExtract;
    }
    
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        var result =  super.receiveEnergy(maxReceive, simulate);
        if(result != 0) onEnergyChange();
        return result;
    }
    
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        var result = super.extractEnergy(maxExtract, simulate);
        if(result != 0) onEnergyChange();
        return result;
    }
    
    public void setEnergy(int energy){
        this.energy = energy;
    }
    
    public void onEnergyChange(){
    
    }
}
