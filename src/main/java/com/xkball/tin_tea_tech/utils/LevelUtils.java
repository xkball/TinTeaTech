package com.xkball.tin_tea_tech.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

public class LevelUtils {
    
    public static void dropItem(Level level, BlockPos pos, IItemHandler itemHandler){
        for(int i=0;i<itemHandler.getSlots();i++){
            var item = itemHandler.getStackInSlot(i);
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item);
        }
    }
    
    public static void dropItem(Player player,Level level, BlockPos pos, IItemHandler itemHandler){
        for(int i=0;i<itemHandler.getSlots();i++){
            var item = itemHandler.extractItem(i,64,false);
            if(item.isEmpty()) continue;
            if(putItemToMainHand(player,item)){
                continue;
            }
            if(!player.addItem(item)){
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item);
            }
        }
    }
    
    public static boolean putItemToMainHand(Player player, ItemStack item){
        if(!player.getMainHandItem().isEmpty()) return false;
        player.getInventory().setItem(player.getInventory().selected,item);
        return true;
    }
}
