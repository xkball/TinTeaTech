package com.xkball.tin_tea_tech.api.annotation;

import net.minecraft.world.item.BlockItem;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutomaticRegistration {

    @Nullable
    Class<? extends BlockItem> blockItem() default BlockItem.class;
}
