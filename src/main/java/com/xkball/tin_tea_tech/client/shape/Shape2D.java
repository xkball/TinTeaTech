package com.xkball.tin_tea_tech.client.shape;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//仅用于记录2d边框
public class Shape2D {
    private final List<Line2D> lines = new ArrayList<>();
    private final Multimap<Direction,Line3D> drawLines = LinkedListMultimap.create();
    
    private boolean canDraw = false;
    
    public Shape2D() {
    }
    
    public Shape2D addLine(Line2D line2D){
        if(canDraw){
            throw new RuntimeException("you must edit a shape before it render");
        }
        lines.add(line2D);
        return this;
    }
    
    public Shape2D end(){
        canDraw = true;
        return this;
    }
    
    public Collection<Line3D> toDraw(Direction direction){
        if(!drawLines.containsKey(direction)){
            for(var line : lines){
                drawLines.put(direction,line.to3D(direction));
            }
        }
        return drawLines.get(direction);
    }
    
}
