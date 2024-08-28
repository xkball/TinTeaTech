package com.xkball.tin_tea_tech.network.packet;

import com.xkball.tin_tea_tech.api.network.ITTPacket;
import com.xkball.tin_tea_tech.client.hud.TricorderHUD;
import com.xkball.tin_tea_tech.common.item_behaviour.TricorderBehaviour;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class ScanPacket implements ITTPacket {
    
    final boolean scanBlock;
    
    UUID uuid;
    
    BlockPos blockPos;
    LinkedList<Component> result;
    
    public ScanPacket(UUID uuid){
        this.scanBlock = false;
        this.uuid = uuid;
    }
    
    public ScanPacket(boolean scanBlock,Collection<Component> result) {
        this.result = new LinkedList<>();
        this.result.addAll(result);
        this.scanBlock = scanBlock;
    }
    
    public ScanPacket(BlockPos blockPos) {
        this.blockPos = blockPos;
        this.result = null;
        this.scanBlock = true;
    }
    
    public ScanPacket(FriendlyByteBuf byteBuf){
        this.scanBlock = byteBuf.readBoolean();
        if(this.scanBlock){
            if(!byteBuf.readBoolean()){
                this.blockPos = byteBuf.readBlockPos();
            }
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
        else {
            if(!byteBuf.readBoolean()){
                this.uuid = byteBuf.readUUID();
            }
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
    }
    
    @Override
    public void serialize(FriendlyByteBuf byteBuf) {
        byteBuf.writeBoolean(scanBlock);
        if(scanBlock){
            var flag = blockPos == null;
            byteBuf.writeBoolean(flag);
            if(!flag){
                byteBuf.writeBlockPos(blockPos);
            }
            
        }
        else {
            var flag = uuid == null;
            byteBuf.writeBoolean(flag);
            if(!flag) {
                byteBuf.writeUUID(uuid);
            }
        }
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
                        if(scanBlock){
                            TricorderHUD.toDrawBlock.clear();
                            TricorderHUD.toDrawBlock.addAll(result);
                        }
                        else {
                            TricorderHUD.toDrawEntity.clear();
                            TricorderHUD.toDrawEntity.addAll(result);
                        }
                    }
                    else {
                        var player = context.get().getSender();
                        if(player != null){
                            var level = player.level();
                            Collection<Component> r = null;
                            if(scanBlock && level.isLoaded(blockPos)){
                                r = TricorderBehaviour.scanBlock(level,blockPos,player,false);
                               
                            }
                            else {
                                if(level instanceof ServerLevel serverLevel){
                                    var entity = serverLevel.getEntities().get(uuid);
                                    if (entity != null) {
                                        r = List.of(Component.literal(entity.saveWithoutId(new CompoundTag()).toString()));
                                    }
                                }
                            }
                            if(r != null) TTNetworkHandler.sentToClientPlayer(new ScanPacket(scanBlock,r),player);
                          }
                    }
                }
        );
        context.get().setPacketHandled(true);
    }
}
