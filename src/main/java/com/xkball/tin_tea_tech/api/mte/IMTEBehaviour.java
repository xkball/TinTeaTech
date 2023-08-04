package com.xkball.tin_tea_tech.api.mte;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.facing.FacingType;
import com.xkball.tin_tea_tech.api.mte.cover.Cover;
import com.xkball.tin_tea_tech.api.mte.cover.VerticalCover;
import com.xkball.tin_tea_tech.client.render.DefaultMTERender;
import com.xkball.tin_tea_tech.common.blocks.te.TTTileEntityBlock;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import com.xkball.tin_tea_tech.network.packet.MTEClientToServerDataPacket;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Consumer;

//理论上除了标注client的方法,都在服务端调用
public interface IMTEBehaviour {
    MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te);
    
    default void markDirty(){
        getLevel().blockEntityChanged(getPos());
    }
    
    //capability
    default @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
        return LazyOptional.empty();
    }
    
    VerticalCover getCoverHandler();
    
    default boolean canApplyCover(Direction direction, Cover cover){
        return false;
    }
    
    //block方法
    //所有NC更新都会调用
    default void onNeighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving){}
    
    //跟面有关的NC更新会调用
    default void onFacingChanged(Direction direction, FacingType facingType, BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving){}
    
    default FacingType getFacingType(BlockState state,Direction direction){
        return this.getBlock().getFacingType(state,direction);
    }
    
    @Nullable
    default BlockPlaceContext updateBlockPlaceContext(BlockPlaceContext context){
        return context;
    }
    
    @Nullable
    default Direction getFacingDirection(FacingType facingType){
        return getBlock().getFacingDirection(getBlockState(),facingType);
    }
    //tick相关
    int getOffsetTick();
    
    default void tick(){}
    
    default void clientTick(){}
    
    default void firstTick(){}
    
    default void firstClientTick(){}
    
    default boolean needTicker(){
        return true;
    }
    
    //数据相关
    default void writeInitData(CompoundTag tag){}
    
    default void readInitData(CompoundTag tag){}
    
    void sentCustomData(int id, Consumer<ByteBuf> bufConsumer);
    
    default void readCustomData(int id,ByteBuf byteBuf){}
    
    default void syncRenderData(){}
    
    default void sentToServer(Consumer<CompoundTag> tag){
        var tagToSent = new CompoundTag();
        tag.accept(new CompoundTag());
        TTNetworkHandler.sentToServer(new MTEClientToServerDataPacket(getPos(),tagToSent));
    }
    default void readClientData(CompoundTag data){}
    
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
        try {
            return getRendererClass().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return new DefaultMTERender();
        }
    }
    
    @Nullable
    @OnlyIn(Dist.CLIENT)
    default ResourceLocation getItemModel(){
        return null;
    }
    
    @OnlyIn(Dist.CLIENT)
    default Class<? extends BlockEntityRenderer<?>> getRendererClass(){
        var clazz =  TinTeaTech.STACK_WALKER.getCallerClass();
        if(clazz.isAnnotationPresent(AutomaticRegistration.MTE.class)){
            try {
                //出错了大概也无所谓,会使用默认渲染器
                //noinspection unchecked
                return (Class<? extends BlockEntityRenderer<?>>) AutoRegManager.getRealClass(clazz.getAnnotation(AutomaticRegistration.MTE.class).renderer());
            } catch (Exception e) {
                return DefaultMTERender.class;
            }
        }
        return DefaultMTERender.class;
    }
    
    @OnlyIn(Dist.CLIENT)
    default int getLight(){
        return LightTexture.pack(getLevel().getBrightness(LightLayer.BLOCK, getPos().above()), getLevel().getBrightness(LightLayer.SKY, getPos().above()));
    }
    
    @OnlyIn(Dist.CLIENT)
    default void renderAdditional(TTTileEntityBase pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource,int light,int pPackedOverlay){}
    
    //INFO
    String getName();
    
    default TTTileEntityBlock getBlock(){
        return (TTTileEntityBlock) AutoRegManager.getRegistryObject(getName()).get();
    }
    
    default void addTester(Player player){
        getTileEntity().setTester(player);
    }
    default Item getItem(){
        return (Item) AutoRegManager.getRegistryObject(getName()+"_item").get();
    }
    default Level getLevel(){
        if(getTileEntity() != null)
            return getTileEntity().getLevel();
        return null;
    }
    default BlockPos getPos(){
        return getTileEntity().getBlockPos();
    }
    default BlockState getBlockState(){
        return getLevel().getBlockState(getPos());
    }
}
