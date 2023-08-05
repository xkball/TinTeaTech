package com.xkball.tin_tea_tech.common.player;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdditionalInventory extends SimpleContainer {
    
    //0为头后栏
    //1-9暂未决定用处 大概是给放工具的
    public AdditionalInventory() {
        super(10);
    }
    
    @Override
    public ItemStack getItem(int pIndex) {
        return super.getItem(0);
    }
}
