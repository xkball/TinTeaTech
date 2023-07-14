package com.xkball.tin_tea_tech.api.annotation;

import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import net.minecraft.world.item.CreativeModeTab;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//给物品使用
//给MTE使用
//BlockItem除外 这时候给Block用
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreativeTag {
    
    Class<? extends CreativeModeTab> tab() default TTCreativeTab.TTBuildingBlockTab.class;
}
