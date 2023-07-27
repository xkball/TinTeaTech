package com.xkball.tin_tea_tech.client.gui.screen;

import com.xkball.tin_tea_tech.api.item.IHoloGlassPlugin;
import com.xkball.tin_tea_tech.client.gui.components.Label;
import com.xkball.tin_tea_tech.client.gui.components.button.InventoryTabButton;
import com.xkball.tin_tea_tech.client.gui.components.button.ModeButton;
import com.xkball.tin_tea_tech.client.gui.sub_screen.GuiConfigSubScreen;
import com.xkball.tin_tea_tech.client.gui.sub_screen.HoloGlassPluginSubScreen;
import com.xkball.tin_tea_tech.client.gui.sub_screen.PlayerInventorySubScreen;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@ParametersAreNonnullByDefault
public class HoloGlassScreen extends TTScreen {
    private static final Component NAME = Component.translatable("gui.title.tin_tea_tech.holo_glass");
    
    //    //protected HoloGlassScreen() {
//        super(NAME);
//    }
    private final PlayerInventorySubScreen<HoloGlassScreen> inventory;
    
    private boolean changed = false;
    
    private final AtomicInteger mode = new AtomicInteger(0);
    
    private final int[] modes = new int[9];
    
    private final ItemStackHandler container = new ItemStackHandler(9){
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            var i = IHoloGlassPlugin.getMode(stack);
            return i != -1 && Arrays.stream(modes).noneMatch((i1) -> i1==i );
        }
        
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
        
        @Override
        protected void onContentsChanged(int slot) {
            changed = true;
        }
    };
    public HoloGlassScreen(CompoundTag data){
        super(NAME,data);
        inventory = new PlayerInventorySubScreen<>(100,100,5);
        if(data.contains("inventory_posX") && data.contains("inventory_posY") && data.contains("inventory_locked")){
            inventory.setX(data.getInt("inventory_posX"));
            inventory.setY(data.getInt("inventory_posY"));
            inventory.setLocked(data.getBoolean("inventory_locked"));
        }
        if(data.contains("inner_container")){
            container.deserializeNBT(data.getCompound("inner_container"));
        }
        if(data.contains("mode")){
            mode.set(data.getInt("mode"));
        }
        for(int i=0;i<9;i++){
            modes[i] = IHoloGlassPlugin.getMode(container.getStackInSlot(i));
        }
        addComponent();
        
    }
    
    
    public void addComponent(){
        this.clear();
        
        this.addSubScreen(inventory);
        this.addComponent(new Label(NAME).dropShadow(true).pos(10,-5).setYCenter(true));
        this.addComponent(new InventoryTabButton(inventory));
        int hy = 2;
        var b0 = new ModeButton(
                Component.translatable("gui.tin_tea_tech.holo_glass_plugin"),
                () -> mode.set(0),() -> mode.get() == 0);
        b0.setSize(90,20);
        b0.pos(0,hy);
        hy = hy+21;
        this.addComponent(b0);
        
        var b1 = new ModeButton(
                Component.translatable("gui.tin_tea_tech.gui_setting"),
                () -> mode.set(1),() -> mode.get() == 1);
        b1.setSize(90,20);
        b1.pos(0,hy);
        hy = hy+21;
        this.addComponent(b1);
        
        var s1 = new HoloGlassPluginSubScreen((self) -> self != null && self.mode.get()==0);
        s1.withParent(this);
        s1.setX(90);
        this.addSubScreen(s1);
        var s2 = new GuiConfigSubScreen((self) -> self != null && self.mode.get()==1);
        s2.withParent(this);
        s2.setX(90);
        this.addSubScreen(s2);
        
        if(width != 0){
            this.resize(Minecraft.getInstance(),width,height);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if(changed){
            addComponent();
            updatePluginData();
            changed = false;
        }
    }
    
    public void updatePluginData(){
        for(int i=0;i<9;i++){
            var oldV = modes[i];
            var newV = IHoloGlassPlugin.getMode(container.getStackInSlot(i));
            if(oldV != newV){
                var pd =  com.xkball.tin_tea_tech.client.gui.components.Component.getExtendedPlayer().getPlayerData();
                if(oldV == -1){
                    pd.modeChange(
                            true,newV
                    );
                }
                else if(newV == -1){
                    pd.modeChange(false,oldV);
                }
                else {
                    pd.modeChange(false,oldV);
                    pd.modeChange(true,newV);
                }
                pd.sync();
            }
            modes[i] = newV;
        }
        syncData();
    }
    
    public void setChanged(boolean changed){
        this.changed = changed;
    }
    
    @Override
    public int getID() {
        return 0;
    }
    
    @Override
    public Component getName() {
        return NAME;
    }
    
    @Override
    public Screen newInstance(CompoundTag data) {
        return new HoloGlassScreen(data);
    }
    
    @Override
    public void save(CompoundTag data) {
        data.putInt("inventory_posX", (int) inventory.getStartPos().x());
        data.putInt("inventory_posY", (int) inventory.getStartPos().y());
        data.putBoolean("inventory_locked",inventory.isLocked());
        data.put("inner_container",container.serializeNBT());
        data.putInt("mode",mode.get());
    }
    
    @Override
    public void renderBackground(GuiGraphics pGuiGraphics) {
        var bg = ColorUtils.getColor(40,40,40,155);
        var black = ColorUtils.getColor(0,0,0,255);
        pGuiGraphics.fill(0,0,width,height, bg);
        pGuiGraphics.fill(0,0,width,12,bg);
        pGuiGraphics.fill(0,height-27,width,height,bg);
        pGuiGraphics.hLine(0,width,12,black);
        pGuiGraphics.hLine(0,width,height-28,black);
        pGuiGraphics.vLine(90,12,height-27,black);
    }
    
    @Override
    protected void init() {
        for(var c : components){
            c.resizeMax(width,height-27);
        }
        for(var c : subScreens){
            c.resizeMax(width,height-27);
        }
    }
    
    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
        for(var c : components){
            c.resizeMax(width,height-27);
        }
        for(var c : subScreens){
            c.resizeMax(width,height-27);
        }
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        
    }
    
    @Override
    public int getX() {
        return 0;
    }
    
    @Override
    public int getY() {
        return 12;
    }
    
    public ItemStackHandler getContainer() {
        return container;
    }
    
    public PlayerInventorySubScreen<HoloGlassScreen> getInventory() {
        return inventory;
    }
    
}
