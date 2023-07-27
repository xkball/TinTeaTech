package com.xkball.tin_tea_tech.client.gui.components;

import com.xkball.tin_tea_tech.utils.TTUtils;

public class DraggableComponent extends Component {
    
    protected int ox = -1;
    protected int oy = -1;
    
    protected boolean isDragging = false;
    
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY){
        if(pButton == 0 && (isDragging || canDrag(pMouseX,pMouseY))){
            int dx = isDragging ? (int) (x+pMouseX - ox) : (int) (x+pDragX);
            int dy = isDragging ? (int)(y+pMouseY-oy) : (int) (y+pDragY);
            this.x = TTUtils.clamp(mw-w,0,dx);
            this.y = TTUtils.clamp(mh-h,0, dy);
            ox = (int) pMouseX;
            oy = (int) pMouseY;
            isDragging = true;
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if(pButton == 0) {
            isDragging = false;
            ox = -1;
            oy = -1;
        }
       
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
    
    @Override
    public boolean canInteractive(int mouseX, int mouseY) {
        return true;
    }
    
    public boolean canDrag(double x, double y){
        return true;
    }
}
