package com.xkball.tin_tea_tech.registration;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.DSL;
import com.mojang.logging.LogUtils;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.common.blocks.te.TTTileEntityBlock;
import com.xkball.tin_tea_tech.common.item.TTCommonItem;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.utils.Timer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.RegistryObject;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class AutoRegManager {
    
    private static final Pattern pattern = Pattern.compile("[A-Z][a-z]*");
    private static final List<ModFileScanData.AnnotationData> dataList = new ArrayList<>();
    
    //仅在单例时使用
    private static final Map<Class<?>, RegistryObject<?>> classRegMap = new HashMap<>();
    
    //所有自动注册的玩意都放进去
    private static final Map<String,RegistryObject<?>> nameRegMap = new HashMap<>();
    private static final List<Class<? extends MetaTileEntity>> mteClassList = new ArrayList<>();
    public static final List<String> models = new ArrayList<>();
    
    //                          TabName,ItemName
    public static final Multimap<String,String> itemTabMap = LinkedHashMultimap.create();
    
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
                models.addAll(re.getAnnotations().stream().filter(
                        (ad) -> ad.annotationType().equals(Type.getType(Model.class))
                ).map(
                        (ad) -> {
                            try {
                                return getRealClass(ad.clazz().getClassName());
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }).flatMap(
                        (aClass) -> {
                            var an = aClass.getAnnotation(Model.class);
                            return Arrays.stream(an.resources());
                        }
                ).toList());
                LogUtils.getLogger().debug(TinTeaTech.MOD_NAME+" finished generate registry object in " + timer.timeMS() + " ms.");
            } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException |
                     InvocationTargetException | InstantiationException e) {
                throw new RuntimeException(e);
            }
            
        }
    }
    
    public static Collection<RegistryObject<?>> allRegistryObjects(){
        return classRegMap.values();
    }
    
    public static Collection<Map.Entry<Class<?>,RegistryObject<?>>> allEntries(){
        return classRegMap.entrySet();
    }
    
    public static Collection<Class<? extends MetaTileEntity>> allMteClasses(){
        return Collections.unmodifiableList(mteClassList);
    }
    
    //输入MTE的类 会返回Block
    public static <T> RegistryObject<T> getRegistryObject(Class<?> clazz){
        //noinspection unchecked
        return (RegistryObject<T>) classRegMap.get(clazz);
    }
    
    //实际上 根本不能检查返回的RegistryObject的类型 感觉很危险 但是我没办法
    public static <T> RegistryObject<T> getRegistryObject(String name){
        //noinspection unchecked
        return (RegistryObject<T>) nameRegMap.get(name);
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
            //此处数据除了MTE应该均为单例
            var oClazz = getRealClass(data.clazz().getClassName());
            newRegistry(oClazz);
        }
    }
    
    //BlockClass 并不是方块真正的类
    //而是RegistryObjectMap里的key
    private static void newRegistry(Class<?> clazz){
        //如果不是mte
        if(!MetaTileEntity.class.isAssignableFrom(clazz)){
            var ro = newRegistry1(clazz);
            classRegMap.put(clazz,ro);
            nameRegMap.put(fromClassName(clazz),ro);
        }
        else {
            newMTE(clazz);
        }
       
    }
    
    private static void newMTE(Class<?> clazz){
        try {
            //前面已经检测过
            //noinspection unchecked
            mteClassList.add((Class<? extends MetaTileEntity>) clazz);
            Class<? extends TTTileEntityBlock> mteClass = TTTileEntityBlock.class;
            if(clazz.isAnnotationPresent(AutomaticRegistration.MTE.class)){
                mteClass = clazz.getAnnotation(AutomaticRegistration.MTE.class).block();
            }
            MetaTileEntity temporaryMTE = (MetaTileEntity) clazz.getDeclaredConstructor(BlockPos.class,TTTileEntityBase.class).newInstance(TinTeaTech.ORIGIN,null);
            var name = fromClassName(clazz);
            MetaTileEntity.mteMap.put(name,temporaryMTE);
            
            var mteBlockConstructor = mteClass.getConstructor(String.class);
            var ro = TTRegistration.BLOCKS.register(name,
                    () -> {
                        try {
                            return mteBlockConstructor.newInstance(name);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    });
            classRegMap.put(clazz,ro);
            nameRegMap.put(name,ro);
            
            var itemName = name+"_item";
            assignCreativeTag(clazz,itemName);
            nameRegMap.put(itemName,TTRegistration.ITEMS.register(name+"_item",
                    () -> new BlockItem((Block) AutoRegManager.getRegistryObject(name).get(),
                            TTRegistration.getItemProperty())));
            
            
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static RegistryObject<?> newRegistry1(Class<?> clazz){
        if(classRegMap.containsKey(clazz)) throw new RuntimeException("duplicate registration: " + clazz.getName());
        var name = fromClassName(clazz);
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
            var annotation = clazz.getAnnotation(AutomaticRegistration.Block.class);
            var itemName = name + "_item";
            //去注册方块物品
            var ro = genItemBlock(itemName, annotation.blockItem(), name);
            nameRegMap.put(itemName,ro);
            if(annotation.blockItem() != BlockItem.class && annotation.singleton()){
                classRegMap.put(annotation.blockItem(),ro);
            }
            assignCreativeTag(clazz,itemName);
            //注册方块
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
        //物品
        else if(Item.class.isAssignableFrom(clazz)){
            assignCreativeTag(clazz,name);
            return TTRegistration.ITEMS.register(name,
                    () -> {
//                            if(blockClass != null){
//                                try {
//                                    var constructor = clazz.getDeclaredConstructor(Block.class,Item.Properties.class);
//                                    //noinspection RedundantCast
//                                    constructor.newInstance((Block)getRegistryObject(blockClass).get(),TTRegistration.itemProperty);
//                                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
//                                         InvocationTargetException e){
//                                    throw new RuntimeException(e);
//                                }
//                            }
                            try {
                                return  (Item) clazz.getDeclaredConstructor().newInstance();
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                     NoSuchMethodException e) {
                                throw new RuntimeException(e);
                            }
                            
                        }
            );
        }
        //还是物品
        else if(IItemBehaviour.class.isAssignableFrom(clazz)){
            assignCreativeTag(clazz,name);
            Class<?> itemClass;
            if(clazz.isAnnotationPresent(AutomaticRegistration.Item.class)){
                itemClass = clazz.getAnnotation(AutomaticRegistration.Item.class).itemClass();
            } else {
                itemClass = TTCommonItem.class;
            }
            return TTRegistration.ITEMS.register(name,
                    () -> {
                        try {
                            //noinspection RedundantCast
                            return (Item) itemClass.getConstructor(IItemBehaviour.class)
                                    .newInstance( (IItemBehaviour)clazz.getConstructor().newInstance());
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                 NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        throw new RuntimeException("Not support Type!");
    }
    
    private static RegistryObject<Item> genItemBlock(String name,Class<?> clazz,String blockKey){
        return TTRegistration.ITEMS.register(name,
                () -> {
                    if(clazz == BlockItem.class){
                        try {
                            var constructor = clazz.getDeclaredConstructor(Block.class,Item.Properties.class);
                            //noinspection RedundantCast
                            constructor.newInstance((Block)getRegistryObject(blockKey).get(),TTRegistration.getItemProperty());
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
    
    public static Class<?> getRealClass(String className) throws ClassNotFoundException {
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
    
    public static String fromClassName(String className){
        var result = className;
        if(className.startsWith("TTT")){
            result = className.substring(3);
        }
        else if(className.startsWith("TT")){
            result = className.substring(2);
        }
        else if(className.startsWith("MTE")){
            result = className.substring(3);
        }
        else if(className.endsWith("Behaviour")){
            result = className.substring(0,className.lastIndexOf("B"));
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
    
    private static void assignCreativeTag(Class<?> clazz,String name){
        if(clazz.isAnnotationPresent(CreativeTag.class)){
            var tabClazz = clazz.getAnnotation(CreativeTag.class);
            itemTabMap.put(fromClassName(tabClazz.tab()),name);
        }
    }
}
