package com.xkball.tin_tea_tech;

import com.mojang.logging.LogUtils;
import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.client.key.vanilla.VanillaInputHandler;
import com.xkball.tin_tea_tech.client.render.MTERender;
import com.xkball.tin_tea_tech.common.player.PlayerData;
import com.xkball.tin_tea_tech.config.TTConfig;
import com.xkball.tin_tea_tech.data.DataGen;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import com.xkball.tin_tea_tech.registration.TTRegistration;
import com.xkball.tin_tea_tech.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

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
    public static RandomSource random = RandomSource.createNewThreadLocalInstance();
    
    //可能在不同维度上撞  但是我决定忽略这种可能
    public static final Map<BlockPos, Connections> lastPlace = new HashMap<>();
    
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
        //Bindings.getForgeBus().get().addListener();
    }
    
    public void commonSetup(final FMLCommonSetupEvent event) {
        var timer = new Timer();
        event.enqueueWork(TTNetworkHandler::init);
        LOGGER.debug(MOD_NAME + " common setup completed in "+timer.timeNS()+" ns.");
    }

  

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        var timer = new Timer();
        
        LOGGER.debug(MOD_NAME + " setup on server starting completed in "+timer.timeNS()+" ns.");
    }
    
    @SubscribeEvent
    public static void onServerClose(ServerStoppingEvent event){
        var timer = new Timer();
        lastPlace.clear();
        LOGGER.debug(MOD_NAME + " handle server stopping completed in "+timer.timeNS()+" ns.");
    }
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            ticks++;
            if(ticks%100==0){
                lastPlace.clear();
            }
        }
    }
    
    public static boolean isClient(){
        return FMLEnvironment.dist.isClient();
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
        
        @SubscribeEvent
        public static void onRegKey(RegisterKeyMappingsEvent event){
            event.register(VanillaInputHandler.OPEN_HOLO_GlASS_KEY);
            event.register(VanillaInputHandler.PRINT_NBT);
        }
        
        @SubscribeEvent
        public static void onRegOverlay(RegisterGuiOverlaysEvent event){
            for(var gui : AutoRegManager.overlays){
                event.registerAboveAll(AutoRegManager.fromClassName(gui.getClass()),gui);
            }
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
            for(var rl : AutoRegManager.models){
               registerAdditional.register(new ResourceLocation(rl));
            }
        }
        
    }
    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientForgeEvents{
        
        public static float speedFactor = 1;
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event){
            if(event.phase == TickEvent.Phase.START){
                clientTicks++;
                if(Minecraft.getInstance().options.keyShift.isDown()){
                    if(PlayerData.get().modeAvailable(7)){
                        speedFactor += 0.01;
                        speedFactor = Math.min(speedFactor,5f);
                    }
                }
                else if(speedFactor>1){
                    speedFactor = Math.max(speedFactor-0.01f,1f);
                }
                
            }
        }
        
      
    }
}
