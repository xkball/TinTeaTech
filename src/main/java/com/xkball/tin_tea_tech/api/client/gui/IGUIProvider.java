package com.xkball.tin_tea_tech.api.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IGUIProvider {
    int getID();
    Component getName();
    
    @OnlyIn(Dist.CLIENT)
    Screen newInstance(CompoundTag data);
    
    void save(CompoundTag data);
}
