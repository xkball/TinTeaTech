package com.xkball.tin_tea_tech.utils;

public class Timer {
    private final long startTime;
    
    public Timer(){
        startTime = System.nanoTime();
    }
    
    public long timeNS(){
        return System.nanoTime() - startTime;
    }
    
    public long getStartTime() {
        return startTime;
    }
}
