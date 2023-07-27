package com.xkball.tin_tea_tech.common.player;

import net.minecraft.world.item.ItemStack;

public interface IExtendedPlayer {
    AdditionalInventory getAdditionalInventory();
    
    ItemStack getHeadItem();
    
    PlayerData getPlayerData();
}
