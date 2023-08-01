package com.xkball.tin_tea_tech.capability.item;

import com.xkball.tin_tea_tech.api.capability.item.TTItemHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.EmptyHandler;

import java.util.function.Supplier;

public class TTEmptyHandler extends EmptyHandler implements TTItemHandler {
    
    public static final TTEmptyHandler INSTANCE = new TTEmptyHandler();
    public static final Supplier<TTItemHandler> get = () -> INSTANCE;
    @Override
    public void setItem(int slot, ItemStack itemStack) {
    
    }
    
    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
    
    }
}
