package com.xkball.tin_tea_tech.utils;

public class ColorUtils {
    public static final int white = getColor(255,255,255,255);
    public static final int black = getColor(0,0,0,255);
    public static int getColor(int r,int g,int b,int a){
        return a << 24 | r << 16 | g << 8 | b;
    }
    
    public static int getColor(int color,int a){
        return getColor(getRed(color),getGreen(color),getBlue(color),a);
    }
    
    public static int getRed(int color){
        return color >> 16 & 255;
    }
    
    public static int getGreen(int color){
        return color >> 8 & 255;
    }
    
    public static int getBlue(int color){
        return color & 255;
    }
}
