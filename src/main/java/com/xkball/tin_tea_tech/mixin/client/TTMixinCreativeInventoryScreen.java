package com.xkball.tin_tea_tech.mixin.client;


import com.xkball.tin_tea_tech.common.player.IExtendedPlayer;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class TTMixinCreativeInventoryScreen extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    
    
    @Unique
    private static Constructor<?> tin_tea_tech$constructor;
    
    @Shadow private static CreativeModeTab selectedTab;
    
    public TTMixinCreativeInventoryScreen(CreativeModeInventoryScreen.ItemPickerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    
    @Inject(method = "selectTab",at = @At("RETURN"))
    public void onChangeTab(CreativeModeTab pTab, CallbackInfo ci){
        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY){
            @SuppressWarnings("DataFlowIssue")
            var con = ((IExtendedPlayer)this.minecraft.player).getAdditionalInventory();
            try {
                this.menu.slots.add((Slot) tin_tea_tech$constructor.newInstance(new Slot(con,0,54+18,6),0,54+18,6));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static {
        var c = CreativeModeInventoryScreen.class;
        for(var cc : c.getDeclaredClasses()){
            if(Slot.class.isAssignableFrom(cc)){
                try {
                    tin_tea_tech$constructor = cc.getConstructor(Slot.class,int.class,int.class,int.class);
                    tin_tea_tech$constructor.setAccessible(true);
                    break;
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
