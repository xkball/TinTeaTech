package com.xkball.tin_tea_tech.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class Helper {
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static LocalPlayer getLocalPlayer(){
        return Minecraft.getInstance().player;
    }
}
