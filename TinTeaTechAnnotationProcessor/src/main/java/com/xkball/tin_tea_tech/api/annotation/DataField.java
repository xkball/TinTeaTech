package com.xkball.tin_tea_tech.api.annotation;

import com.xkball.tin_tea_tech.util.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
@SuppressWarnings("unused")
public @interface DataField {
    
    String codec() default "";
    
    SyncPolicy syncPolicy() default SyncPolicy.OnlySave;
    
    enum SyncPolicy{
        OnlySave,
        Sync2Client,
        SyncBoth;
        
        public static SyncPolicy fromString(String str) {
            str = StringUtils.removeDoubleQuotes(str);
            var sa = str.split("\\.");
            str = sa[sa.length - 1];
            if("Sync2Client".equals(str)) return Sync2Client;
            if("SyncBoth".equals(str)) return SyncBoth;
            return SyncPolicy.OnlySave;
        }
    }
    
    @interface Exclude {
    
    }
}
