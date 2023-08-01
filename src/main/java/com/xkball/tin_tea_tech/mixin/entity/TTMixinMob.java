package com.xkball.tin_tea_tech.mixin.entity;

import com.xkball.tin_tea_tech.common.item_behaviour.EntityControllerBehaviour;
import com.xkball.tin_tea_tech.utils.ItemUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin({Mob.class, Pig.class})
public abstract class TTMixinMob extends Entity {
    
    public TTMixinMob(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    
    @Inject(method = "getControllingPassenger",at = @At("HEAD"),cancellable = true)
    public void onGetControllingPassenger(CallbackInfoReturnable<LivingEntity> cir){
        var entity = this.getFirstPassenger();
        while (entity != null){
            if(entity instanceof Player player){
                if(ItemUtils.holdingItem(player, EntityControllerBehaviour.class)){
                    cir.setReturnValue(player);
                    cir.cancel();
                    return;
                }
            }
            entity = entity.getFirstPassenger();
        }
    }
}
