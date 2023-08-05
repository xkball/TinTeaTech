package com.xkball.tin_tea_tech.network.packet;

import com.xkball.tin_tea_tech.api.network.ITTPacket;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class MTEClientToServerDataPacket implements ITTPacket {
    final BlockPos blockPos;
    final CompoundTag data;
    
    public MTEClientToServerDataPacket(FriendlyByteBuf byteBuf){
        blockPos = byteBuf.readBlockPos();
        data = byteBuf.readAnySizeNbt();
    }
    
    public MTEClientToServerDataPacket(BlockPos pos,CompoundTag data){
        this.blockPos = pos;
        this.data = data;
    }
    
    @Override
    public void serialize(FriendlyByteBuf byteBuf) {
        byteBuf.writeBlockPos(blockPos);
        byteBuf.writeNbt(data);
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(
                () ->
                {
                    var player = context.get().getSender();
                    if(player != null){
                        var level = player.level();
                        if(!level.isLoaded(blockPos)) return;
                        var te = level.getBlockEntity(blockPos);
                        if(te instanceof TTTileEntityBase){
                            ((TTTileEntityBase) te).getMte().readClientData(data);
                        }
                    }
                }
        );
    }
}
