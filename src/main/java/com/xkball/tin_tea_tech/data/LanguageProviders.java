package com.xkball.tin_tea_tech.data;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;

public class LanguageProviders {
    public static class ChineseLanguageProvider extends LanguageProvider {
        
        public ChineseLanguageProvider(PackOutput output) {
            super(output, TinTeaTech.MODID,"zh_cn");
        }
        
        @Override
        protected void addTranslations() {
            
            
            for(var key : DataGen.LangUtils.keys()){
                this.add(key, DataGen.LangUtils.getChinese(key));
            }
            
            for(var entry : AutoRegManager.allEntries()){
                var clazz = entry.getKey();
                Object obj = entry.getValue().get();
                if(clazz.isAnnotationPresent(I18N.class)){
                    var i18n = clazz.getAnnotation(I18N.class);
                    var prefix = i18n.prefix();
                    if(prefix.isEmpty()){
                        if(obj instanceof CreativeModeTab){
                            prefix = "creative_tab";
                        }
                        else if(obj instanceof Block || obj instanceof MetaTileEntity){
                            prefix = "block";
                        }
                        else if(obj instanceof Item){
                            prefix = "item";
                        }
                    }
                    this.add(prefix+"."+TinTeaTech.MODID+"."+AutoRegManager.fromClassName(clazz),i18n.chinese());
                    
                }
            }
//            for(var mteClazz : AutoRegManager.allMteClasses()){
//                if(mteClazz.isAnnotationPresent(I18N.class)){
//                    var i18n = mteClazz.getAnnotation(I18N.class);
//                    this.add("block."+TinTeaTech.MODID+"."+AutoRegManager.fromClassName(mteClazz),i18n.chinese());
//                }
//            }
        }
    }
    
    public static class EnglishLanguageProvider extends LanguageProvider{
        
        public EnglishLanguageProvider(PackOutput output) {
            super(output,TinTeaTech.MODID,"en_us");
        }
        
        @Override
        protected void addTranslations() {
            
            for(var key : DataGen.LangUtils.keys()){
                this.add(key, DataGen.LangUtils.getEnglish(key));
            }
            
            for(var entry : AutoRegManager.allEntries()){
                var clazz = entry.getKey();
                Object obj = entry.getValue().get();
                if(clazz.isAnnotationPresent(I18N.class)){
                    var i18n = clazz.getAnnotation(I18N.class);
                    var prefix = i18n.prefix();
                    if(prefix.isEmpty()){
                        if(obj instanceof CreativeModeTab){
                            prefix = "creative_tab";
                        }
                        else if(obj instanceof Block){
                            prefix = "block";
                        }
                        else if(obj instanceof Item){
                            prefix = "item";
                        }
                    }
                    this.add(prefix+"."+TinTeaTech.MODID+"."+AutoRegManager.fromClassName(clazz),i18n.english());
                    
                }
                
            }
//            for(var mteClazz : AutoRegManager.allMteClasses()){
//                if(mteClazz.isAnnotationPresent(I18N.class)){
//                    var i18n = mteClazz.getAnnotation(I18N.class);
//                    this.add("block."+TinTeaTech.MODID+"."+AutoRegManager.fromClassName(mteClazz),i18n.english());
//                }
//            }
        }
    }
}
