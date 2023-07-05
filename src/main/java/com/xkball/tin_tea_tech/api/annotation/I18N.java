package com.xkball.tin_tea_tech.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface I18N {
    
    String prefix() default "";
    String chinese();
    
    String english();
}
