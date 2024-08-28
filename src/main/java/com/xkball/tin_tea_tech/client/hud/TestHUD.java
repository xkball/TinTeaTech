package com.xkball.tin_tea_tech.client.hud;

import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.common.player.PlayerData;
import com.xkball.tin_tea_tech.utils.ClientUtils;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import com.xkball.tin_tea_tech.utils.TTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@AutomaticRegistration
public class TestHUD implements IGuiOverlay {
    
    private static final int white = ColorUtils.getColor(255,255,255,255);
    private static final int black = ColorUtils.getColor(0,0,0,255);
    
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if(Minecraft.getInstance().level == null){
            return;
        }
        var level = Minecraft.getInstance().level;
        if (PlayerData.get().modeAvailable(1000)) {
            screenWidth = screenWidth-5;
            int y = 10;
            for(var entry: PlayerData.get().hgPluginMap.int2IntEntrySet()){
                var text =  "mode: "+entry.getIntKey()+" value: "+entry.getIntValue();
                ClientUtils.drawStringLeft(guiGraphics,text,screenWidth,y,white);
                y=y+12;
            }
            ClientUtils.drawStringLeft(guiGraphics,"shift:"+ClientUtils.isShiftDown(),screenWidth,y, white);
            y=y+12;
            ClientUtils.drawStringLeft(guiGraphics,"ctrl:"+ClientUtils.isCtrlDown(),screenWidth,y,white);
            y=y+12;
            var player = Minecraft.getInstance().player;
            if (player != null) {
                var vec = player.getLookAngle();
                ClientUtils.drawStringLeft(guiGraphics,vec.toString(),screenWidth,y,white);
                y=y+12;
                var v2 = TTUtils.getHorizontalVec2(vec);
                ClientUtils.drawStringLeft(guiGraphics, "("+v2.x+","+v2.y+")",screenWidth,y,white);
                y=y+12;
                ClientUtils.drawStringLeft(guiGraphics, String.valueOf(v2.length()),screenWidth,y,white);
                y=y+12;
                ClientUtils.drawStringLeft(guiGraphics,
                        String.valueOf(180D-TTUtils.sameSign(v2.y,Math.toDegrees(Math.acos(v2.x)))),screenWidth,y,white);
                y+=12;
                ClientUtils.drawStringLeft(guiGraphics,
                        String.format("dayTime: %d  / gameTime: %d / isDay: %b / skyDarken: %.8f",
                                level.getDayTime(),
                                level.getGameTime(),level.isDay(),
                                level.getSkyDarken(partialTick)),screenWidth,y,white);
                y+=12;
                var ll = ClientUtils.getLatitudeAndLongitude();
                ClientUtils.drawStringLeft(guiGraphics,"E/N: "+ll,screenWidth,y,white);
                y+=12;
                ClientUtils.drawStringLeft(guiGraphics,String.format("day: %d / time of day: %.8f",(level.getDayTime()/24000)%365,level.getTimeOfDay(partialTick)) ,screenWidth,y,white);
                y+=12;
                ClientUtils.drawStringLeft(guiGraphics,String.format("partialTick: %.8f",partialTick),screenWidth,y,white);
                
            }
        }
//        for(int i=0;i<screenWidth;i++){
//            float dayOfTime = level.dimensionType().timeOfDay((long) (24000L * ((float)i/screenWidth)));
//            guiGraphics.fill(i, (int) (screenHeight-dayOfTime*screenHeight),i+1, (int) (screenHeight-dayOfTime*screenHeight+1),black);
//        }
    }
}
