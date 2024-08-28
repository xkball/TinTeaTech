package com.xkball.tin_tea_tech.common.worldgen.vein;

import com.xkball.tin_tea_tech.api.event.AppendNoiseChunkRuleEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GenVeins {
    
    @SubscribeEvent
    public static void onAppendNoiseChunkRule(AppendNoiseChunkRuleEvent event){
        var level = event.level;
        var veins = Veins.levelVeinMap.get(level);
        if(veins.isEmpty()) return;
        for(var vein : veins){
            event.builder.add(vein.getVeinRule());
        }
    }
}
