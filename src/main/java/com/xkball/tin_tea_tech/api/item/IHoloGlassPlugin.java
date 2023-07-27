package com.xkball.tin_tea_tech.api.item;

import com.xkball.tin_tea_tech.common.item.TTCommonItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface IHoloGlassPlugin {
    int mode();
    Component buttonText();
    
    static int getMode(ItemStack stack){
        if(stack.getItem() instanceof IHoloGlassPlugin h){
            return h.mode();
        }
        if (stack.getItem() instanceof TTCommonItem ti && ti.getItemBehaviour() instanceof IHoloGlassPlugin h) {
            return h.mode();
        }
        return -1;
    }
}
