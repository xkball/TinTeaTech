package com.xkball.tin_tea_tech.api.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;

public interface TTItemHandler extends IItemHandler, INBTSerializable<CompoundTag> {
    default boolean isEmpty(){
        for(int i=0;i<getSlots();i++){
            if(!getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }
    
    void setItem(int slot, ItemStack itemStack);
    
    default int countInSlot(int slot){
        return getStackInSlot(slot).getCount();
    }
}
