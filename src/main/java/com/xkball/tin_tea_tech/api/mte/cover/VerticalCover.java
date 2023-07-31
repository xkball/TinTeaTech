package com.xkball.tin_tea_tech.api.mte.cover;

import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface VerticalCover extends INBTSerializable<CompoundTag> {

    void tick();
    MetaTileEntity getMTE();
    boolean addCover(Direction direction,Cover cover);
    Cover removeCover(Direction direction,boolean force);
    default Cover removeCover(Direction direction){
        return removeCover(direction,false);
    }
    boolean haveCover(Direction direction);
    boolean canApplyCover(Direction direction,Cover cover);
    
    Collection<Cover> allCovers();
    
    @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side);
}
