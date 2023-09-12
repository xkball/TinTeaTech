package com.xkball.tin_tea_tech.common.event;

import com.xkball.tin_tea_tech.common.player.PlayerData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EntityEventListener {
    
    @SubscribeEvent
    public static void onArmorChange(LivingEquipmentChangeEvent event){
        var entity = event.getEntity();
        if(!(entity instanceof Player) || event.getSlot() != EquipmentSlot.HEAD) return;
        var from = event.getFrom();
        var to = event.getTo();
        var data = PlayerData.get((Player) entity);
        data.updateDateFromItem(from,to);
    }
}
