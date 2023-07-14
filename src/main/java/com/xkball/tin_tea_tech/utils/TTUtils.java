package com.xkball.tin_tea_tech.utils;

import java.util.Map;
import java.util.function.Supplier;

public class TTUtils {
    
    public static <T,K> T getOrCreate(Map<K,T> map, K key, Supplier<T> supplier){
        if(!map.containsKey(key)){
            map.put(key,supplier.get());
        }
        return map.get(key);
    }
 }
