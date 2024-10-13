package com.xkball.tin_tea_tech.datagen;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.reg.RegBlock;
import com.xkball.tin_tea_tech.api.reg.RegItem;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = TinTeaTech.MODID, bus = EventBusSubscriber.Bus.MOD)
public class TinTeaTechDataGen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        var existingFileHelper = event.getExistingFileHelper();
        var dataGenerator = event.getGenerator();
        var run = event.includeClient();
        var packOutput = dataGenerator.getPackOutput();
        dataGenerator.addProvider(run,LangUtils.getEN_US(packOutput));
        dataGenerator.addProvider(run,LangUtils.getZH_CN(packOutput));
        dataGenerator.addProvider(run,new ModBlockModelProvider(packOutput,existingFileHelper));
        dataGenerator.addProvider(run,new ModItemModelProvider(packOutput,existingFileHelper));
    }
    
    public static class LangUtils{
        public static final Map<String,String> EN_US = new HashMap<>();
        public static final Map<String,String> ZH_CN = new HashMap<>();
        private static boolean init = false;
        
        private static void init(){
            if(init) return;
            init = true;
            addLangKey("itemGroup.tin_tea_tech.blocks","TinTeaTech: Blocks","锡茶科技: 方块");
            addLangKey("itemGroup.tin_tea_tech.misc","TinTeaTech: Misc","锡茶科技: 杂项");
            for(var item : RegItem.REG_ITEM_POOL.values()){
                if(item.getI18n() == null) continue;
                var i18n = item.getI18n();
                addLangKey(i18n.key(),i18n.en_us(),i18n.zh_cn());
            }
            for(var block : RegBlock.REG_BLOCK_POOL.values()){
                if(block.getI18n() == null) continue;
                var i18n = block.getI18n();
                addLangKey(i18n.key(),i18n.en_us(),i18n.zh_cn());
            }
        }
        
        public static void addLangKey(String key, String en_us, String zh_cn){
            EN_US.put(key, en_us);
            ZH_CN.put(key, zh_cn);
        }
        
        public static MappedLanguageProvider getEN_US(PackOutput packOutput){
            init();
            return new MappedLanguageProvider(packOutput,EN_US,"en_us");
        }
        
        public static MappedLanguageProvider getZH_CN(PackOutput packOutput){
            init();
            return new MappedLanguageProvider(packOutput,ZH_CN,"zh_cn");
        }
    }
}
