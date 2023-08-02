package com.xkball.tin_tea_tech.network.packet;

import com.xkball.tin_tea_tech.api.network.ITTPacket;
import com.xkball.tin_tea_tech.common.player.PlayerData;
import com.xkball.tin_tea_tech.utils.TTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SyncGUIDataPacket implements ITTPacket {
    
    
    //private static HandlePacket handler = new HandlePacket();
    
    final Type type;
    private final CompoundTag data;
    
    private Object[] typeData = new Object[1];
    
    
    public SyncGUIDataPacket(BlockPos blockPos, CompoundTag data){
        this.type = Type.Block;
        typeData[0] = blockPos;
        this.data = data;
    }
    
    public SyncGUIDataPacket(int slot,CompoundTag data){
        this.type = Type.Item;
        typeData[0] = slot;
        this.data = data;
    }
    
    public SyncGUIDataPacket(int slot,ItemStack itemStack){
        this.type = Type.Inventory;
        this.data = new CompoundTag();
        data.putInt("slot",slot);
        data.put("item",itemStack.save(new CompoundTag()));
    }
    
    public SyncGUIDataPacket(PlayerData playerData){
        this.type = Type.PlayerData;
        this.data = playerData.serializeNBT();
    }
    
    public SyncGUIDataPacket(FriendlyByteBuf friendlyByteBuf){
        this.data = friendlyByteBuf.readAnySizeNbt();
        this.type = Type.get(friendlyByteBuf.readInt());
        type.read.accept(friendlyByteBuf,this);
    }
    
    
    @Override
    public void serialize(FriendlyByteBuf byteBuf) {
        byteBuf.writeNbt(data);
        byteBuf.writeInt(type.index);
        type.write.accept(byteBuf,this);
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(
                () -> HandlePacket.handle(context.get(),this)
                
        );
        context.get().setPacketHandled(true);
    }
    
    public enum Type{
        NULL(0,(b,p) -> {},(b,p) -> {},(b,p) -> {}),
        Block(1,(b,p) -> p.typeData[0] = b.readBlockPos(),
                (b,p) -> b.writeBlockPos((BlockPos) p.typeData[0]),
                (b,p) -> {}),
        Item(2, (b,p) -> p.typeData[0] = b.readInt(),
                (b,p) -> b.writeInt((Integer) p.typeData[0]),
                (b, p) ->{
            var data = p.data;
            var item = b.getSlot((Integer) p.typeData[0]).get();
            if(item.is(ItemStack.of(data.getCompound("openedFromItem")).getItem())){
                 item.setTag(p.data.getCompound("itemTag"));
            }
          
        }),
        Inventory(3,(b,p) -> {},(b,p) -> {},
                (b,p) -> {
                var slot = p.data.getInt("slot");
                var item = ItemStack.of(p.data.getCompound("item"));
                b.getSlot(slot).set(item);
                }),
        PlayerData(4,(b,p) -> {},(b,p) -> {},
                (b,p) -> com.xkball.tin_tea_tech.common.player.PlayerData.get(b).deserializeNBT(p.data));
        final int index;
        final BiConsumer<FriendlyByteBuf,SyncGUIDataPacket> read;
        final BiConsumer<FriendlyByteBuf,SyncGUIDataPacket> write;
        final BiConsumer<Player,SyncGUIDataPacket> handle;
        
        Type(int index, BiConsumer<FriendlyByteBuf, SyncGUIDataPacket> read, BiConsumer<FriendlyByteBuf, SyncGUIDataPacket> write, BiConsumer<Player, SyncGUIDataPacket> handle) {
            this.index = index;
            
            this.read = read;
            this.write = write;
            this.handle = handle;
        }
        
        public static Type get(int index){
            if(index>=0 && index<values().length){
                return values()[index];
            }
            return NULL;
        }
    }
    
    public static class HandlePacket {
        public static void handle(NetworkEvent.Context context, SyncGUIDataPacket packet) {
                if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                    packet.type.handle.accept(TTUtils.getPlayer(), packet);
                    return;
                }
                var player = context.getSender();
                if (player != null) {
                    packet.type.handle.accept(player, packet);
                }
        }
        
    }
}

