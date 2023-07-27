package com.xkball.tin_tea_tech.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public class MTERender implements BlockEntityRenderer<TTTileEntityBase> {
    
    private final Map<Class<? extends BlockEntityRenderer<?>>,BlockEntityRenderer<?>> rendererCache;
    
    public MTERender(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        rendererCache = new HashMap<>();
        for(var mte : MetaTileEntity.mteMap.values()){
            rendererCache.putIfAbsent(mte.getRendererClass(), mte.getRenderer());
        }
    }
    
    @Override
    public void render(TTTileEntityBase pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        var mte = pBlockEntity.getMte();
        @SuppressWarnings("unchecked")
        BlockEntityRenderer<TTTileEntityBase> renderer = (BlockEntityRenderer<TTTileEntityBase>) rendererCache.get(mte.getRendererClass());
        renderer.render(pBlockEntity,pPartialTick,pPoseStack,pBufferSource,pPackedLight,pPackedOverlay);
    }
    
    @Override
    public int getViewDistance() {
        return 256;
    }
    
    @Override
    public boolean shouldRenderOffScreen(TTTileEntityBase pBlockEntity) {
        return true;
    }
}
