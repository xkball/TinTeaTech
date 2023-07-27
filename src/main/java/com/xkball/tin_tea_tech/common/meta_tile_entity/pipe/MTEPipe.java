package com.xkball.tin_tea_tech.common.meta_tile_entity.pipe;

import com.xkball.tin_tea_tech.api.TTValue;
import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import com.xkball.tin_tea_tech.utils.TTUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.Collection;

public abstract class MTEPipe extends MetaTileEntity {
    
    protected BitSet connections = new BitSet();
    
    public MTEPipe(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
    }
    
    @Override
    public void writeInitData(CompoundTag tag) {
        super.writeInitData(tag);
        tag.putInt("bitsetL",connections.length());
        tag.putLong("bitset", TTUtils.longValueOfBitSet(connections));
    }
    
    @Override
    public void readInitData(CompoundTag tag) {
        super.readInitData(tag);
        var bl = tag.getInt("bitsetL");
        TTUtils.forLongToBitSet(tag.getLong("bitset"),bl,connections);
    }
    
    public boolean isConnected(Connections connection){
        return connections.get(connection.toCID());
    }
    
    public boolean isBlocked(Connections connection){
        return connections.get(connection.toBID());
    }
    
    @Override
    public InteractionResult useByWrench(Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        var con = LevelUtils.useConnection(pHit);
     //   pPlayer.sendSystemMessage(Component.literal(con.toString()));
        if(!pPlayer.isShiftKeyDown()){
            setConnection(con);
        }
        else if(isConnected(con)){
            setBlocked(con);
        }
        return InteractionResult.SUCCESS;
    }
    
    public void setBlocked(Connections connection){
        connections.set(connection.toBID(),!isBlocked(connection));
        this.sentCustomData(TTValue.DATA_UPDATE,(b) -> {
            b.writeLong(TTUtils.longValueOfBitSet(connections));
            b.writeInt(connections.length());
        });
        markDirty();
    }
    
    public void setConnection(Connections connection,boolean connected){
        if(isConnected(connection) != connected){
            connections.set(connection.toCID(),connected);
            this.sentCustomData(TTValue.DATA_UPDATE,(b) -> {
                b.writeLong(TTUtils.longValueOfBitSet(connections));
                b.writeInt(connections.length());
            });
            markDirty();
        }
    }
    
    public void setConnection(Connections connection){
        connections.set(connection.toCID(),!isConnected(connection));
        this.sentCustomData(TTValue.DATA_UPDATE,(b) -> {
            b.writeLong(TTUtils.longValueOfBitSet(connections));
            b.writeInt(connections.length());
        });
        markDirty();
    }
    
    @Override
    public void syncRenderData() {
        this.sentCustomData(TTValue.DATA_UPDATE,(b) -> {
            b.writeLong(TTUtils.longValueOfBitSet(connections));
            b.writeInt(connections.length());
        });
        
    }
    
    @Override
    public void readCustomData(int id, ByteBuf byteBuf) {
        super.readCustomData(id, byteBuf);
        if(id == TTValue.DATA_UPDATE){
            TTUtils.forLongToBitSet(byteBuf.readLong(),byteBuf.readInt(),connections);
        }
    }
    
    public boolean haveConnection(Collection<Connections> connections){
        for(var c: connections){
            if(isConnected(c)) return true;
        }
        return false;
    }
    
    public boolean haveConnection(){
        return haveConnection(Connections.values);
    }
}
