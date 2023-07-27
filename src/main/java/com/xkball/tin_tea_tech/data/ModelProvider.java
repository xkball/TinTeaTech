package com.xkball.tin_tea_tech.data;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModelProvider extends ItemModelProvider {
    
    public ModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TinTeaTech.MODID, exFileHelper);
    }
    
    @Override
    protected void registerModels() {
        for(var entry : MetaTileEntity.mteMap.entrySet()){
            var rl = entry.getValue().getItemModel();
            if(rl != null){
                this.withExistingParent(entry.getKey()+"_item",rl);
            }
        }
        for(var entry : AutoRegManager.allEntries()){
            var clazz = entry.getKey();
            if((IItemBehaviour.class.isAssignableFrom(clazz) || Item.class.isAssignableFrom(clazz) )&& clazz.isAnnotationPresent(Model.class)){
                //只应执行一次
                for(var rl : clazz.getAnnotation(Model.class).resources()){
                    simpleItem(clazz,rl);
                    break;
                }
            }
        }
        
    }
    
    public void simpleItem(Class<?> clazz,String resourceLocation){
        this.singleTexture(AutoRegManager.fromClassName(clazz),mcLoc("item/generated"),"layer0",new ResourceLocation(resourceLocation));
        
    }
}
