package com.xkball.tin_tea_tech.datagen;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.reg.RegBlock;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockModelProvider extends BlockStateProvider {
    
    public ModBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TinTeaTech.MODID, existingFileHelper);
    }
    
    @Override
    protected void registerStatesAndModels() {
        for(var block : RegBlock.REG_BLOCK_POOL.values()){
            var item = block.getBlockItemHolder();
            if(item == null) continue;
            if(block.getItemModelLocation() != null){
                this.simpleBlockItem(block.get(),models().getExistingFile(block.getItemModelLocation()));
            }
            else if(item.getModelLocation() != null){
                this.itemModels().basicItem(item.getModelLocation());
            }
        }
    }
}
