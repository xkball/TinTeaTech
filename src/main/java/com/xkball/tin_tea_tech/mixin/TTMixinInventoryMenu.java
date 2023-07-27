package com.xkball.tin_tea_tech.mixin;

import com.xkball.tin_tea_tech.common.player.IExtendedPlayer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(InventoryMenu.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class TTMixinInventoryMenu extends RecipeBookMenu<CraftingContainer> {
    
    public TTMixinInventoryMenu(MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }
    
    @Inject(method = "<init>",at = @At("RETURN"))
    public void onCon(Inventory pPlayerInventory, boolean pActive, Player pOwner, CallbackInfo ci){
        this.addSlot(new Slot(((IExtendedPlayer)pOwner).getAdditionalInventory(),0,26,8));
    }
}
