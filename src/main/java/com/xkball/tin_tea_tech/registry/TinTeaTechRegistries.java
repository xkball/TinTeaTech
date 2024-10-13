package com.xkball.tin_tea_tech.registry;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.mte.MTEType;
import com.xkball.tin_tea_tech.api.reg.RegItem;
import com.xkball.tin_tea_tech.utils.VanillaUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = TinTeaTech.MODID, bus = EventBusSubscriber.Bus.MOD)
public class TinTeaTechRegistries {
    
    public static final ResourceKey<Registry<MTEType<?>>> MTE_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(VanillaUtils.modRL("meta_tile_entity"));
    public static final Registry<MTEType<?>> MTE_TYPE_REGISTRY = new RegistryBuilder<>(MTE_TYPE_REGISTRY_KEY).sync(true).create();
    
    public static final DeferredRegister<Item> ITEM = DeferredRegister.create(Registries.ITEM,TinTeaTech.MODID);
    public static final DeferredRegister<Block> BLOCK = DeferredRegister.create(Registries.BLOCK,TinTeaTech.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,TinTeaTech.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,TinTeaTech.MODID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE,TinTeaTech.MODID);
    public static final DeferredRegister<MTEType<?>> MTE = DeferredRegister.create(MTE_TYPE_REGISTRY,TinTeaTech.MODID);
    
    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> BLOCK_TAB = CREATIVE_TAB.register("blocks",() -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tin_tea_tech.blocks"))
            .icon(Items.APPLE::getDefaultInstance)
            .withTabsBefore(CreativeModeTabs.FOOD_AND_DRINKS, CreativeModeTabs.INGREDIENTS, CreativeModeTabs.SPAWN_EGGS)
            .build());
    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> MISC_TAB = CREATIVE_TAB.register("misc",() -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tin_tea_tech.misc"))
            .icon(Items.APPLE::getDefaultInstance)
            .withTabsBefore(CreativeModeTabs.FOOD_AND_DRINKS, CreativeModeTabs.INGREDIENTS, CreativeModeTabs.SPAWN_EGGS)
            .build());
    
    public static void init(IEventBus bus) {
        ITEM.register(bus);
        BLOCK.register(bus);
        CREATIVE_TAB.register(bus);
        BLOCK_ENTITY_TYPE.register(bus);
        DATA_COMPONENT.register(bus);
        MTE.register(bus);
    }
    
    @SubscribeEvent
    public static void creativeTab(BuildCreativeModeTabContentsEvent event) {
        RegItem.REG_ITEM_POOL.values()
                .stream()
                .filter(item -> item.getTab() != null && item.getTab().value() == event.getTab())
                .forEach(event::accept);
    }
    
    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event){
        event.register(MTE_TYPE_REGISTRY);
    }
}
