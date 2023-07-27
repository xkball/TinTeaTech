package com.xkball.tin_tea_tech.client.gui.sub_screen;

import com.xkball.tin_tea_tech.client.gui.components.SubScreen;
import com.xkball.tin_tea_tech.client.gui.screen.TTScreen;

import java.util.function.Predicate;

public class ModeSubScreen<T extends TTScreen> extends SubScreen<T> {
    
    protected final Predicate<T> canDisplay;
    public ModeSubScreen(Predicate<T> canDisplay){
        this.canDisplay = canDisplay;
    }
    @Override
    public boolean enabled() {
        return canDisplay.test(getOutScreen());
    }
    
}
