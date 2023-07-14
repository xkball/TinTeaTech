package com.xkball.tin_tea_tech;

import com.mojang.logging.LogUtils;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.client.render.MTERender;
import com.xkball.tin_tea_tech.config.TTConfig;
import com.xkball.tin_tea_tech.data.DataGen;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import com.xkball.tin_tea_tech.registration.TTRegistration;
import com.xkball.tin_tea_tech.utils.Timer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
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
    public static final String FLAME_REACTION_MODID = "flamereaction";
    
    public static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static final BlockPos ORIGIN = new BlockPos(0,0,0);
    
    public static int ticks = 0;
    public static int clientTicks = 0;
    
    public TinTeaTech() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        modEventBus.addListener(this::commonSetup);
        //好像用不到的样子
        //modEventBus.addListener(TTCreativeTab::addCreative);
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
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            ticks++;
        }
    }
    
    public static ResourceLocation ttResource(String path){
        return new ResourceLocation(MODID,path);
    }
    
    public static ResourceLocation frcResource(String path){
        return new ResourceLocation(FLAME_REACTION_MODID,path);
    }
    
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            var timer = new Timer();
            event.enqueueWork(() -> BlockEntityRenderers.register(AutoRegManager.TILE_ENTITY_BASE.get(), MTERender::new));
            LOGGER.debug(MOD_NAME + " setup on client completed in "+timer.timeNS()+" ns.");
        }
        
        //test only
        
//        @SubscribeEvent
//        public static void onBaking(ModelEvent.BakingCompleted bakingCompleted){
//            var map = bakingCompleted.getModels();
//            var list = map.keySet().stream().filter(
//                    (r) -> r.getNamespace().equals(TinTeaTech.FLAME_REACTION_MODID)
//                            || r.getNamespace().equals(TinTeaTech.MODID)).toList();
//            for(var rl : list){
//                LOGGER.info(rl.toString());
//            }
//        }
        
        @SubscribeEvent
        public static void onRegModel(ModelEvent.RegisterAdditional registerAdditional) {
           // registerAdditional.register(new ResourceLocation(TinTeaTech.FLAME_REACTION_MODID,"block/solid_fuel_burning_box_on"));
            for(var clazz : AutoRegManager.modelClasses){
                var an = clazz.getAnnotation(Model.class);
                for(var r : an.resources()){
                    registerAdditional.register(new ResourceLocation(r));
                }
            }
        }
        
    }
    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientForgeEvents{
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event){
            if(event.phase == TickEvent.Phase.START){
                clientTicks++;
            }
        }
        
      
    }
}
