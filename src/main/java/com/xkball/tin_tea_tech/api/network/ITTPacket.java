package com.xkball.tin_tea_tech.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface ITTPacket {
    void serialize(FriendlyByteBuf byteBuf);
    
    void handle(Supplier<NetworkEvent.Context> context);
}
