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
    
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (PlayerData.get().modeAvailable(1000)) {
            screenWidth = screenWidth-5;
            int y = 10;
            var black = ColorUtils.getColor(0,0,0,255);
            for(var entry: PlayerData.get().hgPluginMap.int2IntEntrySet()){
                var text =  "mode: "+entry.getIntKey()+" value: "+entry.getIntValue();
                ClientUtils.drawStringLeft(guiGraphics,text,screenWidth,y,black);
                y=y+12;
            }
            ClientUtils.drawStringLeft(guiGraphics,"shift:"+ClientUtils.isShiftDown(),screenWidth,y, black);
            y=y+12;
            ClientUtils.drawStringLeft(guiGraphics,"ctrl:"+ClientUtils.isCtrlDown(),screenWidth,y,black);
            y=y+12;
            var player = Minecraft.getInstance().player;
            if (player != null) {
                var vec = player.getLookAngle();
                ClientUtils.drawStringLeft(guiGraphics,vec.toString(),screenWidth,y,black);
                y=y+12;
                var v2 = TTUtils.getHorizontalVec2(vec);
                ClientUtils.drawStringLeft(guiGraphics, "("+v2.x+","+v2.y+")",screenWidth,y,black);
                y=y+12;
                ClientUtils.drawStringLeft(guiGraphics, String.valueOf(v2.length()),screenWidth,y,black);
                y=y+12;
                ClientUtils.drawStringLeft(guiGraphics,
                        String.valueOf(180D-TTUtils.sameSign(v2.y,Math.toDegrees(Math.acos(v2.x)))),screenWidth,y,black);
            }
        }
    }
}
