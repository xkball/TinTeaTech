package com.xkball.tin_tea_tech.data;

import com.mojang.datafixers.util.Pair;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.*;

public class DataGen {
    
    private static final ExistingFileHelper helper = new ExistingFileHelper(List.of(), Set.of(),false,null,null);
   
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        gen.addProvider(event.includeClient(), new LanguageProviders.EnglishLanguageProvider(packOutput));
        gen.addProvider(event.includeClient(), new LanguageProviders.ChineseLanguageProvider(packOutput));
        gen.addProvider(event.includeClient(), new ModelProvider(packOutput, helper));
        gen.addProvider(event.includeClient(), new StateProvider(packOutput, helper));
    }
    
    public static class LangUtils{
        static {
            localizations = new HashMap<>();
            addLangKey("info.tin_tea_tech.block_name","Block Name: ","方块名称: ");
        }
        private static final Map<String, Pair<String,String>> localizations;
        
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
