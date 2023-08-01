package com.xkball.tin_tea_tech.mixin.entity;

import com.xkball.tin_tea_tech.common.item_behaviour.EntityControllerBehaviour;
import com.xkball.tin_tea_tech.utils.ItemUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class TTMixinLivingEntity extends Entity {
    
    
    public TTMixinLivingEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    
    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        var entity = this.getFirstPassenger();
        while (entity != null){
            if(entity instanceof Player player){
                if(ItemUtils.holdingItem(player, EntityControllerBehaviour.class)){
                    return player;
                }
            }
            entity = entity.getFirstPassenger();
        }
        return null;
    }
}
