package com.xkball.tin_tea_tech.api.shape.def;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public record Point3D(double x, double y, double z) {
    
    public Point3D(Vec3i vec3i){
        this(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }
    
    public Point3D(Vec3 vec3){
        this(vec3.x(), vec3.y(), vec3.z());
    }
    
    public Point3D(Vec3 hitLocation, BlockPos pos){
        this(hitLocation.x()-pos.getX(),hitLocation.y()-pos.getY(),hitLocation.z()-pos.getZ());
    }
    
    @SuppressWarnings("SuspiciousNameCombination")
    public Point2D to2D(Direction direction){
        switch (direction){
            case DOWN -> {
                return new Point2D(z,1-x);
            }
            case UP -> {
                return new Point2D(z,x);
            }
            case NORTH -> {
                return new Point2D(1-x,y);
            }
            case SOUTH -> {
                return new Point2D(x,y);
            }
            case WEST -> {
                return new Point2D(z,y);
            }
            case EAST -> {
                return new Point2D(1-z,y);
            }
        }
        return new Point2D(0,0);
    }
}
