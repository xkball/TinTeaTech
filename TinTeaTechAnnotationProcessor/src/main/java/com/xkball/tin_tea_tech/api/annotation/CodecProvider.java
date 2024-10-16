package com.xkball.tin_tea_tech.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
@SuppressWarnings("unused")
public @interface CodecProvider {
    
    String name() default "";
    
    int order() default 0;
}
