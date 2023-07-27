package com.xkball.tin_tea_tech.api.client.gui;

import net.minecraft.client.gui.components.Renderable;

public interface IComponent extends Renderable,PosGetter {
    
    void setSize(int w,int h);
    void resizeMax(int pWidth, int pHeight);
    boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY);
    boolean mouseClicked(double pMouseX, double pMouseY, int pButton);
    boolean mouseReleased(double pMouseX, double pMouseY, int pButton);
    boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta);
    void mouseMoved(double pMouseX, double pMouseY);
    boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers);
    boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers);
    boolean charTyped(char pCodePoint, int pModifiers);
    void tick();
    boolean enabled();
    
    boolean display();
    boolean canInteractive(int mouseX,int mouseY);
    
    void withParent(PosGetter posGetter);
}
