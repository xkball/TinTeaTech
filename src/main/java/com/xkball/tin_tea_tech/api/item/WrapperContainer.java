package com.xkball.tin_tea_tech.api.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WrapperContainer implements Container {
    private final IItemHandler itemHandler;
    
    public WrapperContainer(IItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }
    
    
    @Override
    public int getContainerSize() {
        return itemHandler.getSlots();
    }
    
    @Override
    public boolean isEmpty() {
        for(int i=0;i<itemHandler.getSlots();i++){
            if(!itemHandler.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }
    
    @Override
    public ItemStack getItem(int pSlot) {
        return itemHandler.getStackInSlot(pSlot);
    }
    
    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return itemHandler.extractItem(pSlot,pAmount,false);
    }
    
    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return itemHandler.extractItem(pSlot,64,false);
    }
    
    @Override
    public void setItem(int pSlot, ItemStack pStack) {
    
    }
    
    @Override
    public void setChanged() {
    }
    
    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
    
    @Override
    public void clearContent() {
    
    }
}
