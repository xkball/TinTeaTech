package com.xkball.tin_tea_tech.api.pipe.network;

import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.MTEPipe;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Map;

public class PipeNetImpl implements PipeNet{
    
    protected final MTEPipe centerMTE;
    protected final Map<BlockPos,MTEPipe> connected = new Object2ObjectLinkedOpenHashMap<>();
    protected boolean needInit = true;
    protected BlockPos center;
    
    public PipeNetImpl(MTEPipe mte) {
        this.centerMTE = mte;
        this.center = mte.getPos();
        connected.put(mte.getPos(),mte);
    }
    
    @Override
    public void tick() {
        if(needInit){
            for(var mte : LevelUtils.getConnected(centerMTE.getLevel(),centerMTE.getPos())){
                if (mte.getBelongs() != null){
                    mte.getBelongs().combine(center);
                    //centerMTE.setBelongs(mte.getBelongs());
                    return;
                }
            }
            needInit = false;
        }
    }
    
    @Override
    public int size() {
        return connected.size();
    }
    
    @Override
    public Collection<MTEPipe> getConnected() {
        return connected.values();
    }
    
    @Override
    public MTEPipe getCenter() {
        return centerMTE;
    }
    
    @Override
    public PipeNet combine(BlockPos other) {
        if(connected.containsKey(other)) return this;
        var mte = LevelUtils.getMTE(getCenter().getLevel(),other);
        if(mte instanceof MTEPipe pipe){
            tryLink(pipe.getPos());
        }
        return this;
    }
    
    public void tryLink(BlockPos blockPos){
        for(var mte : LevelUtils.getConnected(getLevel(),blockPos)){
            if(mte.havePipeNet() ){
                var net = mte.getBelongs();
                if(net != this){
                    this.connected.putAll(net.getConnectedRaw());
                    for(var pipe:net.getConnected()){
                        pipe.setBelongs(this);
                    }
                }
            }
        }
    }
    
    public Level getLevel(){
        return getCenter().getLevel();
    }
    
    @Override
    public Map<BlockPos,MTEPipe> getConnectedRaw(){
        return connected;
    }
}
