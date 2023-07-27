package com.xkball.tin_tea_tech.client.gui.components.button;

import com.xkball.tin_tea_tech.TinTeaTech;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ModeButton extends Button{
    
    private final BooleanSupplier pressed;
    public ModeButton(Component text, Runnable recall, BooleanSupplier booleanSupplier) {
        super(text, recall);
        this.pressed = booleanSupplier;
    }
    
    public ModeButton(Supplier<Component> text, Runnable recall, BooleanSupplier booleanSupplier) {
        super(text, recall);
        this.pressed = booleanSupplier;
    }
    
    @Override
    public void tick() {
        if(TinTeaTech.clientTicks%3==0){
            cd = pressed.getAsBoolean() ? 1 : 0;
        }
    }
}
