package com.xkball.tin_tea_tech.utils;

import java.util.function.Supplier;

public class Cache<T> {
    
    private final Supplier<T> getter;
    
    private T value;
    
    public Cache(Supplier<T> getter) {
        this.getter = getter;
    }
    
    public T get(){
        if(value != null) return value;
        value = getter.get();
        return value;
    }
}
