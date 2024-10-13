package com.xkball.tin_tea_tech.datagen;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.reg.RegItem;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TinTeaTech.MODID, existingFileHelper);
    }
    
    @Override
    protected void registerModels() {
        for(var item: RegItem.REG_ITEM_POOL.values()){
            if(item.getModelLocation() != null){
                this.basicItem(item.getModelLocation());
            }
        }
    }
}
