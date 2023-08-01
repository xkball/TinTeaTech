package com.xkball.tin_tea_tech.network.packet;

import com.xkball.tin_tea_tech.api.network.ITTPacket;
import com.xkball.tin_tea_tech.utils.DataUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KeyPressToServerPacket implements ITTPacket {
    
    private final String name;
    private final int action;
    private final int modifier;
    
    public KeyPressToServerPacket(String name, int action, int modifier) {
        this.name = name;
        this.action = action;
        this.modifier = modifier;
    }
    
    public KeyPressToServerPacket(FriendlyByteBuf byteBuf){
        this.name = DataUtils.readUTF8String(byteBuf);
        this.action = byteBuf.readInt();
        this.modifier = byteBuf.readInt();
    }
    
    
    @Override
    public void serialize(FriendlyByteBuf byteBuf) {
        DataUtils.writeUTF8String(byteBuf,name);
        byteBuf.writeInt(action);
        byteBuf.writeInt(modifier);
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(
                () ->{
                    if(action == 0 && ("key.forward".equals(name) || "key.left".equals(name) || "key.back".equals(name) || "key.right".equals(name))){
                        var player = context.get().getSender();
                        if(player != null){
                            player.setDeltaMovement(Vec3.ZERO);
                            
                           // player.setPose(Pose.STANDING);
                        }
                    }
                }
        );
        context.get().setPacketHandled(true);
    }
    
    
}
