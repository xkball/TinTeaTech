package com.xkball.tin_tea_tech.common.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.GlowInkSacItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ScaffoldingBlock extends Block implements SimpleWaterloggedBlock {
    
    // private static final Logger LOGGER = LogUtils.getLogger();
    public static final String NAME = "scaffolding_block";
    
    private static final VoxelShape BUTTON_SHAPE;
    private static final VoxelShape UNBUTTON_SHAPE;
    private static final VoxelShape STABLE_SHAPE;
    private static final VoxelShape UNSTABLE_SHAPE_TOP = Block.box(0.0D, 11.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    private static final VoxelShape BELOW_BLOCK = Shapes.block().move(0.0D, -1.0D, 0.0D);
    //破坏时所带的距离
    public static final int MAX_DISTANCE = 8;
    public static final IntegerProperty DISTANCE
            = IntegerProperty.create("scaffolding_count", 0, MAX_DISTANCE);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    
    public static final BooleanProperty BUTTON = BooleanProperty.create("button");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    
    public ScaffoldingBlock() {
        super(Properties
                        .of()
                        .strength(1f, 8f)
                        .sound(SoundType.COPPER)
                        .noOcclusion()
                        .noCollission()
                        .dynamicShape()
                //.speedFactor(2f)
        );
        this.registerDefaultState(
                this.getStateDefinition().any()
                        .setValue(WATERLOGGED, Boolean.FALSE)
                        .setValue(DISTANCE, 0)
                        .setValue(BUTTON, Boolean.TRUE)
                        .setValue(EAST, Boolean.FALSE)
                        .setValue(WEST, Boolean.FALSE)
                        .setValue(NORTH, Boolean.FALSE)
                        .setValue(SOUTH, Boolean.FALSE)
        );

    }
    
    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(
                BUTTON,
                EAST,
                WEST,
                NORTH,
                SOUTH,
                WATERLOGGED,
                DISTANCE
        );
        //super.createBlockStateDefinition(builder);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockPos blockpos = blockPlaceContext.getClickedPos();
        Level level = blockPlaceContext.getLevel();
        var bs = refreshState(this.defaultBlockState(), level, blockpos);
        bs = bs.setValue(WATERLOGGED, level.getFluidState(blockpos).getType() == Fluids.WATER);
        return bs;
    }
    
    @Override
    public boolean canBeReplaced(@Nonnull BlockState p_56037_, BlockPlaceContext p_56038_) {
        return p_56038_.getItemInHand().is(this.asItem());
    }
    
    @Override
    public boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return true;
    }
    
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (!level.isClientSide) {
            if(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof GlowInkSacItem){
                addNeighborDistance(1,level,pos);
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
    
    @Override
    public void neighborChanged(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos,
                                @Nonnull Block un1, @Nonnull BlockPos neighbor, boolean un2) {
        int i = state.getValue(DISTANCE);
        if (!level.isClientSide) {
            var bs1 = level.getBlockState(pos);
            bs1 = this.refreshState(bs1, level, pos);
            level.setBlock(pos, bs1, Block.UPDATE_CLIENTS);
        }
        
        if (!level.isClientSide() && i != 0) {
            if (i != MAX_DISTANCE) {
                addNeighborDistance(i + 1, level, pos);
            }
            level.destroyBlock(pos, true);
        }
    }
    
    public void addNeighborDistance(int i, Level level, BlockPos pos) {
        BlockPos.MutableBlockPos mbpos = pos.mutable();
        for (Direction direction : Direction.values()) {
            mbpos.move(direction);
            var bs = level.getBlockState(mbpos);
            if (bs.getBlock() instanceof ScaffoldingBlock) {
                bs = bs.setValue(DISTANCE, i);
                level.setBlock(mbpos, bs, Block.UPDATE_NONE);
            }
            mbpos.move(direction.getOpposite());
        }
    }
    
    public BlockState refreshState(BlockState blockState, Level level, BlockPos blockPos) {
        if (!level.isClientSide()) {
            var bp1 = blockPos.mutable().move(Direction.DOWN);
            BlockState bs1 = level.getBlockState(bp1);
            if (bs1.isFaceSturdy(level, bp1, Direction.UP) || bs1.getBlock() instanceof ScaffoldingBlock) {
                blockState = blockState.setValue(BUTTON, Boolean.TRUE);
            } else {
                blockState = blockState.setValue(BUTTON, Boolean.FALSE);
            }
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                var bp2 = blockPos.mutable().move(direction);
                BlockState bs2 = level.getBlockState(bp2);
                if (bs2.getBlock() instanceof ScaffoldingBlock) {
                    blockState = blockState.setValue(getFromDirection(direction), Boolean.TRUE);
                } else {
                    blockState = blockState.setValue(getFromDirection(direction), Boolean.FALSE);
                }
            }
        }
        
        return blockState;
    }
    
    public static BooleanProperty getFromDirection(Direction direction) {
        BooleanProperty result = BUTTON;
        switch (direction) {
            case DOWN, UP -> {
            }
            case EAST -> result = EAST;
            case WEST -> result = WEST;
            case NORTH -> result = NORTH;
            case SOUTH -> result = SOUTH;
        }
        
        return result;
    }
    
    static {
        BUTTON_SHAPE = Block.box(0D, 0D, 0D, 16.0D, 16.0D, 16.0D);
        UNBUTTON_SHAPE = Block.box(0D, 11D, 0D, 16.0D, 15.0D, 16.0D);
        VoxelShape voxelshape = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        VoxelShape voxelshape1 = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D);
        VoxelShape voxelshape2 = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
        VoxelShape voxelshape3 = Block.box(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0D);
        VoxelShape voxelshape4 = Block.box(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
        STABLE_SHAPE = Shapes.or(voxelshape, voxelshape1, voxelshape2, voxelshape3, voxelshape4);
    }
    
    @Override
    public void tick(@Nonnull BlockState blockState, @Nonnull ServerLevel serverLevel, @Nonnull BlockPos blockPos, @Nonnull RandomSource random) {
        var bs = refreshState(blockState, serverLevel, blockPos);
        serverLevel.setBlock(blockPos, bs, Block.UPDATE_NONE);
        //LogUtils.getLogger().info("run_tick: "+(serverLevel.nextSubTickCount()-1));
    }
    
    
    @Override
    public @Nonnull VoxelShape getCollisionShape(@Nonnull BlockState blockState, @Nonnull BlockGetter blockGetter,
                                                 @Nonnull BlockPos blockPos, CollisionContext collisionContext) {
        
        if (collisionContext.isAbove(Shapes.block(), blockPos, true) && !collisionContext.isDescending()) {
            return STABLE_SHAPE;
        }
//        if (blockState.getValue(BUTTON)) {
//            return Shapes.empty();
//        }
        else {
            return !blockState.getValue(BUTTON) && collisionContext.isAbove(BELOW_BLOCK, blockPos, true) ? UNSTABLE_SHAPE_TOP : Shapes.empty();
        }
    }
    
    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        pEntity.resetFallDistance();
    }
    
    @Override
    public @Nonnull FluidState getFluidState(BlockState p_56073_) {
        return p_56073_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_56073_);
    }
    
    @Override
    public @Nonnull BlockState updateShape(BlockState blockState, @Nonnull Direction direction, @Nonnull BlockState blockState1,
                                           @Nonnull LevelAccessor levelAccessor, @Nonnull BlockPos blockPos, @Nonnull BlockPos blockPos1) {
        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        
        if (!levelAccessor.isClientSide()) {
            levelAccessor.scheduleTick(blockPos, this, 1);
        }
        
        return blockState;
    }
    
    @Override
    public @Nonnull VoxelShape getInteractionShape(@Nonnull BlockState p_56053_, @Nonnull BlockGetter p_56054_, @Nonnull BlockPos p_56055_) {
        return Shapes.block();
    }
    
    @Override
    public void onPlace(@Nonnull BlockState blockState, @Nonnull Level level, @Nonnull BlockPos blockPos, @Nonnull BlockState p_56065_, boolean p_56066_) {
        var bs = refreshState(blockState, level, blockPos);
        level.setBlock(blockPos, bs, Block.UPDATE_NONE);
    }
    
    @Override
    public @Nonnull VoxelShape getShape(BlockState p_56057_, @Nonnull BlockGetter p_56058_, @Nonnull BlockPos p_56059_, CollisionContext p_56060_) {
        if (!p_56060_.isHoldingItem(p_56057_.getBlock().asItem())) {
            return p_56057_.getValue(BUTTON) ? BUTTON_SHAPE : UNBUTTON_SHAPE;
        } else {
            return Shapes.block();
        }
    }
}
    
