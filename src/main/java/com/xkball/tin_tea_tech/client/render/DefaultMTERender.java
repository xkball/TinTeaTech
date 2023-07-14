package com.xkball.tin_tea_tech.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.tin_tea_tech.common.blocks.te.TTTileEntityBlock;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DefaultMTERender implements BlockEntityRenderer<TTTileEntityBase> {
    
    @Override
    public void render(TTTileEntityBase pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        var mte = pBlockEntity.getMte();
        var blockState = pBlockEntity.getBlockState();
        for(var model : mte.needToRender()){
            pPoseStack.pushPose();
            //旋转|预处理
            var block = blockState.getBlock();
            if(block instanceof TTTileEntityBlock){
                ((TTTileEntityBlock) block).rotateBlockModel(blockState,mte,pPoseStack);
            }
            //效果不太行
            //pPoseStack.scale(0.99f,0.99f,0.99f);
            //渲染
            var render = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
            var blockColor = Minecraft.getInstance().getBlockColors();
            var level = mte.getLevel();
            if(level != null){
                var light = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, mte.getPos().above()), level.getBrightness(LightLayer.SKY, mte.getPos().above()));
                int i = blockColor.getColor(blockState, null, null, 0);
                float r = (float)(i >> 16 & 255) / 255.0F;
                float g = (float)(i >>  8 & 255) / 255.0F;
                float b = (float)(i       & 255) / 255.0F;
                
                for (net.minecraft.client.renderer.RenderType rt :
                        model.getRenderTypes(blockState, RandomSource.create(42), ModelData.EMPTY)){
                    render.renderModel(pPoseStack.last(),
                            pBufferSource.getBuffer( RenderTypeHelper.getEntityRenderType(rt, false)),
                            blockState, model, r, g, b, light, pPackedOverlay,ModelData.EMPTY, rt);
                    
                }
            }
            pPoseStack.popPose();
        }
    }
    
}
