package com.xkball.tin_tea_tech.common.blocks.te;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.tin_tea_tech.api.facing.FacingType;
import com.xkball.tin_tea_tech.common.item_behaviour.WrenchBehaviour;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TTTileEntityBlock extends BaseEntityBlock {
    
    final String mteName;
    final boolean needTicker;
    public TTTileEntityBlock(String mteName) {
        super(BlockBehaviour.Properties.of().noOcclusion());
        this.mteName = mteName;
        this.needTicker = MetaTileEntity.mteMap.get(mteName).needTicker();
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new TTTileEntityBase(pos,blockState);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getDefaultMTE().getShape(pState,pLevel,pPos,pContext);
    }
    
    
    //渲染交给自己解决
//    @Override
//    public RenderShape getRenderShape(BlockState p_49232_) {
//        return RenderShape.MODEL;
//    }
    
    @OnlyIn(Dist.CLIENT)
    public void rotateBlockModel(BlockState blockState, MetaTileEntity mte, PoseStack pPoseStack){
    
    }
    
    public MetaTileEntity getDefaultMTE(){
        return MetaTileEntity.mteMap.get(mteName);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        if(!needTicker) return null;
        return level.isClientSide ?
                createTickerHelper(blockEntityType, AutoRegManager.TILE_ENTITY_BASE.get(), TTTileEntityBase::clientTick) :
                createTickerHelper(blockEntityType, AutoRegManager.TILE_ENTITY_BASE.get(), TTTileEntityBase::tick);
    }
    
    //怪物不能生成
    @Override
    public boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
        return false;
    }
    
    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        var te = level.getBlockEntity(pos);
        if(te instanceof TTTileEntityBase && !level.isClientSide){
            if(!((TTTileEntityBase) te).getMte().beforeExplosion()) return;
        }
        super.onBlockExploded(state, level, pos, explosion);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        var te = pLevel.getBlockEntity(pPos);
        if(te instanceof TTTileEntityBase && !pLevel.isClientSide){
            var itemStack = pPlayer.getItemInHand(pHand);
            if(itemStack.is((Item) AutoRegManager.getRegistryObject(WrenchBehaviour.class).get())){
                return ((TTTileEntityBase) te).getMte().useByWrench(pPlayer,pHand,pHit);
            }
            if(pPlayer.isShiftKeyDown()){
                return ((TTTileEntityBase) te).getMte().useShift(pPlayer,pHand,pHit);
            }
            else {
                return ((TTTileEntityBase) te).getMte().use(pPlayer,pHand,pHit);
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
    
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if(pState == pNewState) return;
        if(pState.getBlock() == pNewState.getBlock()) return;
        var te = pLevel.getBlockEntity(pPos);
        if(te instanceof TTTileEntityBase && !pLevel.isClientSide){
            var mte = ((TTTileEntityBase) te).getMte();
            mte.onRemove();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        var te = pLevel.getBlockEntity(pPos);
        if(te instanceof TTTileEntityBase && !pLevel.isClientSide){
            var mte = ((TTTileEntityBase) te).getMte();
            mte.onNeighborChanged(pState,pLevel,pPos,pBlock,pFromPos,pIsMoving);
            var d = pFromPos.subtract(pPos);
            var direction = Direction.fromDelta(d.getX(),d.getY(),d.getZ());
            var face = mte.getFacingType(pState,direction);
            if(face != FacingType.Common){
                mte.onFacingChanged(direction,face,pState,pLevel,pPos,pBlock,pFromPos,pIsMoving);
            }
            
        }
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
    }
    
    public String getMteName() {
        return mteName;
    }
    
    public FacingType getFacingType(BlockState state,Direction direction){
        return FacingType.Common;
    }
    
    @Nullable
    public Direction getFacingDirection(BlockState blockState,FacingType facingType){
        return null;
    }
    
}
