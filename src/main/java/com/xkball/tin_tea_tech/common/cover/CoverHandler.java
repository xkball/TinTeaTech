package com.xkball.tin_tea_tech.common.cover;

import com.xkball.tin_tea_tech.api.TTValue;
import com.xkball.tin_tea_tech.api.mte.cover.Cover;
import com.xkball.tin_tea_tech.api.mte.cover.VerticalCover;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import com.xkball.tin_tea_tech.utils.TTUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.Collection;
import java.util.EnumMap;

public class CoverHandler implements VerticalCover {
    
    private final EnumMap<Direction,Cover> covers = new EnumMap<>(Direction.class);
    private final BitSet haveCover = new BitSet();
    private final MetaTileEntity mte;
    
    public CoverHandler(MetaTileEntity mte) {
        this.mte = mte;
    }
    
    @Override
    public void tick() {
        for(var c : covers.values()){
            c.tick();
        }
    }
    
    @Override
    public MetaTileEntity getMTE() {
        return mte;
    }
    
    @Override
    public boolean addCover(Direction direction,Cover cover) {
        if(!haveCover(direction) && canApplyCover(direction,cover)){
            this.covers.put(direction,cover);
            haveCover.set(direction.ordinal());
            syncRender();
            mte.markDirty();
            return true;
        }
        return false;
    }
    
    public void checkCanMaintainCover(){
        for(var c : covers.entrySet()){
            if(!canApplyCover(c.getKey(),c.getValue())){
                var cover = removeCover(c.getKey(),true);
                if(cover != null){
                    LevelUtils.dropItem(mte.getLevel(),mte.getPos(),cover.asItemStack());
                }
               
            }
        }
    }
    
    public void removeAll(){
        for(var c : covers.entrySet()){
            var cover = removeCover(c.getKey(),true);
            if(cover != null){
                LevelUtils.dropItem(mte.getLevel(),mte.getPos(),cover.asItemStack());
            }
        }
    }
    
    @Override
    @Nullable
    public Cover removeCover(Direction direction,boolean force) {
        var cover = covers.get(direction);
        if(cover != null &&( cover.canRemove() || force)){
            cover.onRemove();
            covers.remove(direction);
            haveCover.clear(direction.ordinal());
            syncRender();
            mte.markDirty();
            return cover;
        }
        return null;
    }
    
    @Override
    public boolean haveCover(Direction direction) {
        if(mte.isClient()) return haveCover.get(direction.ordinal());
        return covers.containsKey(direction);
    }
    
    @Override
    public boolean canApplyCover(Direction direction, Cover cover) {
        return getMTE().canApplyCover(direction,cover);
    }
    
    @Override
    public Collection<Cover> allCovers() {
        return covers.values();
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(covers.containsKey(side)){
            return covers.get(side).getCapability(cap,side);
        }
        return LazyOptional.empty();
    }
    
    @Override
    public CompoundTag serializeNBT() {
        var result = new CompoundTag();
        for(var entry:covers.entrySet()){
            result.putInt(entry.getKey().getName()+"_type",entry.getValue().type());
            result.put(entry.getKey().getName(),entry.getValue().serializeNBT());
        }
        return result;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for(var direction:Direction.values()){
            if(nbt.contains(direction.getName())){
                var type = nbt.getInt(direction.getName()+"_type");
                var cover = CoverFactory.getCover(getMTE(),type,direction);
                cover.deserializeNBT(nbt.getCompound(direction.getName()));
                covers.put(direction,cover);
                haveCover.set(direction.ordinal());
            }
        }
        syncRender();
    }
    
    public void syncRender(){
        mte.sentCustomData(TTValue.COVER,(b) -> b.writeInt(TTUtils.intValueOfBitSet(haveCover)));
    }
    
    public void loadRenderData(ByteBuf buf){
        TTUtils.forIntToBitSet(buf.readInt(),6,haveCover);
    }
}
