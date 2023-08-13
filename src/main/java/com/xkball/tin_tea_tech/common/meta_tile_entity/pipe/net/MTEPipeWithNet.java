package com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.net;

import com.xkball.tin_tea_tech.api.data.DataProvider;
import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.api.pipe.network.EmptyPipeNet;
import com.xkball.tin_tea_tech.api.pipe.network.PipeNet;
import com.xkball.tin_tea_tech.api.pipe.network.PipeNetImpl;
import com.xkball.tin_tea_tech.api.pipe.network.UninitPipeNet;
import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.MTEPipe;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MTEPipeWithNet extends MTEPipe implements DataProvider {
    
    @Nonnull
    protected PipeNet belongs;
    @Nullable
    private BlockPos netCenter = null;
    private CompoundTag toLoadNet = new CompoundTag();
    
    
    
    public MTEPipeWithNet(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
        belongs = new EmptyPipeNet(this);
    }
    
    
    @Override
    public void tick() {
        super.tick();
        belongs.tick(getPos());
    }
    
    @Override
    public void writeInitData(CompoundTag tag) {
        super.writeInitData(tag);
        var netCenter = belongs.getCenter().getPos();
        tag.putInt("netCenterX", netCenter.getX());
        tag.putInt("netCenterY",netCenter.getY());
        tag.putInt("netCenterZ",netCenter.getZ());
        tag.put("net",belongs.save(getPos()));
    }
    
    @Override
    public void readInitData(CompoundTag tag) {
        super.readInitData(tag);
        if(tag.contains("netCenterX")){
            var x = tag.getInt("netCenterX");
            var y = tag.getInt("netCenterY");
            var z = tag.getInt("netCenterZ");
            netCenter = new BlockPos(x,y,z);
        }
        toLoadNet = tag.getCompound("net");
    }
    
    @Override
    public void onRemove() {
        super.onRemove();
        belongs.cut(getPos());
        belongs = new EmptyPipeNet(this);
    }
    
    public PipeNet getBelongs() {
        return belongs;
    }
    
    public void setBelongs(PipeNet belongs) {
        this.belongs = belongs;
        markDirty();
    }
    
    @Override
    public void firstTick() {
        super.firstTick();
        if(havePipeNet()){
            belongs = new UninitPipeNet(this,netCenter == null?getPos():netCenter);
        }
    }
    
    @Override
    public void setBlocked(Connections connection) {
        super.setBlocked(connection);
        if(belongs instanceof PipeNetImpl pipeNet){
            pipeNet.IOChanged = true;
        }
    }
    
    @Override
    public void setConnection(Connections connection, boolean connected, boolean noticeNeighbor) {
        super.setConnection(connection, connected, noticeNeighbor);
        if (isConnected(connection) && isNeighborConnected(connection)) {
            belongs.combine(getPos(connection));
        }
        else {
            belongs.checkNet(true);
        }
    }
    
    @Override
    public void setConnection(Connections connection) {
        super.setConnection(connection);
        if (isConnected(connection) && isNeighborConnected(connection)) {
            belongs.combine(getPos(connection));
        }
        else {
            belongs.checkNet(true);
        }
    }
    
    @Override
    public void onNeighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.onNeighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        if(havePipeNet() && belongs instanceof PipeNetImpl net){
            net.IOChanged = true;
        }
    }
    
    @Override
    public Collection<Component> getInfo() {
        var result = new ArrayList<Component>();
        if(!havePipeNet()) result.add(Component.literal("no pipe net"));
        else {
            result.add(Component.literal("pipe net center: "+belongs.getCenter().getPos().toString()));
            if(belongs.getCenter().getPos() == this.getPos()){
                result.add(Component.literal("this is the net center"));
            }
        }
        return result;
    }
    
    public CompoundTag getToLoadNet() {
        return toLoadNet;
    }
    
    @Nullable
    public MTEPipeWithNet getPipeWithNetAt(@Nullable Level level,BlockPos pos,boolean requireSame){
        if(level!=null){
            var mte = LevelUtils.getMTE(level,pos);
            if(mte instanceof MTEPipeWithNet pipe){
                if(!requireSame) return pipe;
                if(pipe.getClass().equals(this.getClass())) return pipe;
            }
        }
        return null;
    }
    
    @Nullable
    public MTEPipeWithNet getPipeWithNetAt(Connections connections,boolean requireSame){
        return getPipeWithNetAt(getLevel(),getPos(connections),requireSame);
    }
    
    public PipeNet createPipeNet(){
        return PipeNet.create(this);
    }
}
