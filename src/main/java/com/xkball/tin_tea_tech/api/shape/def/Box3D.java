package com.xkball.tin_tea_tech.api.shape.def;

import net.minecraft.core.Direction;

public record Box3D(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    
    
    public Box3D(Point3D min, Point3D max){
        this(min.x(),min.y(),min.z(),max.x(),max.y(),max.z());
    }
    
    public Vec8f toRender(Direction direction){
        return switch (direction){
            case DOWN -> new Vec8f(minX,maxX,minY,minY,minZ,minZ,maxZ,maxZ);
            case UP -> new Vec8f(minX,maxX,maxY,maxY,minZ,minZ,maxZ,maxZ);
            case NORTH -> new Vec8f(minX,maxX,minY,maxY,minZ,minZ,minZ,minZ);
            case SOUTH -> new Vec8f(minX,maxX,minY,maxY,maxZ,maxZ,maxZ,maxZ);
            case WEST -> new Vec8f(maxX,maxX,minY,maxY,minZ,maxZ,maxZ,minZ);
            case EAST -> new Vec8f(minX,minX,minY,maxY,minZ,maxZ,maxZ,minZ);
        };
    }
    
}
