package com.xkball.tin_tea_tech.client.gui.sub_screen;

import com.xkball.tin_tea_tech.client.gui.components.Label;
import com.xkball.tin_tea_tech.client.gui.components.button.EnableButton;
import com.xkball.tin_tea_tech.client.gui.screen.HoloGlassScreen;

import java.util.function.Predicate;

public class GuiConfigSubScreen extends ModeSubScreen<HoloGlassScreen> {
    
    public GuiConfigSubScreen(Predicate<HoloGlassScreen> canDisplay) {
        super(canDisplay);
    }
    
    @Override
    public void init() {
        super.init();
        
        this.addComponent(new Label(translatable("building_mode")).setYCenter(true).pos(70,18));
        var b0 = new EnableButton(() -> {
            var pd =  getExtendedPlayer().getPlayerData();
            pd.buildingMode = !pd.buildingMode;
            pd.sync();
        },() -> getExtendedPlayer().getPlayerData().buildingMode);
        b0.setSize(40,20);
        b0.pos(15,10);
        this.addComponent(b0);
        
        this.addComponent(new Label(translatable("lock_inventory")).setYCenter(true).pos(70,48));
        var b = new EnableButton(() -> {
            if (getOutScreen() != null) {
                var inv = getOutScreen().getInventory();
                inv.setLocked(!inv.isLocked());
            }
        },() -> {
            if (getOutScreen() != null) {
                return getOutScreen().getInventory().isLocked();
            }
            return false;
        });
        b.setSize(40,20);
        b.pos(15,40);
        this.addComponent(b);
        
        this.addComponent(new Label(translatable("display_nbt")).setYCenter(true).pos(70,48+30));
        var b2 = new EnableButton(() -> {
            var pd =  getExtendedPlayer().getPlayerData();
            pd.setDisplayNBT(!pd.displayNBT);
        },() -> getExtendedPlayer().getPlayerData().displayNBT);
        b2.setSize(40,20);
        b2.pos(15,40+30);
        this.addComponent(b2);
        
        this.addComponent(new Label(translatable("allow_riding_by_player")).setYCenter(true).pos(70,48+60));
        var b3 = new EnableButton(() -> {
            var pd =  getExtendedPlayer().getPlayerData();
            pd.allowRidingByPlayer = !pd.allowRidingByPlayer;
            pd.sync();
        },() -> getExtendedPlayer().getPlayerData().allowRidingByPlayer);
        b3.setSize(40,20);
        b3.pos(15,40+60);
        this.addComponent(b3);
    }
}
