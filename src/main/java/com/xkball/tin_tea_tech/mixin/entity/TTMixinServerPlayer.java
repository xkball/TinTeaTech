package com.xkball.tin_tea_tech.mixin.entity;

import com.mojang.authlib.GameProfile;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.common.player.IExtendedPlayer;
import com.xkball.tin_tea_tech.common.player.PlayerData;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import com.xkball.tin_tea_tech.network.packet.SyncGUIDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class TTMixinServerPlayer extends Player {
    
    //仅服务端用到
    @Unique
    private boolean tin_tea_tech$needUpdate = true;
    
    public TTMixinServerPlayer(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile) {
        super(pLevel, pPos, pYRot, pGameProfile);
    }
    
    @Inject(method = "tick",at = @At("HEAD"))
    public void onTick(CallbackInfo ci){
        if(tin_tea_tech$needUpdate){
            if(!this.level().isClientSide){
                var tin_tea_tech$playerData = PlayerData.get(this);
                tin_tea_tech$playerData.hgPluginMap.clear();
                var i1 = ((IExtendedPlayer)this).getHeadItem();
                tin_tea_tech$playerData.loadHGPDataFromItem(i1);
                var i2 = getItemBySlot(EquipmentSlot.HEAD);
                tin_tea_tech$playerData.loadHGPDataFromItem(i2);
                var tin_tea_tech$additionalInventory =((IExtendedPlayer)this).getAdditionalInventory();
                //noinspection DataFlowIssue
                TTNetworkHandler.sentToClientPlayer(new SyncGUIDataPacket(tin_tea_tech$playerData),
                        (ServerPlayer)(Object)this);
                //noinspection DataFlowIssue
                TTNetworkHandler.sentToClientPlayer(new SyncGUIDataPacket(10000,tin_tea_tech$additionalInventory.getItem(0)),
                        (ServerPlayer)(Object)this);
            }
            tin_tea_tech$needUpdate = false;
        }
        if(TinTeaTech.ticks%10==0){
            var tin_tea_tech$playerData = PlayerData.get(this);
            var b = getControlledVehicle() instanceof Player && tin_tea_tech$playerData.allowRidingByPlayer;
            if(b != tin_tea_tech$playerData.controlled) {
                tin_tea_tech$playerData.controlled = b;
                TTNetworkHandler.sentToClientPlayer(new SyncGUIDataPacket(tin_tea_tech$playerData),
                        (ServerPlayer) (Object) this);
            }
        }
    }
}
