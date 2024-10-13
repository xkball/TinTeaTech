package com.xkball.tin_tea_tech.api.shape.def;

public record Vec8f(float pX0, float pX1, float pY0, float pY1, float pZ0, float pZ1, float pZ2, float pZ3) {
    
    public Vec8f(double pX0,double pX1,double pY0,double pY1,double pZ0,double pZ1,double pZ2,double pZ3){
        this((float) pX0,(float) pX1,(float) pY0,(float) pY1,(float) pZ0,(float) pZ1,(float) pZ2,(float) pZ3);
    }
}
