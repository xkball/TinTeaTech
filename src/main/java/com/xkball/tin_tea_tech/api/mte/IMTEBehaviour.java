package com.xkball.tin_tea_tech.api.mte;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.xkball.tin_tea_tech.api.annotation.NonNullByDefault;
import com.xkball.tin_tea_tech.api.block.te.TTTileEntityBase;
import com.xkball.tin_tea_tech.api.facing.FacingType;
import com.xkball.tin_tea_tech.common.block.TTTileEntityBlock;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

//理论上除了标注client的方法,都在服务端调用
@NonNullByDefault
@SuppressWarnings("unused")
public interface IMTEBehaviour<T extends IMTEBehaviour<T>> {
    
    MTEType<T> getType();
    
    default void markDirty(){
        getTileEntity().setChanged();
        Objects.requireNonNull(getLevel()).blockEntityChanged(getPos());
    }
    
    //TODO: 重做: capability 覆盖版
    
    //block方法
    //所有NC更新都会调用
    default boolean onNeighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving){
        return false;
    }
    
    //跟面有关的NC更新会调用
    default boolean onFacingChanged(Direction direction, FacingType facingType, BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving){
        return false;
    }
    
    default FacingType getFacingType(BlockState state,Direction direction){
        //todo 重做
        return FacingType.Common;
    }
    
    @Nullable
    default BlockPlaceContext updateBlockPlaceContext(BlockPlaceContext context){
        return context;
    }
    
    Codec<T> codec();

    //tick相关
    int getOffsetTick();
    
    default void tick(){}
    
    default void clientTick(){}
    
    default void firstTick(){}
    
    default void firstClientTick(){}
    
    default boolean needTicker(boolean isClientSide){
        return true;
    }
    
    //数据相关
    //todo 重做
    
    //与玩家的交互
    default InteractionResult use(Player pPlayer, InteractionHand pHand, BlockHitResult pHit){
        return InteractionResult.PASS;
    }
    
    default InteractionResult useShift(Player pPlayer, InteractionHand pHand, BlockHitResult pHit){
        return InteractionResult.PASS;
    }
    
    //item
    default boolean isFoil(ItemStack stack){
        return false;
    }
    
    default void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
    
    }
    
    //与世界的交互
    //返回false会阻止自己被炸掉
    default void onRemove(){}
    default boolean beforeExplosion(){
        return true;
    }

    TTTileEntityBase getTileEntity();
    default void afterRotation(Direction to) {}
    
    default InteractionResult useByWrench(Player pPlayer, InteractionHand pHand, BlockHitResult pHit){
        return InteractionResult.PASS;
    }
    
    default VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext){
        return Shapes.block();
    }
    
    //render
    //应该全部ClientOnly?
    
    //默认的渲染方式
    //默认北面为正面
    @OnlyIn(Dist.CLIENT)
    BakedModel[] needToRender();
    
    @OnlyIn(Dist.CLIENT)
    default BlockEntityRenderer<?> getRenderer(){
        return null;
    }
    
    @Nullable
    @OnlyIn(Dist.CLIENT)
    default ResourceLocation getItemModel(){
        return null;
    }
    
    @OnlyIn(Dist.CLIENT)
    default Class<? extends BlockEntityRenderer<?>> getRendererClass(){
        return null;
    }
    
    @OnlyIn(Dist.CLIENT)
    default int getLight(){
        return LightTexture.pack(getLevel().getBrightness(LightLayer.BLOCK, getPos().above()), getLevel().getBrightness(LightLayer.SKY, getPos().above()));
    }
    
    @OnlyIn(Dist.CLIENT)
    default void renderAdditional(TTTileEntityBase pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource,int light,int pPackedOverlay){
    
    }
    
    //INFO
    String getName();
    
    default TTTileEntityBlock getBlock(){
        return (TTTileEntityBlock) getTileEntity().getBlockState().getBlock();
    }
    
    //扫描仪用
    default void addTester(Player player){
    
    }
    
    default Item getItem(){
        return getBlock().asItem();
    }
    
    @Nullable
    default Level getLevel(){
        return getTileEntity().getLevel();
    }
    default BlockPos getPos(){
        return getTileEntity().getBlockPos();
    }
    
    @Nullable
    default BlockState getBlockState(){
        if(getLevel() == null) return null;
        return getLevel().getBlockState(getPos());
    }
}
