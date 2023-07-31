package com.xkball.tin_tea_tech.data;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.annotation.Tag;
import com.xkball.tin_tea_tech.api.mte.cover.Cover;
import com.xkball.tin_tea_tech.data.tag.TTBlockTags;
import com.xkball.tin_tea_tech.data.tag.TTItemTags;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import org.objectweb.asm.Type;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
public class TagProviders {
    
    public static class ItemTagProvider extends ItemTagsProvider {
        
        
        public ItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags) {
            super(pOutput, pLookupProvider, pBlockTags,TinTeaTech.MODID,null);
        }
        
        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            var itemTagType = Type.getType(Tag.Item.class);
            for(var clazz : AutoRegManager.re.getAnnotations().stream().filter(
                    (ad) -> ad.annotationType().equals(itemTagType)
            ).map((ad) -> {
                try {
                    return AutoRegManager.getRealClass(ad.clazz().getClassName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).toList()){
                for(var tagName : clazz.getAnnotation(Tag.Item.class).value()){
                    var Tag = TTItemTags.tagMap.get(tagName);
                    var obj = AutoRegManager.getRegistryObject(clazz).get();
                    if(obj instanceof Item){
                        tag(Tag).add((Item) obj);
                    }
                    else if(obj instanceof Block b){
                        tag(Tag).add(b.asItem());
                    }
                }
            }
        }
    }
    
    public static class BlockTagProvider extends BlockTagsProvider{
        
        public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider,TinTeaTech.MODID,null);
        }
        
        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            var itemTagType = Type.getType(Tag.Block.class);
            for(var clazz : AutoRegManager.re.getAnnotations().stream().filter(
                    (ad) -> ad.annotationType().equals(itemTagType)
            ).map((ad) -> {
                try {
                    return AutoRegManager.getRealClass(ad.clazz().getClassName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).toList()){
                for(var tagName : clazz.getAnnotation(Tag.Block.class).value()){
                    var Tag = TTBlockTags.tagMap.get(tagName);
                    tag(Tag).add((Block) AutoRegManager.getRegistryObject(clazz).get());
                }
            }
        }
    }
}
