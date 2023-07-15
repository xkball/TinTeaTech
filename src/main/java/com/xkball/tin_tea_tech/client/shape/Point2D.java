package com.xkball.tin_tea_tech.client.shape;

import net.minecraft.core.Direction;

public record Point2D(double x, double y) {
    
    public Point3D to3D(Direction direction){
        var axis = direction.getAxis();
        switch (axis) {
            case X -> {
                return new Point3D(direction == Direction.WEST ? 0d : 1d,y,x);
            }
            case Y -> {
                return new Point3D(x,direction == Direction.DOWN ? 0d : 1d ,y);
            }
            case Z -> {
                return new Point3D(x,y,direction == Direction.NORTH ? 0d : 1d);
            }
        }
        return new Point3D(0,0,0);
    }
}
