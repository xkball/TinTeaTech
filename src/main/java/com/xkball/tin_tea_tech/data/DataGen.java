package com.xkball.tin_tea_tech.data;

import com.mojang.datafixers.util.Pair;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataGen {
   
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        gen.addProvider(event.includeClient(), new LanguageProviders.EnglishLanguageProvider(packOutput));
        gen.addProvider(event.includeClient(), new LanguageProviders.ChineseLanguageProvider(packOutput));
    
    }
    
    public static class LangUtils{
        private static final Map<String, Pair<String,String>> localizations = new HashMap<>();
        
        public static void addLangKey(String key,String en_us,String zh_cn){
            localizations.putIfAbsent(key,new Pair<>(en_us,zh_cn));
        }
        
        public static String getChinese(String key){
            return localizations.get(key).getSecond();
        }
        
        public static String getEnglish(String key){
            return localizations.get(key).getFirst();
        }
        
        public static Collection<String> keys(){
            return Collections.unmodifiableSet(localizations.keySet());
        }
        
    }
    
    
}
