package com.xkball.tin_tea_tech.utils;

import com.xkball.tin_tea_tech.api.annotation.DataField;
import net.minecraft.world.item.ItemStack;

public class TestData {
    
    @DataField
    public int aaa = 0;
    
    @DataField
    public ItemStack itemStack = ItemStack.EMPTY;
    
}
