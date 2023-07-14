package com.xkball.tin_tea_tech.utils;

public class Timer {
    private final long startNS;
    private final long startMS;
    
    public Timer(){
        startNS = System.nanoTime();
        startMS = System.currentTimeMillis();
    }
    
    public long timeNS(){
        return System.nanoTime() - startNS;
    }
    
    public long timeMS(){
        return System.currentTimeMillis() - startMS;
    }
    
    public long getStartNS() {
        return startNS;
    }
    
    public long getStartMS() {
        return startMS;
    }
}
