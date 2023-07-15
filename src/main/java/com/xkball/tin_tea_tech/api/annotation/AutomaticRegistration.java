package com.xkball.tin_tea_tech.api.annotation;

import com.xkball.tin_tea_tech.common.blocks.te.TTTileEntityBlock;
import com.xkball.tin_tea_tech.common.item.TTCommonItem;
import net.minecraft.world.item.BlockItem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//除了MTE 其他时候指定的类都只能有一个实例
public @interface AutomaticRegistration {
    
    boolean singleton() default true;
    
    boolean needDataGenModel() default false;
    
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Block{
        //给方块使用,用于指定BlockItem的类
        Class<? extends BlockItem> blockItem() default BlockItem.class;
        
        boolean singleton() default false;
    }
    
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface MTE{
        
        //定义MTE的方块类 于MTE有方块状态时使用
        Class<? extends TTTileEntityBlock> block() default TTTileEntityBlock.class;
        
        //类名
        //不能用class 否则服务器会爆炸
        String renderer() default "com.xkball.tin_tea_tech.client.render.DefaultMTERender" ;
    }
    
    //仅用于IItemBehaviour
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Item{
        Class<? extends net.minecraft.world.item.Item> itemClass() default TTCommonItem.class;
    }
}
