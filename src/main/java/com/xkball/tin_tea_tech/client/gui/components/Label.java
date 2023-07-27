package com.xkball.tin_tea_tech.client.gui.components;

import com.xkball.tin_tea_tech.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class Label extends Component{
    
    protected final Supplier<net.minecraft.network.chat.Component> text;
    private boolean yCenter = false;
    protected boolean drawShadow = false;
    
    protected int defaultColor = ColorUtils.getColor(183,183,183,255);
    
    public Label(Supplier<net.minecraft.network.chat.Component> textSupplier){
        this.text = textSupplier;
        this.h = 9;
    }
    
    public Label(net.minecraft.network.chat.Component text){
        this(() -> text);
    }
    
    public Label pos(int x,int y){
        this.x = x;
        this.y = y;
        return this;
    }
    
    public Label setYCenter(boolean yCenter){
        this.yCenter = yCenter;
        return this;
    }
    
    public Label dropShadow(boolean b){
        this.drawShadow = b;
        return this;
    }
    
    public Label color(int color){
        this.defaultColor = color;
        return this;
    }
    
    @Override
    public int getY() {
        var ry = super.getY();
        return yCenter? ry- getH()/2 : ry;
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        //int ry = yCenter ? posGetter.getY()-4 : posGetter.getY();
        pGuiGraphics.drawString(Minecraft.getInstance().font,text.get(),posGetter.getX(),getY(),defaultColor,drawShadow);
    }
}
