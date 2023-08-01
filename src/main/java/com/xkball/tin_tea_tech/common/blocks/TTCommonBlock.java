package com.xkball.tin_tea_tech.common.blocks;

import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TTCommonBlock extends Block {
    public TTCommonBlock() {
        super(BlockBehaviour.Properties.of());
    }
    
    public TTCommonBlock(Properties properties){
        super(properties);
    }
    
    @AutomaticRegistration
    @AutomaticRegistration.Block
    @CreativeTag
    public static class CopperFrame extends TTCommonBlock{
        public CopperFrame(){
            super(BlockBehaviour.Properties.of().noOcclusion());
        }
    }
    
    @AutomaticRegistration
    @AutomaticRegistration.Block
    @CreativeTag
    public static class CopperWall extends TTCommonBlock{}
    @AutomaticRegistration
    @AutomaticRegistration.Block
    @CreativeTag
    public static class CopperCoil extends TTCommonBlock{}
}
