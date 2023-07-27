package com.xkball.tin_tea_tech.client.shape;

import com.xkball.tin_tea_tech.client.gui.components.Component;

public record Rec2D(Point2D min, Point2D max) {
    
    public Rec2D(Component component){
        this(component.getStartPos(),component.getEndPos());
    }
    
    public boolean isInside(Point2D point2D){
        return point2D.x()>this.min.x() && point2D.y()>this.min.y() && point2D.x()<this.max.x() && point2D.y()<this.max().y();
    }
    
    public boolean isInside(int x,int y){
        return x>this.min.x() && y>this.min.y() && x<this.max.x() && y<this.max().y();
    }
    
}
