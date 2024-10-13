package com.xkball.tin_tea_tech.api.shape.def;

import com.xkball.tin_tea_tech.api.facing.RelativeFacing;
import net.minecraft.core.Direction;

import static com.xkball.tin_tea_tech.api.shape.TTShapes.H1;
import static com.xkball.tin_tea_tech.api.shape.TTShapes.H2;


public record Point2D(double x, double y) {
    
    public Point2D add(int w,int h){
        return new Point2D(x+w,y+h);
    }
    
    //在1*1*1立方体内的变换
    //默认为朝向东面计算(视野前面是西面)
    //每个面的坐标系原点为左下角
    @SuppressWarnings("SuspiciousNameCombination")
    public Point3D to3D(Direction direction){
        switch (direction){
            case DOWN -> {
                return new Point3D(1-y,0,x);
            }
            case UP -> {
                return new Point3D(y,1,x);
            }
            case NORTH -> {
                return new Point3D(1-x,y,0);
            }
            case SOUTH -> {
                return new Point3D(x,y,1);
            }
            case WEST -> {
                return new Point3D(0,y,x);
            }
            case EAST -> {
                return new Point3D(1,y,1-x);
            }
        }
        return new Point3D(0,0,0);
    }
    
    public RelativeFacing toRelativeFacing(){
        if(x < H1){
            if(y < H1){
                return RelativeFacing.left_down;
            }
            if(y > H2){
                return RelativeFacing.left_up;
            }
            else return RelativeFacing.left;
        }
        if(x > H2){
            if(y < H1){
                return RelativeFacing.right_down;
            }
            if(y > H2){
                return RelativeFacing.right_up;
            }
            else return RelativeFacing.right;
        }
        else {
            if(y < H1){
                return RelativeFacing.down;
            }
            if(y > H2){
                return RelativeFacing.up;
            }
            else return RelativeFacing.self;
        }
    }
    
    @Override
    public String toString() {
        return String.format("( %.8f,%.8f)",x,y);
    }
}
