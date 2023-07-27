package com.xkball.tin_tea_tech.data.tag;

import com.xkball.tin_tea_tech.TinTeaTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public class TTItemTags {
    
    public static final Map<String,TagKey<Item>> tagMap = new HashMap<>();
    
    public static TagKey<Item> create(String name){
        var result =  TagKey.create(Registries.ITEM, TinTeaTech.ttResource(name));
        tagMap.put(name,result);
        return result;
    }
    
    public static TagKey<Item> get(String name){
        return tagMap.get(name);
    }
}
