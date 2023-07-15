package com.xkball.tin_tea_tech.network;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.network.packet.MTEClientToServerDataPacket;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class TTNetworkHandler {
    public static final String VERSION = "1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            TinTeaTech.ttResource("network"),() -> VERSION,
            VERSION::equals,VERSION::equals
    );
    
    public static SimpleChannel instance() {
        return CHANNEL;
    }
    
    public static void sentToServer(Object packet){
        CHANNEL.sendToServer(packet);
    }
    
    public static void init(){
        CHANNEL.registerMessage(0,
                MTEClientToServerDataPacket.class,
                MTEClientToServerDataPacket::serialize,
                MTEClientToServerDataPacket::new,
                MTEClientToServerDataPacket::handle);
    }
    
}
