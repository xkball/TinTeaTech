package com.xkball.tin_tea_tech.data;

import com.mojang.datafixers.util.Pair;
import com.xkball.tin_tea_tech.TinTeaTech;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.*;

public class DataGen {
    
    private static final ExistingFileHelper helper = new ExistingFileHelper(List.of(), Set.of(),false,null,null);
   
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        var lookupProvider = event.getLookupProvider();
        gen.addProvider(event.includeClient(), new LanguageProviders.EnglishLanguageProvider(packOutput));
        gen.addProvider(event.includeClient(), new LanguageProviders.ChineseLanguageProvider(packOutput));
        gen.addProvider(event.includeClient(), new ModelProvider(packOutput, helper));
        gen.addProvider(event.includeClient(), new StateProvider(packOutput, helper));
        var blockTagProvider = new TagProviders.BlockTagProvider(packOutput,lookupProvider);
        gen.addProvider(event.includeClient(),blockTagProvider);
        gen.addProvider(event.includeClient(),new TagProviders.ItemTagProvider(packOutput,lookupProvider,blockTagProvider.contentsGetter()));
    }
    
    public static class LangUtils{
        static {
            localizations = new HashMap<>();
            addLangKey("info.tin_tea_tech.block_name","Block Name: ","方块名称: ");
            addLangKey("info.tin_tea_tech.block_pos","Block Pos: ","方块位置: ");
            addLangKey("info.tin_tea_tech.tick_used","Last tick used","上一tick消耗了: ");
            addLangKey("info.tin_tea_tech.block_info","Block Info: ","方块信息: ");
            addLangKey("info.tin_tea_tech.mte_info","MetaTileEntity Info: ","MTE信息: ");
            addLangKey("info.tin_tea_tech.cover_info","Cover Info: ","覆盖版信息: ");
            addLangKey("info.tin_tea_tech.inventory","Machine Inventory: ","机器内部物品存储: ");
            addLangKey("info.tin_tea_tech.fluids","Machine Fluid Storage: ","机器内部流体存储: ");
            addLangKey("info.tin_tea_tech.fe","Machine FE Storage: ","机器内部FE存储: ");
            addLangKey("info.tin_tea_tech.steam","Machine Steam Storage: ","机器内部蒸汽存储: ");
            addLangKey("info.tin_tea_tech.pressure","Pressure: ","压强: ");
            addLangKey("info.tin_tea_tech.volume","Volume: ","体积: ");
            addLangKey("info.tin_tea_tech.storage","Storage: ","已存储: ");
            addLangKey("info.tin_tea_tech.max_storage","Max Storage: ","最大存储: ");
            addLangKey("info.tin_tea_tech.storage_percent","Percent","百分比: ");
            addLangKey("info.tin_tea_tech.slot","Slot: ","槽位: ");
            addLangKey("info.tin_tea_tech.item","Item: ","物品: ");
            addLangKey("info.tin_tea_tech.fluid","Fluid: ","流体: ");
            addLangKey("key.tin_tea_tech.open_holo_glass", "Open Holo Glass","打开云钩");
            addLangKey("key.tin_tea_tech.print_nbt","Print NBT","显示NBT");
            addLangKey("key.category.tin_tea_tech",TinTeaTech.MOD_NAME, TinTeaTech.MOD_NAME_CHINESE);
            addLangKey("gui.title.tin_tea_tech.holo_glass","Holo Glass","云钩界面");
            addLangKey("gui.tin_tea_tech.displayInventory","Display Inventory","显示物品栏");
            addLangKey("gui.tin_tea_tech.holo_glass_plugin","HoloGlass Plugin","云钩插件");
            addLangKey("gui.tin_tea_tech.gui_setting","GUI Setting","GUI设置");
            addLangKey("gui.tin_tea_tech.plugins_installed","PluginsInstalled: ","已安装插件: ");
            addLangKey("gui.tin_tea_tech.lock_inventory","LockInventory","锁定物品栏位置");
            addLangKey("gui.tin_tea_tech.display_nbt","Display NBT info on tricorder","三录仪显示NBT");
            addLangKey("gui.tin_tea_tech.building_mode","Buliding Mode","斜向建筑模式");
            addLangKey("gui.tin_tea_tech.allow_riding_by_player","Allow Riding By Player","允许被控制");
            addLangKey("tooltip.tin_tea_tech.holo_glass_plugin","This is a holo glass plugin","可作为云钩插件");
            addLangKey("tooltip.tin_tea_tech.not_ti","Definitely Not Titanium !","绝对不是钛制!");
            addLangKey("tooltip.tin_tea_tech.sun_glass","Welcome to NIGHT CITY!" ,"黑夜给了我一双黑色的眼睛,从此我只能看到黑夜");
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
