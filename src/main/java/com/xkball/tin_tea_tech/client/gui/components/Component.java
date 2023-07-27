package com.xkball.tin_tea_tech.client.gui.components;

import com.xkball.tin_tea_tech.api.client.gui.IComponent;
import com.xkball.tin_tea_tech.api.client.gui.PosGetter;
import com.xkball.tin_tea_tech.client.shape.Point2D;
import com.xkball.tin_tea_tech.client.shape.Rec2D;
import com.xkball.tin_tea_tech.common.player.IExtendedPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Component implements IComponent {
    
    protected int x = 0;
    protected int y = 0;
    
    protected int w;
    protected int h;
    
    protected int mw = 0;
    protected int mh = 0;
    boolean enabled = true;
    
    protected PosGetter posGetter = new PosGetter() {
        @Override
        public int getX() {
            return x;
        }
        
        @Override
        public int getY() {
            return y;
        }
    };
    
    @Override
    public void setSize(int w, int h) {
        this.w = w;
        this.h = h;
    }
    
    @Override
    public void resizeMax(int pWidth, int pHeight) {
        this.mw = pWidth;
        this.mh = pHeight;
    }
    
    public void offset(int x,int y) {
       this.posGetter = PosGetter.combine(posGetter, PosGetter.fixPoint(x,y));
    }
    
    @Override
    public void withParent(PosGetter posGetter){
        this.posGetter = PosGetter.combine(this.posGetter,posGetter);
    }
    
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return false;
    }
    
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return false;
    }
    
    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        return false;
    }
    
    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
    
    }
    
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return false;
    }
    
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        return false;
    }
    
    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        return false;
    }
    
    @Override
    public void tick() {
    
    }
    
    @Override
    public boolean enabled() {
        return enabled;
    }
    
    @Override
    public boolean display() {
        return enabled();
    }
    
    @Override
    public boolean canInteractive(int mouseX,int mouseY) {
        if(w>0 && h>0){
            return new Rec2D(this).isInside(mouseX,mouseY);
        }
        return false;
    }
    
    @Override
    public int getX() {
        return posGetter.getX();
    }
    
    @Override
    public int getY() {
        return posGetter.getY();
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
    
    }
    
    public Point2D getStartPos(){
        return new Point2D(getX(),getY());
    }
    
    public Point2D getEndPos(){
        return new Point2D(getX()+getW(),getY()+getH());
    }
    
    
    public int getW() {
        return w;
    }
    
    public int getH() {
        return h;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public static Player getPlayer(){
        return Minecraft.getInstance().player;
    }
    
    public static IExtendedPlayer getExtendedPlayer(){
        return (IExtendedPlayer) getPlayer();
    }
    
    public static net.minecraft.network.chat.Component translatable(String key){
        return net.minecraft.network.chat.Component.translatable("gui.tin_tea_tech."+key);
    }
}
