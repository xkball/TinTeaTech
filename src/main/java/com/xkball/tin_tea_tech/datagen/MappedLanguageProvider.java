package com.xkball.tin_tea_tech.datagen;

import com.xkball.tin_tea_tech.TinTeaTech;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.Map;

public class MappedLanguageProvider extends LanguageProvider {
    
    private final Map<String,String> map;
    public MappedLanguageProvider(PackOutput output, Map<String,String> map, String locale) {
        super(output, TinTeaTech.MODID, locale);
        this.map = map;
    }
    
    @Override
    protected void addTranslations() {
        for(var entry : map.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
    }
}
