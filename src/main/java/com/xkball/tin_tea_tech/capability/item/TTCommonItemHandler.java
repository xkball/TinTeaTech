package com.xkball.tin_tea_tech.capability.item;

import com.xkball.tin_tea_tech.api.item.TTItemHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class TTCommonItemHandler extends ItemStackHandler implements TTItemHandler {
    
    public TTCommonItemHandler() {
        super();
    }
    
    public TTCommonItemHandler(int size) {
        super(size);
    }
    
    public TTCommonItemHandler(NonNullList<ItemStack> stacks) {
        super(stacks);
    }
    
    @Override
    public void setItem(int slot, ItemStack itemStack) {
        validateSlotIndex(slot);
        this.stacks.set(slot, itemStack);
    }
}
