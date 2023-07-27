package com.xkball.tin_tea_tech.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IRotatable {
    boolean rotation(Level level, BlockPos pos,BlockState blockState, Direction to);
    
    default boolean rotate(Level level,BlockPos pos,BlockState blockState, Direction to){
        if(rotation(level,pos,blockState,to)){
            afterRotation(level,pos,blockState,to);
            return true;
        }
        return false;
    }
    void afterRotation(Level level,BlockPos pos,BlockState blockState,Direction to);
}
