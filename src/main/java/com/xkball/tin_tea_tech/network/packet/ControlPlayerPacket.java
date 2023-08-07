package com.xkball.tin_tea_tech.network.packet;

import com.xkball.tin_tea_tech.api.network.ITTPacket;
import com.xkball.tin_tea_tech.common.player.PlayerData;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import com.xkball.tin_tea_tech.utils.TTUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ControlPlayerPacket implements ITTPacket {
    
    public final float leftImpulse;
    public final float forwardImpulse;
    
    public ControlPlayerPacket(float leftImpulse, float forwardImpulse) {
        this.leftImpulse = leftImpulse;
        this.forwardImpulse = forwardImpulse;
    }
    
    public ControlPlayerPacket(FriendlyByteBuf byteBuf){
        this.leftImpulse = byteBuf.readFloat();
        this.forwardImpulse = byteBuf.readFloat();
    }
    
    @Override
    public void serialize(FriendlyByteBuf byteBuf) {
        byteBuf.writeFloat(leftImpulse);
        byteBuf.writeFloat(forwardImpulse);
    }
    
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(
                () -> {
                    if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                        var pd = PlayerData.get(TTUtils.getPlayer());
                        pd.leftImpulse = this.leftImpulse;
                        pd.forwardImpulse = this.forwardImpulse;
                        return;
                    }
                    var player = context.get().getSender();
                    if (player != null) {
                        var vehicle = player.getVehicle();
                        while (vehicle != null){
                            var buff = vehicle;
                            vehicle = vehicle.getVehicle();
                            if(vehicle == null){
                                vehicle = buff;
                                break;
                            }
                        }
                        if(vehicle instanceof Player v){
                            var pd = PlayerData.get(v);
                            if(!pd.controlled) return;
                            pd.leftImpulse = this.leftImpulse;
                            pd.forwardImpulse = this.forwardImpulse;
                            TTNetworkHandler.sentToClientPlayer(new ControlPlayerPacket(this.leftImpulse,this.forwardImpulse), v);
                        }
                    }
                }
        );
        context.get().setPacketHandled(true);
    }
}
