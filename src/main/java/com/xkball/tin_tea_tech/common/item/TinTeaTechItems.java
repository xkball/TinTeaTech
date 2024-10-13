package com.xkball.tin_tea_tech.common.item;

import com.xkball.tin_tea_tech.api.reg.RegItem;
import com.xkball.tin_tea_tech.registry.TinTeaTechRegistries;
import com.xkball.tin_tea_tech.utils.VanillaUtils;
import net.minecraft.world.item.Item;

public class TinTeaTechItems {
    
    public static final RegItem<IconItem> THE_ICON = new RegItem<>("icon",() -> new IconItem(new Item.Properties()))
            .setI18n("icon","测试物品")
            .setCreativeTab(TinTeaTechRegistries.MISC_TAB)
            .setModelLocation(VanillaUtils.modRL("icon"));
    
    
    //仅用于触发类加载
    public static void init(){}
}
