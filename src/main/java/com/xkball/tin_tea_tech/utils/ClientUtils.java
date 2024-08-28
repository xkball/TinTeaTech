package com.xkball.tin_tea_tech.utils;

import com.mojang.blaze3d.platform.InputConstants;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.astronomy.AstronomyAccess;
import com.xkball.tin_tea_tech.api.astronomy.LevelModel;
import com.xkball.tin_tea_tech.api.data.LatitudeAndLongitudeAccess;
import com.xkball.tin_tea_tech.client.shape.Point2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientUtils {
    
    public static Point2D getLatitudeAndLongitude(){
        assert Minecraft.getInstance().level!=null;
        return ((LatitudeAndLongitudeAccess)Minecraft.getInstance().level).tin_tea_tech$getLatitudeAndLongitude(Minecraft.getInstance().player);
    }
    
    public static LevelModel getLevelModel(){
        assert Minecraft.getInstance().level!=null;
        return((AstronomyAccess)Minecraft.getInstance().level).tin_tea_tech$getLevelModel();
    }
    
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
        guiGraphics.fill(screenW-w-2,y-2,screenW,y+10,-1873784752);
        guiGraphics.drawString(font,text,screenW-w,y,color,false);
    }
}
