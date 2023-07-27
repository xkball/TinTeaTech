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
        this.addComponent(new Label(translatable("lock_inventory")).setYCenter(true).pos(70,58));
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
        b.pos(15,50);
        this.addComponent(b);
    }
}