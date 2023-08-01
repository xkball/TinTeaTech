package com.xkball.tin_tea_tech.api.mte.cover;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public interface Cover extends INBTSerializable<CompoundTag> {
    
    int type();
    void tick();
    
    void onRemove();
    
    ItemStack asItemStack();
    
    MetaTileEntity getMTE();
    
    Direction getDirection();
    
    default boolean canRemove(){
        return true;
    }
    @OnlyIn(Dist.CLIENT)
    default void render(TTTileEntityBase pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int light, int pPackedOverlay){}
    
    default @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
        return LazyOptional.empty();
    }
}
