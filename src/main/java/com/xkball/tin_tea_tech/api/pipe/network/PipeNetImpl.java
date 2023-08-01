package com.xkball.tin_tea_tech.api.pipe.network;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.MTEPipe;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PipeNetImpl implements PipeNet{
    
    protected final MTEPipe centerMTE;
    protected final Level level;
    protected final Map<BlockPos,MTEPipe> connected = new Object2ObjectLinkedOpenHashMap<>();
    protected BlockPos center;
    protected int lastTick = 0;
    protected boolean changed = false;
    public boolean IOChanged = true;
    
    private boolean firstTick = false;
    private final int offset;
    
    
    public PipeNetImpl(MTEPipe mte) {
        this.centerMTE = mte;
        this.level = mte.getLevel();
        this.center = mte.getPos();
        connected.put(mte.getPos(),mte);
        offset = ThreadLocalRandom.current().nextInt(20);
    }
    
    @Override
    public final void tick(BlockPos pos) {
        if(!firstTick){
            firstTick();
            firstTick = true;
        }
        if(!(pos == center)){
            if (!(lastTick != TinTeaTech.ticks && !level.isLoaded(center))){
                return;
            }
        }
        lastTick = TinTeaTech.ticks;
        doTick();
        checkNet(false);
    }
    
    public void firstTick(){
        load(getCenter().getToLoadNet());
        checkNet(true);
    }
    
    public void doTick(){
    
    }
    
    @Override
    public void checkNet(boolean force) {
        _checkNet(force);
    }
    
    public void _checkNet(boolean force, BlockPos... removed) {
        var needMarkDirty = false;
        if((getOffsetTick() % 100 == 0 && changed) || force){
            var s = new BFSSearcher();
            s.setRemoved(removed);
            while (s.hasNext()){
                s.next();
            }
            var result = new HashSet<>(s.getSearched());
            Map<BlockPos,MTEPipe> buff = new Object2ObjectLinkedOpenHashMap<>();
            for(var pos : connected.keySet()){
                var pipe = LevelUtils.getPipe(level,pos);
                if (result.contains(pos)) {
                    if(pipe != null){
                        buff.put(pos,pipe);
                    }
                    result.remove(pos);
                }
                else {
                    if(pipe != null){
                        pipe.setBelongs(new UninitPipeNet(pipe,pos));
                    }
                }
            }
            for(var pos : result){
                var pipe = LevelUtils.getPipe(level,pos);
                if(pipe != null){
                    buff.put(pos,pipe);
                    pipe.setBelongs(this);
                }
            }
            connected.clear();
            connected.putAll(buff);
            changed = false;
            IOChanged = true;
            needMarkDirty = true;
        }
        if((getOffsetTick() % 10 == 0 && IOChanged) || force){
            for(var c : needClear()){
                c.clear();
            }
            for(var pipe : connected.values()){
                for(var d : Direction.values()){
                    var c = Connections.fromDirection(d);
                    if(pipe.isConnected(c) && !pipe.isNeighborConnected(c)){
                        if(pipe.isBlocked(c)){
                            newInput(pipe.getPos(c),c);
                            needMarkDirty = true;
                        }
                        else {
                            newOutput(pipe.getPos(c),c);
                            needMarkDirty = true;
                        }
                    }
                }
            }
            IOChanged = false;
        }
        if(needMarkDirty) centerMTE.markDirty();
    }
    
    public void newInput(BlockPos pos,Connections c){
    
    }
    
    public void newOutput(BlockPos pos,Connections c){
    
    }
    
    public Collection<Map<?,?>> needClear(){
        return Collections.emptySet();
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
    
    @Override
    public void cut(BlockPos pos) {
        connected.remove(pos);
        _checkNet(true,pos);
        IOChanged = true;
    }
    
    public void tryLink(BlockPos blockPos){
        for(var mte : LevelUtils.getConnected(getLevel(),blockPos,true)){
            if(mte.havePipeNet() ){
                var net = mte.getBelongs();
                if(net != this){
                   // this.connected.putAll(net.getConnectedRaw());
                    for(var pipe:net.getConnected()){
                        if(canCombine(pipe.getBelongs())){
                            this.connected.put(pipe.getPos(),pipe);
                            pipe.setBelongs(this);
                            changed = true;
                            IOChanged = true;
                        }
                    }
                }
            }
        }
    }
    
    public Level getLevel(){
        return level;
    }
    
    @Override
    public final CompoundTag save(BlockPos pos) {
        if(pos != center) return new CompoundTag();
        return _save();
    }
    
    public CompoundTag _save(){
        return new CompoundTag();
    }
    
    @Override
    public Map<BlockPos,MTEPipe> getConnectedRaw(){
        return connected;
    }
    
    public int getOffsetTick(){
        return offset+TinTeaTech.ticks;
    }
    
    public class BFSSearcher implements Iterator<BlockPos> {
        private final Set<BlockPos> searched = Sets.newLinkedHashSet();
        private final Queue<BlockPos> queue = Queues.newArrayDeque();
        protected final ArrayList<BlockPos> removed = new ArrayList<>();
        
        public BFSSearcher() {
            this.searched.add(center);
            this.queue.offer(center);
        }
        
        public void setRemoved(BlockPos... blockPos){
            removed.addAll(List.of(blockPos));
        }
        @Override
        public boolean hasNext() {
            return this.queue.size() > 0;
        }
        
        @Override
        public BlockPos next() {
            BlockPos node = this.queue.remove();
            for(var c: LevelUtils.getConnected(level,node,false)){
                BlockPos another = c.getPos();
                if (!removed.contains(another) && this.searched.add(another))
                {
                    this.queue.offer(another);
                }
            }
            return node;
        }
        
        public Set<BlockPos> getSearched()
        {
            return this.searched;
        }
    }
}
