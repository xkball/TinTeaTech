package com.xkball.tin_tea_tech.common.blocks.te;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.xkball.tin_tea_tech.api.block.IRotatable;
import com.xkball.tin_tea_tech.api.facing.FacingType;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HorizontalMTEBlock extends TTTileEntityBlock implements IRotatable {
    
    public static final DirectionProperty horizontalFacing = BlockStateProperties.HORIZONTAL_FACING;
    
    public HorizontalMTEBlock(String mteName) {
        super(mteName);
        this.registerDefaultState(this.getStateDefinition().any().setValue(horizontalFacing, Direction.NORTH));
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void rotateBlockModel(BlockState blockState, MetaTileEntity mte, PoseStack pPoseStack) {
        switch (blockState.getValue(horizontalFacing)) {
            case EAST -> {
//                pPoseStack.mulPose(Axis.YN.rotationDegrees(90));
//                pPoseStack.translate(0d, 0d, -1d);
                pPoseStack.rotateAround(Axis.YN.rotationDegrees(90),0.5f,0.5f,0.5f);
            }

            case SOUTH-> {
//                pPoseStack.mulPose(Axis.YN.rotationDegrees(180));
//                pPoseStack.translate(-1d, 0d, -1d);
                pPoseStack.rotateAround(Axis.YN.rotationDegrees(180),0.5f,0.5f,0.5f);
                
            }
            case WEST -> {
//                pPoseStack.mulPose(Axis.YP.rotationDegrees(90));
//                pPoseStack.translate(-1d, 0d, 0d);
                pPoseStack.rotateAround(Axis.YN.rotationDegrees(270),0.5f,0.5f,0.5f);
            }
        }
    }
    
    @Override
    public FacingType getFacingType(BlockState state,Direction direction) {
        return direction == state.getValue(horizontalFacing) ? FacingType.MainFace : FacingType.Common;
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(horizontalFacing);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(horizontalFacing,
                pContext.getHorizontalDirection().getOpposite());
    }
    
    @Override
    public @Nullable Direction getFacingDirection(BlockState blockState,FacingType facingType) {
        if(facingType == FacingType.MainFace){
            return blockState.getValue(horizontalFacing);
        }
        return super.getFacingDirection(blockState,facingType);
    }
    
    @Override
    public boolean rotation(Level level, BlockPos pos, BlockState blockState, Direction to) {
        if(to.getAxis() != Direction.Axis.Y){
            blockState = blockState.setValue(horizontalFacing,to);
            level.setBlock(pos,blockState,1);
            return true;
        }
        return false;
    }
    
    @Override
    public void afterRotation(Level level,BlockPos pos, BlockState blockState, Direction to) {
        LevelUtils.getMTEAndExecute(level,pos,(mte) -> mte.afterRotation(to));
    }
}
