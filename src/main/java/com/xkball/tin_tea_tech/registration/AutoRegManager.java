package com.xkball.tin_tea_tech.registration;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.DSL;
import com.mojang.logging.LogUtils;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.utils.Timer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.RegistryObject;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class AutoRegManager {
    
    private static final Pattern pattern = Pattern.compile("[A-Z][a-z]*");
    private static final List<ModFileScanData.AnnotationData> dataList = new ArrayList<>();
    
    private static final Map<Class<?>, RegistryObject<?>> regMap = new HashMap<>();
    
    public static final Multimap<String,Class<? extends Item>> itemTabMap = LinkedHashMultimap.create();
    
    public static RegistryObject<BlockEntityType<TTTileEntityBase>> TILE_ENTITY_BASE;
    
    
    public static void init(){
        var timer = new Timer();
        var c = ModLoadingContext.get().getActiveContainer();
        if(c instanceof FMLModContainer mc){
            //var re = ((TTMixinFMLModContainerAccess)mc).getScanResults();
            try {
                var scanResults = FMLModContainer.class.getDeclaredField("scanResults");
                scanResults.setAccessible(true);
                var modClass = FMLModContainer.class.getDeclaredField("modClass");
                modClass.setAccessible(true);
                ModFileScanData re = (ModFileScanData) scanResults.get(mc);
                var type = Type.getType(AutomaticRegistration.class);
                var list = re.getAnnotations().stream().filter(
                                (ad) -> ad.annotationType().equals(type) && ad.targetType() == ElementType.TYPE)
                        .toList();
                updateAnnotationDataList(list);
                genRegistryObj();
                
                 TILE_ENTITY_BASE = TTRegistration.BLOCK_ENTITY_TYPE.register(
                        "tile_entity_base",() ->
                                BlockEntityType.Builder.of(TTTileEntityBase::new,allMTEBlock())
                                        .build(DSL.remainderType())
                );
                
                LogUtils.getLogger().debug(TinTeaTech.MOD_NAME+" finished generate registry object in " + timer.timeNS() + " ns.");
            } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException |
                     InvocationTargetException | InstantiationException e) {
                throw new RuntimeException(e);
            }
            
        }
    }
    
    public static Collection<RegistryObject<?>> allRegistryObjects(){
        return regMap.values();
    }
    
    public static Collection<Map.Entry<Class<?>,RegistryObject<?>>> allEntries(){
        return regMap.entrySet();
    }
    
    //输入MTE的类 会返回Block
    public static <T> RegistryObject<T> getRegistryObject(Class<?> clazz){
        //noinspection unchecked
        return (RegistryObject<T>) regMap.get(clazz);
    }
    
    private static Block[] allMTEBlock(){
        List<Block> blocks = new LinkedList<>();
        for(var entry : allEntries()){
            var clazz = entry.getKey();
            if(MetaTileEntity.class.isAssignableFrom(clazz)){
                blocks.add((Block) entry.getValue().get());
            }
        }
        return blocks.toArray(new Block[0]);
    }
    
    private static void genRegistryObj() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for(var data : dataList){
            var oClazz = getRealClass(data.clazz().getClassName());
            newRegistry(fromClassName(oClazz),oClazz,null);
        }
    }
    
    //BlockClass 并不是方块真正的类
    //而是RegistryObjectMap里的key
    private static void newRegistry(String name,Class<?> clazz,@Nullable Class<?> blockClass){
        regMap.put(clazz,newRegistry1(name,clazz,blockClass));
    }
    
    private static RegistryObject<?> newRegistry1(String name,Class<?> clazz,Class<?> blockClass){
        if(regMap.containsKey(clazz)) throw new RuntimeException("duplicate registration: " + clazz.getName());
        
        //创造模式物品栏
        if(CreativeModeTab.class.isAssignableFrom(clazz)){
            
            return TTRegistration.CREATIVE_MODE_TABS.register(name,
                    () -> {
                        try {
                            return (CreativeModeTab) clazz.getDeclaredConstructor().newInstance();
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                 NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    });
            
        }
        //方块
        else if(Block.class.isAssignableFrom(clazz)){
            //正常情况
            if(blockClass == null) {
                var annotation = clazz.getAnnotation(AutomaticRegistration.class);
                //去注册物品
                newRegistry(name + "_item", annotation.blockItem(), clazz);
                
                return TTRegistration.BLOCKS.register(name,
                        () -> {
                            try {
                                return (Block) clazz.getDeclaredConstructor().newInstance();
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                     NoSuchMethodException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
            }
            //MTE
            
        }
        //物品
        else if(Item.class.isAssignableFrom(clazz)){
            if(clazz.isAnnotationPresent(CreativeTag.class)){
                var tabClazz = clazz.getAnnotation(CreativeTag.class).tab();
                //前面已经确认了Item是clazz的父类
                //noinspection unchecked
                itemTabMap.put(fromClassName(tabClazz), (Class<? extends Item>) clazz);
            }
            return TTRegistration.ITEMS.register(name,
                    () -> {
                            if(blockClass != null){
                                try {
                                    var constructor = clazz.getDeclaredConstructor(Block.class,Item.Properties.class);
                                    //noinspection RedundantCast
                                    constructor.newInstance((Block)getRegistryObject(blockClass).get(),TTRegistration.itemProperty);
                                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                                         InvocationTargetException e){
                                    throw new RuntimeException(e);
                                }
                            }
                            try {
                                return  (Item) clazz.getDeclaredConstructor().newInstance();
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                     NoSuchMethodException e) {
                                throw new RuntimeException(e);
                            }
                            
                        }
            );
        }
        //MTE
        else if(MetaTileEntity.class.isAssignableFrom(clazz)){
            try {
                MetaTileEntity temporaryMTE = (MetaTileEntity) clazz.getDeclaredConstructor(BlockPos.class,TTTileEntityBase.class).newInstance(null,null);
                MetaTileEntity.mteMap.put(fromClassName(clazz),temporaryMTE);
                
                
                
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            
        }
        throw new RuntimeException("Not support Type!");
    }
    
    private static Class<?> getRealClass(String className) throws ClassNotFoundException {
        className = fixClassName(className);
        if(className.contains("$")){
            var facName = className.substring(0,className.lastIndexOf("$"));
            var fac = Class.forName(facName);
            for(var clazz : fac.getDeclaredClasses()){
                if(clazz.getSimpleName().equals(className.substring(className.lastIndexOf("$")+1))){
                    return clazz;
                }
            }
        }
        return Class.forName(className);
    }
    
    private static String fixClassName(String className){
        //result = result.replace('$','.');
        return className.replace('/','.');
    }
    
    public static String fromClassName(Class<?> clazz){
        return fromClassName(clazz.getSimpleName());
    }
    
    private static String fromClassName(String className){
        var result = className;
        if(className.startsWith("TTT")){
            result = className.substring(3);
        }
        else if(className.startsWith("TT")){
            result = className.substring(2);
        }
        StringBuilder builder = new StringBuilder();
        for(MatchResult s : pattern.matcher(result).results().toList()){
            builder.append(s.group().toLowerCase());
            builder.append("_");
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }
    
    private static void updateAnnotationDataList(List<ModFileScanData.AnnotationData> list){
        dataList.clear();
        dataList.addAll(list);
    }
}