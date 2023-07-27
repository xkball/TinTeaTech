package com.xkball.tin_tea_tech.network.packet;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.network.ITTPacket;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenGUIPacket implements ITTPacket {
    
    final int guiType;
    
    final CompoundTag data;
    
    public OpenGUIPacket(int guiType, CompoundTag data) {
        this.guiType = guiType;
        this.data = data;
    }
    
    public OpenGUIPacket(FriendlyByteBuf byteBuf){
        guiType = byteBuf.readInt();
        data = byteBuf.readAnySizeNbt();
    }
    
    @Override
    public void serialize(FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(guiType);
        byteBuf.writeNbt(data);
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        var con = context.get();
        con.enqueueWork(
                () -> {
                    if(TinTeaTech.isClient()){
                        var mc = Minecraft.getInstance();
                        if(mc.level != null && mc.player != null && mc.player.isAlive()){
                            mc.setScreen(AutoRegManager.ttGuiMap.get(guiType).newInstance(data));
                        }
                    }
                }
        );
        con.setPacketHandled(true);
    }
}
