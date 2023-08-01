package com.xkball.tin_tea_tech.data;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class StateProvider extends BlockStateProvider {
    
    final ExistingFileHelper exFileHelper;
    public StateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TinTeaTech.MODID, exFileHelper);
        this.exFileHelper = exFileHelper;
    }
    
    @Override
    protected void registerStatesAndModels() {
        for(var entry : MetaTileEntity.mteMap.entrySet()){
            var clazz = entry.getValue().getClass();
            var block = entry.getValue().getBlock();
            if(clazz.isAnnotationPresent(Model.class) && clazz.isAnnotationPresent(AutomaticRegistration.class) && clazz.getAnnotation(AutomaticRegistration.class).needDataGenModel()){
                this.simpleBlock(block,this.models()
                        .cubeAll(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath(),blockTexture(block)));
                continue;
            }
            this.simpleBlock(block,this.models()
                            .getExistingFile(TinTeaTech.ttResource("brick_particle"))
//                   new BlockModelBuilder(TinTeaTech.ttResource("block/"+entry.getKey()),this.exFileHelper)
//                            .texture("particle",new ResourceLocation(TinTeaTech.FLAME_REACTION_MODID,"block/solid_fuel_burning_box"))
        );
        }
        
    }
}
