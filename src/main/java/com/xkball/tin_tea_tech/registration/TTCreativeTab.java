package com.xkball.tin_tea_tech.registration;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.common.item.itemblock.ScaffoldingBlockItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.ParametersAreNullableByDefault;

//@Mod.EventBusSubscriber
@ParametersAreNullableByDefault
@MethodsReturnNonnullByDefault
public class TTCreativeTab {
    
    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event != null && event.getTab() == AutoRegManager.getRegistryObject(TTBuildingBlockTab.class).get()) {
           // event.accept(Items.APPLE.getDefaultInstance());
        }
        
    }
    @AutomaticRegistration
    @I18N(chinese = TinTeaTech.MOD_NAME_CHINESE+": 建筑方块",english = TinTeaTech.MOD_NAME+": Building Blocks")
    public static class TTBuildingBlockTab extends CreativeModeTab{
        
        private static final ResourceLocation backgroundLocation = new ResourceLocation("textures/gui/container/creative_inventory/tab_items.png");
        
        public TTBuildingBlockTab() {
            super(CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.REDSTONE_BLOCKS)
                    .icon(() ->
                            ((Item)AutoRegManager.getRegistryObject(ScaffoldingBlockItem.class).get()).getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        for(var clazz : AutoRegManager.itemTabMap.get(AutoRegManager.fromClassName(TTBuildingBlockTab.class))){
                            output.accept((Item)AutoRegManager.getRegistryObject(clazz).get());
                        }
                    }
                    ));
        }
        
        @Override
        public Component getDisplayName() {
            return Component.translatable("creative_tab.tin_tea_tech.building_block_tab");
        }
        
        
        @Override
        public ResourceLocation getBackgroundLocation() {
            return backgroundLocation;
        }
        
        @Override
        public boolean shouldDisplay() {
            return true;
        }
    }
    
}
