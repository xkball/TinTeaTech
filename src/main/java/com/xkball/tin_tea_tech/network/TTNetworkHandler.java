package com.xkball.tin_tea_tech.network;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.network.packet.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
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
    
    public static void sentToClientPlayer(Object packet, Player player){
        instance().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),packet);
    }
    
    public static void init(){
        CHANNEL.registerMessage(0,
                MTEClientToServerDataPacket.class,
                MTEClientToServerDataPacket::serialize,
                MTEClientToServerDataPacket::new,
                MTEClientToServerDataPacket::handle);
        CHANNEL.registerMessage(1,
                OpenGUIPacket.class,
                OpenGUIPacket::serialize,
                OpenGUIPacket::new,
                OpenGUIPacket::handle);
        CHANNEL.registerMessage(2,
                SyncGUIDataPacket.class,
                SyncGUIDataPacket::serialize,
                SyncGUIDataPacket::new,
                SyncGUIDataPacket::handle);
        CHANNEL.registerMessage(3,
                ScanPacket.class,
                ScanPacket::serialize,
                ScanPacket::new,
                ScanPacket::handle);
        CHANNEL.registerMessage(4,
                KeyPressToServerPacket.class,
                KeyPressToServerPacket::serialize,
                KeyPressToServerPacket::new,
                KeyPressToServerPacket::handle);
    }
    
}
