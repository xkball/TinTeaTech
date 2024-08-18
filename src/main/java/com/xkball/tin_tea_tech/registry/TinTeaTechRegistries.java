package com.xkball.tin_tea_tech.registry;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.reg.RegItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber
public class TinTeaTechRegistries {
    
    public static final DeferredRegister<Item> ITEM = DeferredRegister.create(Registries.ITEM,TinTeaTech.MODID);
    public static final DeferredRegister<Block> BLOCK = DeferredRegister.create(Registries.BLOCK,TinTeaTech.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,TinTeaTech.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,TinTeaTech.MODID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE,TinTeaTech.MODID);
    
    
    public static void init(IEventBus bus) {
        ITEM.register(bus);
        BLOCK.register(bus);
        CREATIVE_TAB.register(bus);
        BLOCK_ENTITY_TYPE.register(bus);
        DATA_COMPONENT.register(bus);
    }
    
    @SubscribeEvent
    public static void creativeTab(BuildCreativeModeTabContentsEvent event) {
        RegItem.REG_ITEM_POOL.values()
                .stream()
                .filter(item -> item.getTab() != null && item.getTab().value() == event.getTab())
                .forEach(event::accept);
    }
}
