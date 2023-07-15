package com.xkball.tin_tea_tech.client.shape;

import net.minecraft.core.Direction;

public record Line2D(Point2D start, Point2D end) {
    
    public Line3D to3D(Direction direction){
        return new Line3D(start.to3D(direction),end.to3D(direction));
    }
}
