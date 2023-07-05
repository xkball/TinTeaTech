package com.xkball.tin_tea_tech;

import com.mojang.logging.LogUtils;
import com.xkball.tin_tea_tech.config.TTConfig;
import com.xkball.tin_tea_tech.data.DataGen;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.registration.TTRegistration;
import com.xkball.tin_tea_tech.utils.Timer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TinTeaTech.MODID)
@Mod.EventBusSubscriber
public class TinTeaTech
{
    public static final String MODID = "tin_tea_tech";
    public static final String MOD_NAME = "Tin Tea Tech";
    
    public static final String MOD_NAME_CHINESE = "锡茶科技";
    private static final Logger LOGGER = LogUtils.getLogger();
    

    public TinTeaTech() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(TTCreativeTab::addCreative);
        modEventBus.addListener(DataGen::onGatherData);
        TTRegistration.init(modEventBus);
        AutoRegManager.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TTConfig.SPEC);
    }
    
    public void commonSetup(final FMLCommonSetupEvent event) {
        var timer = new Timer();
        
        LOGGER.debug(MOD_NAME + " common setup completed in "+timer.timeNS()+" ns.");
    }

  

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        var timer = new Timer();
        
        LOGGER.debug(MOD_NAME + " setup on server starting completed in "+timer.timeNS()+" ns.");
    }
    
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            var timer = new Timer();
            
            LOGGER.debug(MOD_NAME + " setup on client completed in "+timer.timeNS()+" ns.");
        }
    }
}
