package com.xkball.tin_tea_tech.client.gui.sub_screen;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.client.gui.components.Label;
import com.xkball.tin_tea_tech.client.gui.components.slot.ItemHandlerSlot;
import com.xkball.tin_tea_tech.client.gui.screen.HoloGlassScreen;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class HoloGlassPluginSubScreen extends ModeSubScreen<HoloGlassScreen> {
    
    private final List<ItemHandlerSlot> slots = new ArrayList<>(36);
    public HoloGlassPluginSubScreen(Predicate<HoloGlassScreen> canDisplay){
        super(canDisplay);
        this.layer = 1;
    }
    
    @Override
    public void init() {
        var out = getOutScreen();
        assert out != null;
        this.setSize(out.width-90,out.height-27-12);
        var container = out.getContainer();
        for(int i=0;i<container.getSlots();i++) {
            slots.add(new ItemHandlerSlot(container,i,70+(i%9)*18,50,this){
                @Override
                public void setItem(ItemStack item) {
                    super.setItem(item);
                    if(TinTeaTech.isClient()){
                        out.setNeedSync(true);
                    }
                }
            });
        }
        this.addComponent(new Label(translatable("plugins_installed")).setYCenter(true).pos(10,58));
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        for(var s : slots){
            s.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        }
    }
    
    public void renderBackground(GuiGraphics pGuiGraphics) {
        var edgeColor = ColorUtils.getColor(0,0,0,255);
        var x = getX()+66;
        var y = getY()+46;
        var w = 18*9+8;
        var h = 18+8;
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
                            if(!slot.isItemValid(carried)) return false;
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
                        return true;
                    }
                }
            }
            if(pButton == 2) {
                assert Minecraft.getInstance().player != null;
                if (Minecraft.getInstance().player.isCreative()) {
                    var slot = getSlot(pMouseX, pMouseY);
                    if (slot != null){
                        outScreen.setCarriedItem(slot.getItem().copy());
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    
    @Nullable
    public ItemHandlerSlot getSlot(double mx, double my){
        for(var slot : slots){
            if(slot.isInSlot((int) mx, (int) my)) return slot;
        }
        return null;
    }
}
