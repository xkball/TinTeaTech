package com.xkball.tin_tea_tech.mixin.client;


import com.mojang.logging.LogUtils;
import com.xkball.tin_tea_tech.common.player.AdditionalInventory;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class TTMixinCreativeInventoryScreen extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
   // @Shadow private static CreativeModeTab selectedTab;
    
    public TTMixinCreativeInventoryScreen(CreativeModeInventoryScreen.ItemPickerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    
//    @Inject(method = "selectTab",at = @At("RETURN"))
//    public void onChangeTab(CreativeModeTab pTab, CallbackInfo ci){
//        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY){
//            @SuppressWarnings("DataFlowIssue")
//            var con = ((IExtendedPlayer)this.minecraft.player).getAdditionalInventory();
//            try {
//                var menu = this.minecraft.player.inventoryMenu;
//                var i = menu.slots.stream()
//                        .filter((s) -> s.container instanceof AdditionalInventory)
//                        .findFirst()
//                        .orElseGet(() -> menu.getSlot(0)).index;
//                var slotIn = new Slot(con,i,54+18,6);
//                slotIn.index = 1;
//                this.menu.slots.add((Slot) tin_tea_tech$constructor.newInstance(slotIn,i,54+18,6));
//            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Redirect(method = "selectTab",
            at = @At(value = "INVOKE",target = "Lnet/minecraft/core/NonNullList;add(Ljava/lang/Object;)Z"),
            remap = false)
    public boolean tryAdd(NonNullList instance, Object o){
        if(o instanceof CreativeModeInventoryScreen.SlotWrapper wrapper){
            try{
                Slot slot = (Slot) o;
                if(slot.container instanceof AdditionalInventory && slot.y != 6){
                    var i = wrapper.target.index;
                    this.menu.slots.add(new CreativeModeInventoryScreen.SlotWrapper(wrapper.target,i,54+18,6));
                    return false;
                }
            }catch (Exception e){
                LogUtils.getLogger().error("should not happen",e);
            }
        }
        return instance.add(o);
    }
}
