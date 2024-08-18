package com.xkball.tin_tea_tech.config;

import com.xkball.tin_tea_tech.TinTeaTech;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;


@EventBusSubscriber(modid = TinTeaTech.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{


    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    
    }
}
