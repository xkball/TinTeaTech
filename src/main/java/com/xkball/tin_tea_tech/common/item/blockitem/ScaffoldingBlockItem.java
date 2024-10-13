package com.xkball.tin_tea_tech.common.item.blockitem;

import com.xkball.tin_tea_tech.common.block.ScaffoldingBlock;
import com.xkball.tin_tea_tech.common.block.TinTeaTechBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ScaffoldingBlockItem extends BlockItem {
    
    public ScaffoldingBlockItem() {
        super(TinTeaTechBlocks.SCAFFOLDING_BLOCK.get(), new Properties());
    }
    
    @Override
    @SuppressWarnings("deprecation")
    protected SoundEvent getPlaceSound(BlockState p_state, Level world, BlockPos pos, Player entity) {
        return TinTeaTechBlocks.SCAFFOLDING_BLOCK.get().defaultBlockState().getSoundType().getPlaceSound();
    }
    
    @Override
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext blockPlaceContext) {
        BlockPos blockpos = blockPlaceContext.getClickedPos();
        Level level = blockPlaceContext.getLevel();
        BlockState blockstate = level.getBlockState(blockpos);
        //指向一个脚手架
        
        if (!(blockstate.getBlock() instanceof ScaffoldingBlock)) {
            return blockPlaceContext;
        }
        else {
            Direction direction;
            //副手使用
            if (blockPlaceContext.isSecondaryUseActive()) {
                direction =
                        //人是不是在方块里面
                        blockPlaceContext.isInside()
                                ? blockPlaceContext.getClickedFace().getOpposite()
                                : blockPlaceContext.getClickedFace();
            }
            //主手使用
            else {
                direction =
                        //向上点击则获得对应方向
                        blockPlaceContext.getClickedFace() == Direction.UP
                                ? blockPlaceContext.getHorizontalDirection()
                                : Direction.UP;
            }
            
            int i = 0;
            @SuppressWarnings("SpellCheckingInspection")
            BlockPos.MutableBlockPos mbpos = blockpos.mutable().move(direction);
            
            while (i < 50) {
                
                //如果放置位置超过高度上限
                if (!level.isInWorldBounds(mbpos)) {
                    Player player = blockPlaceContext.getPlayer();
                    int j = level.getMaxBuildHeight();
                    
                    if (player != null && mbpos.getY() >= j) {
                        player.sendSystemMessage(Component.translatable("build.tooHigh", j - 1)
                                .withStyle(ChatFormatting.RED));
                    }
                    return null;
                }
                
                blockstate = level.getBlockState(mbpos);
                
                if (!blockstate.is(this.getBlock())) {
                    if (blockstate.canBeReplaced(blockPlaceContext)) {
                        return BlockPlaceContext.at(blockPlaceContext, mbpos, direction);
                    }
                    return null;
                }
                
                mbpos.move(direction);
                if (direction.getAxis().isHorizontal()) {
                    ++i;
                }
            }
            return null;
        }
    }
}
