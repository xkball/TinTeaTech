package com.xkball.tin_tea_tech.network.packet;

import com.xkball.tin_tea_tech.api.data.LatitudeAndLongitudeAccess;
import com.xkball.tin_tea_tech.api.network.ITTPacket;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncLatitudeAndLongitudePacket implements ITTPacket {
    private final CompoundTag tag;
    
    public SyncLatitudeAndLongitudePacket(ServerLevel level) {
        this.tag = ((LatitudeAndLongitudeAccess)level).tin_tea_tech$getLALSelf().save(new CompoundTag());
    }
    
    public SyncLatitudeAndLongitudePacket(CompoundTag tag) {
        this.tag = tag;
    }
    
    public SyncLatitudeAndLongitudePacket(FriendlyByteBuf byteBuf) {
        this.tag = byteBuf.readAnySizeNbt();
    }
    
    @Override
    public void serialize(FriendlyByteBuf byteBuf) {
        byteBuf.writeNbt(tag);
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(
                ()->{
                    if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()-> ()-> {
                            var level = Minecraft.getInstance().level;
                            if (level != null) {
                                ((LatitudeAndLongitudeAccess)level).tin_tea_tech$reInitLatitudeAndLongitude(tag);
                            }
                        });
                        
                    }
                    else{
                        var player = context.get().getSender();
                        if(player != null){
                            var level = player.serverLevel();
                            TTNetworkHandler.CHANNEL.send(
                                    PacketDistributor.DIMENSION.with(level::dimension),
                                    new SyncLatitudeAndLongitudePacket(((LatitudeAndLongitudeAccess)level).tin_tea_tech$getLALSelf().save(new CompoundTag())));
                            
                        }
                    }
                }
        );
        context.get().setPacketHandled(true);
    }
}
