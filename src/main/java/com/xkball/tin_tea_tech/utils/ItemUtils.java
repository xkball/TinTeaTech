package com.xkball.tin_tea_tech.utils;

import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.common.item.TTCommonItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemUtils {
    
    @SafeVarargs
    public static boolean hasTagInHand(Player player, TagKey<Item>... tagKeys){
        var i1 = player.getItemInHand(InteractionHand.MAIN_HAND);
        for(var tag : tagKeys){
            if(i1.is(tag)) return true;
        }
        var i2 = player.getItemInHand(InteractionHand.OFF_HAND);
        for(var tag : tagKeys){
            if(i2.is(tag)) return true;
        }
        
        return false;
    }
    
    public static String toString(Item item){
        var rl = ForgeRegistries.ITEMS.getKey(item);
        return rl == null?"minecraft:air" : rl.toString();
    }
    
    public static Item fromString(String s){
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
        return item==null? Items.AIR:item;
    }
    
    public static boolean transportItem(IItemHandler from,IItemHandler to,int maxStackSize){
        var exd = 0;
        for(int i=0;i<from.getSlots();i++){
            var canEx = from.extractItem(i,64,true);
            for(int j=0;j<to.getSlots();j++){
                var notIn = to.insertItem(j,canEx,true);
                if(notIn.isEmpty()){
                    var ex = from.extractItem(i,canEx.getCount(),false);
                    to.insertItem(j,ex,false);
                    exd++;
                    break;
                }
                else {
                    var exCount = canEx.getCount()-notIn.getCount();
                    if(exCount>0){
                        var ex = from.extractItem(i,exCount,false);
                        to.insertItem(j,ex,false);
                        exd++;
                        break;
                    }
                }
            }
            if(exd>=maxStackSize) return true;
        }
        return false;
    }
    
    public static int transportItemWithCount(IItemHandler from,IItemHandler to,int maxStackSize){
        var exd = 0;
        for(int i=0;i<from.getSlots();i++){
            var canEx = from.extractItem(i,64,true);
            for(int j=0;j<to.getSlots();j++){
                var notIn = to.insertItem(j,canEx,true);
                if(notIn.isEmpty()){
                    var ex = from.extractItem(i,canEx.getCount(),false);
                    to.insertItem(j,ex,false);
                    exd++;
                    break;
                }
                else {
                    var exCount = canEx.getCount()-notIn.getCount();
                    if(exCount>0){
                        var ex = from.extractItem(i,exCount,false);
                        to.insertItem(j,ex,false);
                        exd++;
                        break;
                    }
                }
            }
            if(exd>=maxStackSize) return 0;
        }
        return maxStackSize-exd;
    }
    
    public static boolean holdingItem(Player player, Class<? extends IItemBehaviour> clazz){
        var left = player.getItemInHand(InteractionHand.OFF_HAND);
        if(left.getItem() instanceof TTCommonItem commonItem){
            if(clazz.isAssignableFrom(commonItem.getItemBehaviour().getClass())) {
                return true;
            }
        }
        var right = player.getItemInHand(InteractionHand.MAIN_HAND);
        if(right.getItem() instanceof TTCommonItem commonItem){
            return clazz.isAssignableFrom(commonItem.getItemBehaviour().getClass());
        }
        return false;
    }
}
