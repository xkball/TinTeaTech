package com.xkball.tin_tea_tech.mixin;

import com.xkball.tin_tea_tech.TinTeaTech;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlintAndSteelItem.class)
public class TTMixinFlintAndSteel {
    
    @Inject(method = "useOn",
            at = @At(
                value = "INVOKE",
                shift = At.Shift.BEFORE,
                target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"),
            cancellable = true
    )
    public void onUseOn(UseOnContext pContext, CallbackInfoReturnable<InteractionResult> cir){
        var level = pContext.getLevel();
        if(!level.isClientSide){
            var i = TinTeaTech.random.nextInt(10);
            if(i>2){
                var player = pContext.getPlayer();
                ItemStack itemstack = pContext.getItemInHand();
                if (player instanceof ServerPlayer) {
                    itemstack.hurtAndBreak(1, player, (player1) -> {
                        player1.broadcastBreakEvent(pContext.getHand());
                    });
                }
                cir.setReturnValue(InteractionResult.CONSUME);
                cir.cancel();
            }
        }
        else {
            cir.setReturnValue(InteractionResult.SUCCESS);
            cir.cancel();
        }
    }
}
