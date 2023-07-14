package com.xkball.tin_tea_tech.capability.item;

import com.mojang.logging.LogUtils;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.item.TTItemHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

//大小不可变的物品栏实现
//区分输入输出和缓存
public class TTIOItemHandler extends ItemStackHandler implements TTItemHandler {
    
    protected int inputs;
    
    protected int outputs;
    
    //暂时不明用途
    protected int buff;
    
    private boolean init = false;
    
    public TTIOItemHandler create(int inputs, int outputs){
        return new TTIOItemHandler(inputs,outputs,0);
    }
    
    public TTIOItemHandler create(int capacity){
        return new TTIOItemHandler(capacity,capacity,0);
    }
    
    
    public TTIOItemHandler(int inputs, int outputs, int buff) {
        super(inputs+outputs+buff);
        this.inputs = inputs;
        this.outputs = outputs;
        this.buff = buff;
    }
    
    
    
    @Override
    public void setSize(int size) {
        if(!init){
            super.setSize(size);
            init = true;
            return;
        }
        LogUtils.getLogger().warn("Someone tried to change the size of a Tin Tea Tech item handler." +
                "Tin Tea Tech not support this operation." +
                "From: " + TinTeaTech.STACK_WALKER.getCallerClass());
    }
    
    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        //仅可以向输入槽插入物品
        if(slot>= inputs){
            return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }
    
    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        //仅可以向输出槽提取物品
        if(slot < inputs || slot >= inputs+outputs ){
            return ItemStack.EMPTY;
        }
        return super.extractItem(slot, amount, simulate);
    }
    
    @Override
    public CompoundTag serializeNBT() {
        var result =  super.serializeNBT();
        result.putInt("inputs_slots",inputs);
        result.putInt("outputs_slots",outputs);
        result.putInt("buff_slots",buff);
        return result;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.inputs = nbt.getInt("inputs_slots");
        this.outputs = nbt.getInt("outputs_slots");
        this.buff = nbt.getInt("buff_slots");
        super.deserializeNBT(nbt);
    }
    
    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        
        return slot < inputs && super.isItemValid(slot, stack);
    }
}
