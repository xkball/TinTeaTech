package com.xkball.tin_tea_tech.utils;

import com.mojang.blaze3d.platform.InputConstants;
import com.xkball.tin_tea_tech.TinTeaTech;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.KeyboardInput;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientUtils {
    
    
    public static boolean isShiftDown() {
        assert TinTeaTech.isClient();
        return isKeyDown(340) || isKeyDown(344);
    }
    
    public static boolean isCtrlDown() {
        assert TinTeaTech.isClient();
        return isKeyDown(341) || isKeyDown(345);
    }
    
    public static boolean isKeyDown(int key){
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(),key);
    }
    
//    @SubscribeEvent
//    public static void onKeyboardInput(InputEvent.Key event){
//    }
    
    public static void drawStringLeft(GuiGraphics guiGraphics,String text,int screenW,int y,int color){
        var font = Minecraft.getInstance().font;
        var w = font.width(text);
        guiGraphics.drawString(font,text,screenW-w,y,color,false);
    }
}
