package com.xkball.tin_tea_tech.client.gui.components.button;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.client.gui.components.Label;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class Button extends Label {
    public static final ResourceLocation defaultBG = TinTeaTech.ttResource("textures/gui/button.png");
    public static final Component ON = Component.literal("ON").withStyle(ChatFormatting.GREEN);
    public static final Component OFF = Component.literal("OFF").withStyle(ChatFormatting.RED);
    private final Runnable recall;
    //private final ResourceLocation bg;
    
    protected int cd = 0;
    
    public Button(Component text,Runnable recall) {
        super(text);
        this.recall = recall;
        //this.bg = bg;
        this.color(ColorUtils.getColor(0,0,0,255));
        this.setYCenter(true);
    }
    
    public Button(Supplier<Component> text, Runnable recall){
        super(text);
        this.recall = recall;
    }
    
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(cd <= 0 &&canInteractive((int) pMouseX, (int) pMouseY)){
            recall.run();
            cd = 5;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    
    @Override
    public void tick() {
        if(cd>0) cd--;
    }
    
    @Override
    public int getY() {
        return posGetter.getY();
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        drawBG(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        pGuiGraphics.drawString(Minecraft.getInstance().font,text.get(),getX()+5,getY()+getH()/2-3,defaultColor,drawShadow);
        
    }
    
    public void drawBG(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick){
        pGuiGraphics.fill(getX(),getY(),getX()+getW(),getY()+getH(),
                cd<1 && canInteractive(pMouseX,pMouseY) ? ColorUtils.getColor(255,255,255,255) : ColorUtils.getColor(0,0,0,255));
        pGuiGraphics.fill(getX()+1,getY()+1,getX()+getW()-1,getY()+getH()-1, ColorUtils.getColor(139,139,139,255));
        var c1 = ColorUtils.getColor(255,255,255,255);
        var c2 = ColorUtils.getColor(55,55,55,255);
        var c3 = cd>0?c2:c1;
        var c4 = cd>0?c1:c2;
        pGuiGraphics.vLine(getX()+1,getY()+1,getY()+getH()-2,c3);
        pGuiGraphics.hLine(getX()+1,getX()+getW()-2,getY()+1,c3);
        pGuiGraphics.vLine(getX()+getW()-2,getY()+1,getY()+getH()-2,c4);
        pGuiGraphics.hLine(getX()+1,getX()+getW()-2,getY()+getH()-2,c4);
    }
}
