package com.xkball.tin_tea_tech.data;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import net.minecraft.data.PackOutput;
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
        
    }
}
