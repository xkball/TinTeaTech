package com.xkball.tin_tea_tech.data.tag;

import com.xkball.tin_tea_tech.TinTeaTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class TTBlockTags {
    
    public static final Map<String, TagKey<Block>> tagMap = new HashMap<>();
    
    public static TagKey<Block> create(String name){
        var result =  TagKey.create(Registries.BLOCK, TinTeaTech.ttResource(name));
        tagMap.put(name,result);
        return result;
    }
}
