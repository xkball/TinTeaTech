package com.xkball.tin_tea_tech.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.xkball.tin_tea_tech.TinTeaTech.MODID;

@Mod.EventBusSubscriber
public class TTRegistration {
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,MODID);
    
 
    public static final Item.Properties itemProperty = new Item.Properties().fireResistant().setNoRepair();
    public static void init(IEventBus bus){
        
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(bus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(bus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(bus);
        
        BLOCK_ENTITY_TYPE.register(bus);
    }
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderSetup(FMLClientSetupEvent event){
        //还是不行 还得改json...
//        //虽然弃用了 但是好像还是能用
//        //不想改json了
//        //noinspection deprecation
//        ItemBlockRenderTypes.setRenderLayer((Block) AutoRegManager.getRegistryObject(ScaffoldingBlock.class), RenderType.cutout());
    }
}
