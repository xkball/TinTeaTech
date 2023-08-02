package com.xkball.tin_tea_tech.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.Map;
import java.util.function.Supplier;

public class TTUtils {
    
    //使用它的时候不应该抛出异常
    public static final NonNullSupplier<RuntimeException> exceptionSupplier = RuntimeException::new;
    
    public static int clamp(int max,int min,int value){
        return value>max ? max : Math.max(value, min);
    }
    
    public static long longValueOfBitSet(BitSet set){
        long result = 0;
        for(int i = 0; i<set.length();i++){
            result = result | (long) (set.get(i) ? 1 : 0) << i;
        }
        return result;
    }
    
    public static BitSet forLongToBitSet(long i,int length){
        return forLongToBitSet(i,length,new BitSet(length));
    }
    
    public static BitSet forLongToBitSet(long i,int length,BitSet result){
        result.clear();
        for(int j = 0;j<length;j++){
            if(((i & (0b1L << j)) / (0b1L << j)) == 1){
                result.set(j);
            }
        }
        return result;
    }
    
    public static int intValueOfBitSet(BitSet set){
        int result = 0;
        for(int i = 0; i<set.length();i++){
            result = result | (set.get(i)?1:0) << i;
        }
        return result;
    }
    
    public static BitSet forIntToBitSet(int i,int length){
        return forIntToBitSet(i,length,new BitSet(length));
    }
    
    public static BitSet forIntToBitSet(int i,int length,BitSet result){
        result.clear();
        for(int j = 0;j<length;j++){
            if(((i & ( 0b1 << j)) / ( 0b1 << j)) == 1){
                result.set(j);
            }
        }
        return result;
    }
    
    public static <T,K> T getOrCreate(Map<K,T> map, K key, Supplier<T> supplier){
        if(!map.containsKey(key)){
            map.put(key,supplier.get());
        }
        return map.get(key);
    }
    
    //长度不变
    public static Vec2 getHorizontalVec2(Vec3 source){
        var x = Math.abs(source.x);
        var z = Math.abs(source.z);
        var k = x/z;
        var y1 = Math.sqrt(1/(k*k+1));
        var x1 = k*y1;
        return new Vec2((float) sameSign(source.x,x1), (float) sameSign(source.z,y1));
    }
    
    public static double sameSign(double sign,double target){
        var b = Math.abs(target);
        return sign>0?b:-b;
    }
    
    @Nullable
    public static Player getPlayer(){
        return DistExecutor.safeCallWhenOn(Dist.CLIENT,() -> Helper::getLocalPlayer);
    }
    
    
  
    
 }
