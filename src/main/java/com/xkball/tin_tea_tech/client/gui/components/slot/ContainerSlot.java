package com.xkball.tin_tea_tech.client.gui.components.slot;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.client.gui.PosGetter;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import com.xkball.tin_tea_tech.network.packet.SyncGUIDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ContainerSlot implements Renderable {
    private static final ResourceLocation bg = TinTeaTech.ttResource("textures/gui/slot.png");
    
    private final int slot;
    public final Container container;
    public final ItemStackHandler itemStackHandler = new ItemStackHandler(1);
    public final int x;
    public final int y;
    private final PosGetter posGetter;
    
    public ContainerSlot(Container pContainer, int pSlot, int pX, int pY, PosGetter posGetter) {
        this.container = pContainer;
        this.slot = pSlot;
        this.x = pX;
        this.y = pY;
        this.posGetter = posGetter;
        this.itemStackHandler.setStackInSlot(0,container.getItem(slot));
    }
    public ItemStack getItem(){
        return container.getItem(slot);
    }
    
    public void setItem(ItemStack item){
        container.setItem(slot, item);
        itemStackHandler.setStackInSlot(0,item);
        if(TinTeaTech.isClient()){
            TTNetworkHandler.CHANNEL.sendToServer(new SyncGUIDataPacket(slot,item));
        }
    }
    
    public ItemStack extractItem(int amount, boolean simulate){
        var result = itemStackHandler.extractItem(0,amount,simulate);
        if(!simulate && !getItem().equals(itemStackHandler.getStackInSlot(0),false)){
            setItem(itemStackHandler.getStackInSlot(0));
        }
        return result;
    }
    public ItemStack insertItem(@NotNull ItemStack stack, boolean simulate){
        var result  = itemStackHandler.insertItem(0,stack,simulate);
        if(!simulate && !getItem().equals(itemStackHandler.getStackInSlot(0),false)){
            setItem(itemStackHandler.getStackInSlot(0));
        }
        return result;
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        var px = x + posGetter.getX();
        var py = y + posGetter.getY();
        var item = container.getItem(slot);
        pGuiGraphics.pose().pushPose();
        //pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        pGuiGraphics.blit(bg,px,py,0f,0f,18,18,18,18);
        if(isInSlot(pMouseX,pMouseY)){
            pGuiGraphics.fill(px+1, py+1, px + 17, py + 17, -2130706433);
            if(!item.isEmpty()){
                pGuiGraphics.renderTooltip(Minecraft.getInstance().font,item,pMouseX,pMouseY);
            }
        }
        
        pGuiGraphics.renderItem(item, px+1, py+1);
        pGuiGraphics.renderItemDecorations(Minecraft.getInstance().font, item, px+1, py+1, null);
        pGuiGraphics.pose().popPose();
    }
    
    public boolean isInSlot(int mx,int my){
        var px = x + posGetter.getX();
        var py = y + posGetter.getY();
        return mx>=px+1 && mx<px+18 && my>=py+1 && my<py+18;
        
    }
}
