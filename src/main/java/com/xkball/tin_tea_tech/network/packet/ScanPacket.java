package com.xkball.tin_tea_tech.network.packet;

import com.xkball.tin_tea_tech.api.network.ITTPacket;
import com.xkball.tin_tea_tech.client.hud.TricorderHUD;
import com.xkball.tin_tea_tech.common.item_behaviour.TricorderBehaviour;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Supplier;

public class ScanPacket implements ITTPacket {
    
    final BlockPos blockPos;
    final LinkedList<Component> result;
    
    public ScanPacket(BlockPos blockPos, Collection<Component> result) {
        this.blockPos = blockPos;
        this.result = new LinkedList<>();
        this.result.addAll(result);
    }
    
    public ScanPacket(BlockPos blockPos) {
        this.blockPos = blockPos;
        this.result = null;
    }
    
    public ScanPacket(FriendlyByteBuf byteBuf){
        this.blockPos = byteBuf.readBlockPos();
        result = new LinkedList<>();
        if(byteBuf.readBoolean()){
            var tag = byteBuf.readAnySizeNbt();
            if (tag != null) {
                var max= tag.getInt("l");
                for(int i=0;i<max;i++)
                {
                    var s = tag.getString(String.valueOf(i));
                    result.add(Component.Serializer.fromJson(s));
                }
            }
        }
    }
    
    @Override
    public void serialize(FriendlyByteBuf byteBuf) {
        byteBuf.writeBlockPos(blockPos);
        if(result != null){
            byteBuf.writeBoolean(true);
            var tag = new CompoundTag();
            var i = 0;
            for(var c : result){
                tag.putString(String.valueOf(i),Component.Serializer.toJson(c));
                i++;
            }
            tag.putInt("l",result.size());
            byteBuf.writeNbt(tag);
        }
        byteBuf.writeBoolean(false);
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(
                () ->{
                    if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                        TricorderHUD.toDraw.clear();
                        TricorderHUD.toDraw.addAll(result);
                    }
                    else {
                        var player = context.get().getSender();
                        if(player != null){
                            var level = player.level();
                            if(level.isLoaded(blockPos)){
                                var r = TricorderBehaviour.scan(level,blockPos,player,false);
                                TTNetworkHandler.sentToClientPlayer(new ScanPacket(blockPos,r),player);
                            }
                          }
                    }
                }
        );
        context.get().setPacketHandled(true);
    }
}
