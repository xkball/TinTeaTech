package com.xkball.tin_tea_tech.api.facing;

import com.xkball.tin_tea_tech.api.pipe.Connections;
import net.minecraft.core.Direction;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public enum RelativeFacing {
    self(direction -> direction),
    up(direction -> {
        if(direction.getAxis()!= Direction.Axis.Y)return Direction.UP;
        return direction.getClockWise(Direction.Axis.Z);
    }),
    down(direction -> {
        if(direction.getAxis()!= Direction.Axis.Y) return Direction.DOWN;
        return direction.getCounterClockWise(Direction.Axis.Z);
    }),
    left(direction -> {
        if(direction.getAxis() != Direction.Axis.Y) return direction.getClockWise();
        return Direction.NORTH;
    }),
    right(direction -> {
        if(direction.getAxis() != Direction.Axis.Y) return direction.getCounterClockWise();
        return Direction.SOUTH;
    }),
    back(Direction::getOpposite),
    left_up(Direction::getOpposite,true,left,up),
    left_down(Direction::getOpposite,true,left,down),
    right_up(Direction::getOpposite,true,right,up),
    right_down(Direction::getOpposite,true,right,down);
    
    private final UnaryOperator<Direction> toDirection;
    private final Function<Direction,Connections> toConnections;
    
    RelativeFacing(UnaryOperator<Direction> toDirection) {
        this(toDirection,false);
    }
    
    RelativeFacing(UnaryOperator<Direction> toDirection,boolean isCombinationFace,RelativeFacing... combined) {
        this.toDirection = toDirection;
        if(!isCombinationFace){
            this.toConnections = (d) -> Connections.formDirection(toDirection.apply(d));
        }
        else {
            this.toConnections = (d) -> toConnection(d,combined[0],combined[1]);
        }
    }
    
    public static Direction toDirection(Direction self,RelativeFacing relativeFacing){
        return relativeFacing.toDirection.apply(self);
    }
    
    public Direction toDirection(Direction self){
        return toDirection.apply(self);
    }
    
    public Connections toConnection(Direction self){
        return toConnections.apply(self);
    }
    
    public static Connections toConnection(Direction d,RelativeFacing a,RelativeFacing b){
        var d1 = a.toDirection.apply(d);
        var d2 = b.toDirection.apply(d);
        return Connections.find(d1,d2);
    }
}
