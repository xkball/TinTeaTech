package com.xkball.tin_tea_tech.capability.item;

import com.xkball.tin_tea_tech.api.item.TTItemHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class DrawerItemHandler implements TTItemHandler {
    
    private Item item = Items.AIR;
    private int storage = 0;
    private int capacity = 0;
    
    public DrawerItemHandler(){}
    
    public DrawerItemHandler(int capacity){
        this.capacity = capacity;
    }
    
    @Override
    public void setItem(int slot, ItemStack itemStack) {
        if(slot == 0){
            this.item = itemStack.getItem();
            this.storage = Math.min(capacity,itemStack.getCount());
            onContentsChanged();
        }
    }
    
    @Override
    public CompoundTag serializeNBT() {
        var result = new CompoundTag();
        var rl = ForgeRegistries.ITEMS.getKey(item);
        result.putString("item", rl == null? "minecraft:air" : rl.toString());
        result.putInt("count",storage);
        result.putInt("capacity",capacity);
        return result;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString("item")));
        if(item == null) item = Items.AIR;
        this.storage = nbt.getInt("count");
        this.capacity = nbt.getInt("capacity");
        this.storage = Math.min(storage,capacity);
        onLoad();
    }
    
    @Override
    public int getSlots() {
        return 1;
    }
    
    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if(slot == 0) return new ItemStack(item,storage);
        return ItemStack.EMPTY;
    }
    
    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if(stack.isEmpty() || stack.hasTag()) return ItemStack.EMPTY;
        if(slot != 0 || !isItemValid(slot,stack)) return stack;
        if(item == Items.AIR || item == stack.getItem()){
            var canIn = this.capacity - this.storage;
            canIn = Math.min(canIn,stack.getCount());
            var remainingCount = stack.getCount()-canIn;
            if(!simulate){
                this.item = stack.getItem();
                this.storage = this.storage+canIn;
                onContentsChanged();
            }
            return remainingCount==0 ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(stack,remainingCount);
        }
        return stack;
    }
    
    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(slot != 0 || storage == 0) return ItemStack.EMPTY;
        var i = item;
        amount = Math.min(amount,storage);
        if(!simulate){
            storage = storage - amount;
            if(storage == 0){
                item = Items.AIR;
            }
            onContentsChanged();
        }
        return new ItemStack(i,amount);
    }
    
    @Override
    public int getSlotLimit(int slot) {
        return capacity;
    }
    
    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }
    
    protected void onLoad() {}
    
    protected void onContentsChanged() {}
    
    
    public Item getItem() {
        return item;
    }
    
    public int getStorage() {
        return storage;
    }
    
    public int getCapacity() {
        return capacity;
    }
}
