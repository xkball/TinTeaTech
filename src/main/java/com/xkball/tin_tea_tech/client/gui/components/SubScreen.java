package com.xkball.tin_tea_tech.client.gui.components;

import com.xkball.tin_tea_tech.api.client.gui.IComponent;
import com.xkball.tin_tea_tech.api.client.gui.ISubScreen;
import com.xkball.tin_tea_tech.client.gui.screen.TTScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SubScreen<T extends TTScreen> extends DraggableComponent implements ISubScreen {
    
    protected int layer = 0;
    
    protected final List<IComponent> components = new ArrayList<>();
    
    @Nullable
    protected T outScreen = null;
    
    public void addComponent(IComponent component){
        this.components.add(component);
        component.withParent(this);
    }
    @Override
    public boolean canPenetrateInteraction() {
        return false;
    }
    
    @Override
    public boolean canDrag(double x,double y) {
        return false;
    }
    
    @Override
    public int getLayer() {
        return layer;
    }
    
    public void setLayer(int layer) {
        this.layer = layer;
    }
    
    public @Nullable T getOutScreen() {
        return outScreen;
    }
    
    public void setOutScreen(@Nonnull T outScreen) {
        this.outScreen = outScreen;
    }
    
    @Override
    public void init(){
    
    }
    
    @Override
    public void resizeMax(int pWidth, int pHeight) {
        super.resizeMax(pWidth,pHeight);
        for(var c : components){
            c.resizeMax(pWidth,pHeight);
        }
    }
    
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        for(var c : components){
            if(c.enabled()){
                c.mouseDragged(pMouseX,pMouseY,pButton,pDragX,pDragY);
            }
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }
    
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for(var c : components){
            if(c.enabled()){
                c.mouseClicked(pMouseX,pMouseY,pButton);
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    
    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        for(var c : components){
            if(c.enabled()){
                c.mouseReleased(pMouseX,pMouseY,pButton);
            }
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
    
    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        for(var c : components){
            if(c.enabled()){
                c.mouseScrolled(pMouseX,pMouseY,pDelta);
            }
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }
    
    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        for(var c : components){
            if(c.enabled()){
                c.mouseMoved(pMouseX,pMouseY);
            }
        }
        super.mouseMoved(pMouseX, pMouseY);
    }
    
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        for(var c : components){
            if(c.enabled()){
                c.keyPressed(pKeyCode,pScanCode,pModifiers);
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        for(var c : components){
            if(c.enabled()){
                c.keyReleased(pKeyCode,pScanCode,pModifiers);
            }
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }
    
    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        for(var c : components){
            if(c.enabled()){
                c.charTyped(pCodePoint, pModifiers);
            }
        }
        return super.charTyped(pCodePoint, pModifiers);
    }
    
    @Override
    public void tick() {
        super.tick();
        for(var c : components){
            if(c.enabled()){
                c.tick();
            }
        }
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        for(var c : components){
            if(c.display()) c.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        }
    }
    
}
