package com.xkball.tin_tea_tech.common.worldgen.vein;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.xkball.tin_tea_tech.api.worldgen.vein.DensityFunctionData;
import com.xkball.tin_tea_tech.api.worldgen.vein.VeinData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import java.util.Comparator;
import java.util.List;

public class Veins {
    public static Multimap<ResourceLocation, VeinData> levelVeinDataMap = LinkedHashMultimap.create();
    public static Multimap<ResourceLocation, Vein> levelVeinMap = LinkedHashMultimap.create();
    public static void init(long seed){
        loadFromMod();
        loadFromFile();
        //var seedRandom = RandomSource.create(seed);
        for(var entry:levelVeinDataMap.asMap().entrySet()){
            int hi = entry.getKey().toString().hashCode();
            RandomSource levelRandom = new XoroshiroRandomSource(hi,seed);
            var veinList = entry.getValue().stream().sorted(Comparator.comparingInt(VeinData::id)).toList();
            var iterator = veinList.listIterator();
            VeinData veinData;
            while (iterator.hasNext()){
                veinData = iterator.next();
                levelRandom = levelRandom.fork();
                levelVeinMap.put(entry.getKey(),new Vein(veinData,levelRandom));
            }
        }
    }
    
    //可覆盖Mod里的
    public static void loadFromFile(){
    
    }
    
    public static void loadFromMod(){
        var overWorld = Level.OVERWORLD.location();
        var testVeinData = new VeinData("test",1, List.of(overWorld),
                50,-30,DensityFunctionData.largerThan(DensityFunctionData.create(1,1,-8),0.4f),
                DensityFunctionData.largerThan(DensityFunctionData.create(1,1,-8),0.75f),
                4f,-0.08f,List.of(),List.of());
        levelVeinDataMap.put(overWorld,testVeinData);
    }
}
