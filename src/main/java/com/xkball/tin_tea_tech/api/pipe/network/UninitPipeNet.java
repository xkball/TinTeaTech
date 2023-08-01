package com.xkball.tin_tea_tech.api.pipe.network;

import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.MTEPipe;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import net.minecraft.core.BlockPos;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

//只应该存在最多2tick
public class UninitPipeNet implements PipeNet{
    
    protected final MTEPipe center;
    protected final BlockPos centerPos;
    protected boolean firstTicked = false;
    
    public UninitPipeNet(MTEPipe center,BlockPos centerPos) {
        this.center = center;
        this.centerPos = centerPos;
    }
    
    
    @Override
    public void tick(BlockPos pos) {
        //在此进行初始化
        //第一tick
        if(!firstTicked){
            //情况1 存在网络且不是自己
            if(!center.getPos().equals(centerPos)) {
                firstTicked = true;
                return;
            }
            //情况2 网络中心是自己
            //两种子情况
            //当加载时和当放置时
            var handled = false;
            //边上存在现成的网络
            for(var mte : LevelUtils.getConnected(center.getLevel(),center.getPos(),false)){
                if (mte.getBelongs().workable()){
                    mte.getBelongs().combine(center.getPos());
                    //centerMTE.setBelongs(mte.getBelongs())
                    handled = true;
                }
            }
            if(!handled){
                center.setBelongs(center.createPipeNet());
            }
            firstTicked = true;
        }
        //第二tick
        else {
            //将MTE的Net设置为已经存在的网络
            var mte = LevelUtils.getMTE(center.getLevel(),centerPos);
            if(mte instanceof MTEPipe pipe && !center.getPos().equals(centerPos)){
                pipe.getBelongs().combine(center.getPos());
            }
            //不应该执行?
            else {
                center.setBelongs(center.createPipeNet());
            }
        }
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    @Override
    public Collection<MTEPipe> getConnected() {
        return List.of(center);
    }
    
    @Override
    public Map<BlockPos, MTEPipe> getConnectedRaw() {
        return Collections.emptyMap();
    }
    
    @Override
    public MTEPipe getCenter() {
        return center;
    }
    
    @Override
    public PipeNet combine(BlockPos other) {
        return this;
    }
    
    @Override
    public void cut(BlockPos pos) {
    
    }
    
    @Override
    public boolean workable() {
        return false;
    }
}
