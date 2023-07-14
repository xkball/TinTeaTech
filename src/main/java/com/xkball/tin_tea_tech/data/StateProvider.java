package com.xkball.tin_tea_tech.data;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class StateProvider extends BlockStateProvider {
    
    final ExistingFileHelper exFileHelper;
    public StateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TinTeaTech.MODID, exFileHelper);
        this.exFileHelper = exFileHelper;
    }
    
    @Override
    protected void registerStatesAndModels() {
        for(var entry : MetaTileEntity.mteMap.entrySet()){
            this.simpleBlock(entry.getValue().getBlock(),this.models()
                            .getExistingFile(TinTeaTech.ttResource("brick_particle"))
//                   new BlockModelBuilder(TinTeaTech.ttResource("block/"+entry.getKey()),this.exFileHelper)
//                            .texture("particle",new ResourceLocation(TinTeaTech.FLAME_REACTION_MODID,"block/solid_fuel_burning_box"))
        );
        }
        
    }
}
