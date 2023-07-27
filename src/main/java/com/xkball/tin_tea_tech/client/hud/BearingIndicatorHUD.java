package com.xkball.tin_tea_tech.client.hud;

import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.common.player.PlayerData;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import com.xkball.tin_tea_tech.utils.TTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@AutomaticRegistration
public class BearingIndicatorHUD implements IGuiOverlay {
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (PlayerData.get().modeAvailable(2) && Minecraft.getInstance().screen == null){
            var player = Minecraft.getInstance().player;
            if (player != null) {
                var x0 = screenWidth/2-100;
                var y0 = 20;
                var x1 = x0+5;
                //颜色与debugScreenOverlay一致
                guiGraphics.fill(x0, y0, x0 + 200, y0 + 20, -1873784752);
                guiGraphics.fill(x0+99,y0+12,x0+101,y0+18,ColorUtils.white);
                guiGraphics.fill(x0+9,y0+12,x0+11,y0+18,ColorUtils.white);
                guiGraphics.fill(x0+189,y0+12,x0+191,y0+18,ColorUtils.white);
                guiGraphics.hLine(x1, x1 + 190, 36, ColorUtils.white);
                var v3 = player.getLookAngle();
                var v2 = TTUtils.getHorizontalVec2(v3);
                int startX = (int) (180-TTUtils.sameSign(v2.y,Math.toDegrees(Math.acos(v2.x))));
                startX = (startX+95)%360;
//                for(int i=0;i<16;i++){
//
//                    //0到360
//                    float v_drawAt = i*22.5f;
//                    //视野暂定180度 排除不在视野的
//                    if(v_drawAt>startX && v_drawAt<startX+180) continue;
//
//                }
                //上面什么一坨垃圾玩意
                //只绘制方向指示算了 小刻度不要也罢
               var first = Facings.getFirst(startX);
               startX = startX%90;
               while (startX<190) {
                   drawAt(first.name, guiGraphics, startX, x1);
                   first = first.getNext();
                   startX=startX+90;
               }
            }
        }
    }
    
    public static void drawAt(String text,GuiGraphics guiGraphics,int xOffset,int x0){
        var font = Minecraft.getInstance().font;
        var w = font.width(text);
        guiGraphics.drawString(font,text,x0+xOffset-w/2,25,ColorUtils.white);
    }
    
    public enum Facings{
        W(1, "W"),
        N(2, "N"),
        E(3, "E"),
        S(0, "S");
        
        final int next;
        final String name;
        
        Facings(int next, String name) {
            this.next = next;
            this.name = name;
        }
        
        public Facings getNext(){
            return Facings.values()[next];
        }
        
        public static Facings getFirst(int start){
            start = start%360;
            if(start<90){
                return W;
            }
            if(start<180){
                return S;
            }
            if(start<270){
                return E;
            }
            return N;
        }
    }
}
