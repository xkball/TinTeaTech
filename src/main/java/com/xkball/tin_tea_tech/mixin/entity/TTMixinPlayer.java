package com.xkball.tin_tea_tech.mixin.entity;

import com.xkball.tin_tea_tech.common.player.AdditionalInventory;
import com.xkball.tin_tea_tech.common.player.IExtendedPlayer;
import com.xkball.tin_tea_tech.common.player.PlayerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
@SuppressWarnings("AddedMixinMembersNamePattern")
public abstract class TTMixinPlayer extends LivingEntity implements IExtendedPlayer {
    
    @Shadow public abstract boolean canBeSeenAsEnemy();
    
    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot pSlot);
    
    @Unique
    public final AdditionalInventory tin_tea_tech$additionalInventory = new AdditionalInventory();
    
    @Unique
    public final PlayerData tin_tea_tech$playerData = new PlayerData();
    
    
    protected TTMixinPlayer(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    
    @Override
    public AdditionalInventory getAdditionalInventory() {
        return tin_tea_tech$additionalInventory;
    }
    
    @Override
    public ItemStack getHeadItem() {
        return tin_tea_tech$additionalInventory.getItem(0);
    }
    
    @Override
    public PlayerData getPlayerData() {
        return tin_tea_tech$playerData;
    }
    
    @Inject(method = "addAdditionalSaveData",at = @At("RETURN"))
    public void onSave(CompoundTag pCompound, CallbackInfo ci){
        pCompound.put("ttt_additionalInventory", tin_tea_tech$additionalInventory.createTag());
        pCompound.put("ttt_playerData", tin_tea_tech$playerData.serializeNBT());
    }
    
    @Inject(method = "readAdditionalSaveData",at = @At("RETURN"))
    public void onLoad(CompoundTag pCompound, CallbackInfo ci) {
        if(pCompound.contains("ttt_additionalInventory")){
            tin_tea_tech$additionalInventory.fromTag( pCompound.getList("ttt_additionalInventory",10));
            
        }
        if(pCompound.contains("ttt_playerData")){
            tin_tea_tech$playerData.deserializeNBT(pCompound.getCompound("ttt_playerData"));
        }
       
    }
    
    @Inject(method = "getSlot",at = @At("HEAD"),cancellable = true)
    public void onGetSlot(int pSlot, CallbackInfoReturnable<SlotAccess> cir){
        if(pSlot>=10000){
            cir.setReturnValue(SlotAccess.forContainer(tin_tea_tech$additionalInventory,pSlot-10000));
            cir.cancel();
        }
    }
    
}
