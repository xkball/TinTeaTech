package com.xkball.tin_tea_tech.client.gui.components.button;

import com.xkball.tin_tea_tech.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BooleanSupplier;

@ParametersAreNonnullByDefault
public class EnableButton extends Button{
    
    private final BooleanSupplier enabled;
    public EnableButton(Runnable recall, BooleanSupplier enabled) {
        super(Component.literal(""), recall);
        this.enabled = enabled;
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        drawBG(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        pGuiGraphics.drawString(Minecraft.getInstance().font,enabled.getAsBoolean()?ON:OFF,getX()+5,getY()+getH()/2-4, ColorUtils.getColor(255,255,255,255));
    }
}
