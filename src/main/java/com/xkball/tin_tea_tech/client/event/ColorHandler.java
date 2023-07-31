package com.xkball.tin_tea_tech.client.event;

import com.xkball.tin_tea_tech.common.item.itemblock.MTEItemBlock;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.MTEItemPipe;
import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.MTEPipe;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ColorHandler {
    
    
    @SubscribeEvent
    public static void onRegItemColor(RegisterColorHandlersEvent.Item event){
        var items = MetaTileEntity.mteMap.values().stream().filter(
                (mte) -> mte instanceof MTEPipe).map(
                (mte) -> mte.getBlock().asItem()
        ).toArray(MTEItemBlock[]::new);
        for(var item : items){
            var i = item.defaultColor();
            event.register(((pStack, pTintIndex) -> i),item);
        }
    }
}
