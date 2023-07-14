package com.xkball.tin_tea_tech.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//类似于延迟初始化一个量
//可以不在构造方法赋值,可以在之后赋值
//但是只能赋值一次

//大概很有必要把它自己放做final
//在考虑需不需要做基本类型版本
//但是做了等于把值类型变成引用类型,性能可能垃圾?
public class FinalObj<T> {
    @Nullable
    private T obj;
    
    private boolean init = false;
    
    public FinalObj(){}
    
    public FinalObj(@Nonnull T instance){
        this.obj = instance;
        init = true;
    }
    
    public void set(@Nonnull T instance){
        this.obj = instance;
        init = true;
    }
    
    public T get(){
        return obj;
    }
    
    public boolean isInit() {
        return init;
    }
    
    public boolean isEmpty(){
        return obj == null;
    }
}
