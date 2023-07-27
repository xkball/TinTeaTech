package com.xkball.tin_tea_tech.api.client.gui;

public interface PosGetter {
    //返回绘制内部组件的起始位置
    int getX();
    int getY();
    
    static PosGetter fixPoint(int x,int y) {
        return new PosGetter() {
            @Override
            public int getX() {
                return x;
            }
            
            @Override
            public int getY() {
                return y;
            }
        };
    }
    static PosGetter combine(PosGetter p1,PosGetter p2){
        return new PosGetter() {
            @Override
            public int getX() {
                return p1.getX()+p2.getX();
            }
            
            @Override
            public int getY() {
                return p1.getY()+p2.getY();
            }
        };
    }
}
