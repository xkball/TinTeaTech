package com.xkball.tin_tea_tech;

import com.xkball.tin_tea_tech.common.block.TinTeaTechBlocks;
import com.xkball.tin_tea_tech.common.item.TinTeaTechItems;
import com.xkball.tin_tea_tech.registry.AutoRegManager;
import com.xkball.tin_tea_tech.registry.TinTeaTechRegistries;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(TinTeaTech.MODID)
public class TinTeaTech {
    
    public static final String MODID = "tin_tea_tech";

    private static final Logger LOGGER = LogUtils.getLogger();
    
    public TinTeaTech(IEventBus modEventBus, ModContainer modContainer) {
        TinTeaTechBlocks.init();
        TinTeaTechItems.init();
        TinTeaTechRegistries.init(modEventBus);
        AutoRegManager.init(modContainer);
        modEventBus.addListener(this::commonSetup);
    }
    
    
    private void commonSetup(final FMLCommonSetupEvent event) {

    }
    
    
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }


    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
