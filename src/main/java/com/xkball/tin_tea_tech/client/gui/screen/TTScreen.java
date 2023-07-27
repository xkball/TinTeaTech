package com.xkball.tin_tea_tech.client.gui.screen;

import com.xkball.tin_tea_tech.api.client.gui.IComponent;
import com.xkball.tin_tea_tech.api.client.gui.IGUIProvider;
import com.xkball.tin_tea_tech.api.client.gui.PosGetter;
import com.xkball.tin_tea_tech.client.gui.components.SubScreen;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import com.xkball.tin_tea_tech.network.packet.SyncGUIDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

//在SubScreen泛型化之前写的 懒得管了
@SuppressWarnings("rawtypes")
@ParametersAreNonnullByDefault
public abstract class TTScreen extends Screen implements IGUIProvider, PosGetter {
    
    protected static final Comparator<SubScreen> comparator = Comparator.comparingInt(SubScreen::getLayer);
    protected final LinkedList<SubScreen> subScreens = new LinkedList<>();
    protected final List<IComponent> components = new ArrayList<>();
    
    protected ItemStack carriedItem = ItemStack.EMPTY;
    
    protected int openedFromPlayerSlot = -1;
    protected ItemStack openedFromItem = ItemStack.EMPTY;
    
    protected boolean needSync = false;
    protected TTScreen(Component pTitle, CompoundTag data) {
        super(pTitle);
        if(data.contains("carriedItem")){
            carriedItem = ItemStack.of(data.getCompound("carriedItem"));
        }
        if(data.contains("openedFromPlayerSlot") && data.contains("openedFromItem")){
            openedFromPlayerSlot = data.getInt("openedFromPlayerSlot");
            openedFromItem = ItemStack.of(data.getCompound("openedFromItem"));
        }
    }
    
    @Override
    public void onClose() {
        syncData();
        super.onClose();
    }
    
    public final void saveIn(CompoundTag data){
        if(!carriedItem.isEmpty()) data.put("carriedItem",carriedItem.save(new CompoundTag()));
        save(data);
    }
   
    public void syncData(){
        if(openedFromPlayerSlot>=0 && !openedFromItem.isEmpty()){
            var tag = new CompoundTag();
            var itemTag = new CompoundTag();
            saveIn(itemTag);
            tag.put("itemTag",itemTag);
            tag.putInt("openedFromPlayerSlot",openedFromPlayerSlot);
            tag.put("openedFromItem",openedFromItem.save(new CompoundTag()));
            TTNetworkHandler.CHANNEL.sendToServer(new SyncGUIDataPacket(openedFromPlayerSlot,tag));
        }
    }
    
    public void addSubScreen(SubScreen screen){
        this.subScreens.add(screen);
        subScreens.sort(comparator.reversed());
        screen.setOutScreen(this);
        screen.init();
    }
    
    public void addComponent(IComponent component){
        this.components.add(component);
        component.withParent(this);
    }
    
    public void clear(){
        subScreens.clear();
        components.clear();
    }
    
    @Override
    protected void init() {
        for(var c : components){
            c.resizeMax(width,height);
        }
        for(var c : subScreens){
            c.resizeMax(width,height);
        }
    }
    
    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
        for(var c : components){
            c.resizeMax(width,height);
        }
        for(var c : subScreens){
            c.resizeMax(width,height);
        }
        
    }
    
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        for(var c : components){
            if(c.enabled()){
                c.mouseDragged(pMouseX,pMouseY,pButton,pDragX,pDragY);
            }
        }
        for (SubScreen scr : subScreens) {
            if (scr.enabled()
                    && scr.canInteractive((int) pMouseX, (int) pMouseY)
                    && scr.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
                    && !scr.canPenetrateInteraction()) {
                return true;
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
        for (SubScreen scr : subScreens) {
            if (scr.enabled()
                    && scr.canInteractive((int) pMouseX, (int) pMouseY)
                    && scr.mouseClicked(pMouseX,pMouseY,pButton)
                    && !scr.canPenetrateInteraction()) {
                return true;
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
        for (SubScreen scr : subScreens){
            if (scr.enabled()
                    && scr.canInteractive((int) pMouseX, (int) pMouseY)
                    && scr.mouseReleased(pMouseX,pMouseY,pButton)
                    && !scr.canPenetrateInteraction()) {
                return true;
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
        for (SubScreen scr : subScreens) {
            if (scr.enabled()
                    && scr.canInteractive((int) pMouseX, (int) pMouseY)
                    && scr.mouseScrolled(pMouseX,pMouseY,pDelta)
                    && !scr.canPenetrateInteraction()) {
                return true;
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
        for (SubScreen scr : subScreens) {
            if (scr.enabled() && scr.canInteractive((int) pMouseX, (int) pMouseY)) {
                scr.mouseMoved(pMouseX,pMouseY);
                if(!scr.canPenetrateInteraction()){
                    return ;
                }
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
        for (SubScreen scr : subScreens) {
            if (scr.enabled()
                    && scr.keyPressed(pKeyCode,pScanCode,pModifiers)
                    && !scr.canPenetrateInteraction()) {
                return true;
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
        for (SubScreen scr : subScreens) {
            if (scr.enabled()
                    && scr.keyReleased(pKeyCode,pScanCode,pModifiers)
                    && !scr.canPenetrateInteraction()) {
                return true;
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
        for (SubScreen scr : subScreens) {
            if (scr.enabled()
                    && scr.charTyped(pCodePoint, pModifiers)
                    && !scr.canPenetrateInteraction()) {
                return true;
            }
        }
        return super.charTyped(pCodePoint, pModifiers);
    }
    
    @Override
    public void tick() {
        super.tick();
        if(needSync){
            syncData();
            needSync = false;
        }
        for(var c : components){
            if(c.enabled()){
                c.tick();
            }
        }
        for (SubScreen scr : subScreens) {
            if (scr.enabled()){
                scr.tick();
            }
        }
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        for(var c : components){
            if(c.display()) c.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        }
        var de = subScreens.descendingIterator();
        while (de.hasNext()){
            var src = de.next();
            if(src.display()) src.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        }
        if(!carriedItem.isEmpty()){
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
            pGuiGraphics.renderItem(carriedItem,pMouseX-8,pMouseY-8);
            pGuiGraphics.renderItemDecorations(Minecraft.getInstance().font,carriedItem,pMouseX-8,pMouseY-8);
            pGuiGraphics.pose().popPose();
        }
    }
    
    public ItemStack getCarriedItem() {
        return carriedItem;
    }
    
    public void setCarriedItem(ItemStack carriedItem) {
        if(!carriedItem.equals(this.carriedItem,false)){
            this.carriedItem = carriedItem;
            syncData();
        }
    }
    
    public void setNeedSync(boolean needSync) {
        this.needSync = needSync;
    }
}
