package com.xkball.tin_tea_tech.mixin.client;

import com.xkball.tin_tea_tech.common.player.AdditionalInventory;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import com.xkball.tin_tea_tech.network.packet.SyncGUIDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class TTMixinMPGameMode {
    
    @Inject(method = "handleCreativeModeItemAdd",at = @At("HEAD"))
    public void onSetItem(ItemStack pStack, int pSlotId, CallbackInfo ci){
        var mc = Minecraft.getInstance();
        var screen = mc.screen;
        if(screen instanceof CreativeModeInventoryScreen creativeScreen){
            var slots = creativeScreen.getMenu().slots;
            if(pSlotId >= slots.size()) return;
            var slot = slots.get(pSlotId);
            if(slot.container instanceof AdditionalInventory){
                TTNetworkHandler.CHANNEL.sendToServer(new SyncGUIDataPacket(slot.getSlotIndex()+10000,pStack));
            }
        }
    }
}
