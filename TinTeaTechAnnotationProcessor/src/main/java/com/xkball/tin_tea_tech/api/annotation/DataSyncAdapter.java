package com.xkball.tin_tea_tech.api.annotation;

public @interface DataSyncAdapter {
    
    Type value();
    
    enum Type{
        BlockEntity;
        
        public static Type fromString(String value){
            return BlockEntity;
        }
    }
}
