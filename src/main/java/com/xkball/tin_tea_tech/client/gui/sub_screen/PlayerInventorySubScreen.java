package com.xkball.tin_tea_tech.client.gui.sub_screen;

import com.xkball.tin_tea_tech.client.gui.components.slot.ContainerSlot;
import com.xkball.tin_tea_tech.client.gui.components.SubScreen;
import com.xkball.tin_tea_tech.client.gui.screen.TTScreen;
import com.xkball.tin_tea_tech.client.shape.Point2D;
import com.xkball.tin_tea_tech.client.shape.Rec2D;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import com.xkball.tin_tea_tech.utils.TTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class PlayerInventorySubScreen<T extends TTScreen> extends SubScreen<T> {
    
    
    @SuppressWarnings("FieldCanBeLocal")
    private final Inventory inventory;
    private boolean locked;
    
    private final List<ContainerSlot> slots = new ArrayList<>(36);
    
    public PlayerInventorySubScreen(){
        assert Minecraft.getInstance().player != null;
        this.inventory = Minecraft.getInstance().player.getInventory();
        for(int i=0;i<36;i++){
            slots.add(new ContainerSlot(inventory,i,(i%9)*18,(i/9)*18,this));
        }
        w = 18*9+8;
        h = 18*4+8;
    }
    
    public PlayerInventorySubScreen(int x,int y,int layer){
        this();
        this.x = x;
        this.y = y;
        this.layer = layer;
        this.setEnabled(getExtendedPlayer().getPlayerData().displayInventoryInGUI);
    }
    
    @Override
    public void resizeMax(int pWidth, int pHeight) {
        super.resizeMax(pWidth, pHeight);
        this.x = TTUtils.clamp(pWidth-w-4,0,x);
        this.y = TTUtils.clamp(pHeight-h-12,0,y);
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        for(var s : slots){
            s.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        }
    }
    
    public void renderBackground(GuiGraphics pGuiGraphics) {
        var edgeColor = ColorUtils.getColor(0,0,0,255);
        var x = this.x+getOutX();
        var y = this.y+getOutY();
        pGuiGraphics.hLine(x,x+w-1,y,edgeColor);
        pGuiGraphics.hLine(x,x+w-1,y+h-1,edgeColor);
        pGuiGraphics.vLine(x,y,y+h-1,edgeColor);
        pGuiGraphics.vLine(x+w-1,y,y+h-1,edgeColor);
        pGuiGraphics.fill(x+1,y+1,x+w-1,y+h-1, ColorUtils.getColor(198,198,198,255));
        var lc = ColorUtils.getColor(255,255,255,255);
        pGuiGraphics.fill(x+1,y+1,x+w-3,y+3,lc);
        pGuiGraphics.fill(x+1,y+2,x+3,y+h-3,lc);
        var dc = ColorUtils.getColor(85,85,85,255);
        pGuiGraphics.fill(x+3,y+h-3,x+w-1,y+h-1,dc);
        pGuiGraphics.fill(x+w-3,y+3,x+w-1,y+h-2,dc);
        var mc = ColorUtils.getColor(139,139,139,255);
        pGuiGraphics.fill(x+w-3,y+1,x+w-1,y+3,mc);
        pGuiGraphics.fill(x+1,y+h-3,x+3,y+h-1,mc);
    }
    
    @Override
    public int getX() {
        return Math.min(x+4+getOutX(),mw+4-w);
    }
    
    @Override
    public int getY() {
        return Math.min(y+4+getOutY(),mh+12-h);
    }
    
    private int getOutX(){
        return getOutScreen() == null ? 0 : getOutScreen().getX();
    }
    
    private int getOutY(){
        return getOutScreen() == null ? 0 : getOutScreen().getY();
    }
    
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (getOutScreen() != null){
            var outScreen = getOutScreen();
            if (pButton == 0) {
                var slot = getSlot(pMouseX, pMouseY);
                if (slot != null) {
                    var carried = outScreen.getCarriedItem();
                    if (carried.isEmpty()) {
                        outScreen.setCarriedItem(slot.extractItem(64, false));
                    } else {
                        if (slot.getItem().is(carried.getItem())) {
                            outScreen.setCarriedItem(slot.insertItem(carried.copy(), false));
                        } else {
                            var buff = slot.getItem().copy();
                            slot.setItem(carried.copy());
                            outScreen.setCarriedItem(buff);
                        }
                    }
                    return true;
                }
            }
            if(pButton == 1){
                var slot = getSlot(pMouseX, pMouseY);
                if (slot != null){
                    var carried = outScreen.getCarriedItem();
                    if (carried.isEmpty()){
                        var i = slot.getItem().getCount();
                        outScreen.setCarriedItem(slot.extractItem(i/2, false));
                    }
                }
            }
            if(pButton == 2) {
                assert Minecraft.getInstance().player != null;
                if (Minecraft.getInstance().player.isCreative()) {
                    var slot = getSlot(pMouseX, pMouseY);
                    if (slot != null){
                        outScreen.setCarriedItem(slot.getItem().copy());
                    }
                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    
    @Nullable
    public ContainerSlot getSlot(double mx, double my){
        for(var slot : slots){
            if(slot.isInSlot((int) mx, (int) my)) return slot;
        }
        return null;
    }
    
    @Override
    public boolean canDrag(double mx,double my) {
        if(locked) return false;
        return new Rec2D(this).isInside((int) mx, (int) my)
                && !new Rec2D(new Point2D(x+4,y+4),new Point2D(x+w-4,y+h-4)).isInside((int) mx, (int) my);
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
        if (this.getOutScreen() != null) {
            this.getOutScreen().setNeedSync(true);
        }
    }
}
