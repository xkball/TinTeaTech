package com.xkball.tin_tea_tech.client.gui.components.button;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.client.gui.sub_screen.PlayerInventorySubScreen;
import com.xkball.tin_tea_tech.common.player.PlayerData;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class InventoryTabButton extends Button{
    
    private static final ResourceLocation bg = TinTeaTech.ttResource("textures/gui/inventory_tag.png");
    
    private final PlayerInventorySubScreen<?> target;
    public InventoryTabButton(PlayerInventorySubScreen<?> target) {
        super(Component.translatable("gui.tin_tea_tech.displayInventory"), () -> {
            target.setEnabled(!target.enabled());
            PlayerData.get(getPlayer()).setDisplayInventoryInGUI(target.enabled());
        });
        this.target = target;
        this.setSize(64,27);
    }
    
    @Override
    public void drawBG(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.blit(bg,getX(),getY(),0,0,64,27,64,27);
    }
    
    @Override
    public void resizeMax(int pWidth, int pHeight) {
        super.resizeMax(pWidth, pHeight);
        this.pos(0,pHeight-12);
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        drawBG(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        if(canInteractive(pMouseX,pMouseY))
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, text.get(), pMouseX, pMouseY);
        pGuiGraphics.renderFakeItem(Items.CHEST.getDefaultInstance(),getX()+5,getY()+5);
        pGuiGraphics.drawString(Minecraft.getInstance().font, target.enabled()?ON:OFF,getX()+25,getY()+5, ColorUtils.getColor(255,255,255,255));
    }
}
