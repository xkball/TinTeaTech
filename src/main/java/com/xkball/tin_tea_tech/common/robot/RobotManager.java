package com.xkball.tin_tea_tech.common.robot;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class RobotManager implements INBTSerializable<CompoundTag> {
    
    final Set<Robot> robots = new LinkedHashSet<>();
    final Map<BlockPos,Node> allNodes = new Object2ObjectOpenHashMap<>();
    
    final Set<BlockPos> allPipes = new LinkedHashSet<>();
    
    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
    
    }
}
