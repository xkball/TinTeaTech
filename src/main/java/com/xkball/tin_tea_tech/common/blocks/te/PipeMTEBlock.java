package com.xkball.tin_tea_tech.common.blocks.te;

import com.xkball.tin_tea_tech.data.tag.TTItemTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PipeMTEBlock extends TTTileEntityBlock{
    
    
    //实际上是一半的厚度
    //这个值用于渲染,代表像素数
    private final int thickness;
    private final VoxelShape shape;
    
    public PipeMTEBlock(String mteName, int thickness) {
        super(mteName);
        this.thickness = thickness;
        var s = 8-thickness;
        var e = 8+thickness;
        this.shape = Block.box(s,s,s,e,e,e);
        if(thickness<0 || thickness>8){
            throw new IllegalArgumentException("invalid thickness");
        }
    }
    
    public PipeMTEBlock(String mteName){
        this(mteName,2);
    }
    
    public int getThickness() {
        return thickness;
    }
    
    @Override
    public boolean hasDynamicShape() {
        return true;
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if(pContext instanceof EntityCollisionContext ec){
            var entity = ec.getEntity();
            if(entity instanceof Player player){
                var tag = TTItemTags.get("tool");
                return player.getMainHandItem().is(tag) || player.getOffhandItem().is(tag) ?
                         Shapes.block():shape;
            }
        }
        return Shapes.block();
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if(pContext instanceof EntityCollisionContext ec){
            var entity = ec.getEntity();
            if(entity instanceof Player player){
                var tag = TTItemTags.get("pipe");
                return player.getMainHandItem().is(tag) || player.getOffhandItem().is(tag) ?
                        Shapes.block():shape;
            }
        }
        return shape;
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if(pContext instanceof EntityCollisionContext ec){
            var entity = ec.getEntity();
            if(entity instanceof Player player){
                var tag = TTItemTags.get("pipe");
                return player.getMainHandItem().is(tag) || player.getOffhandItem().is(tag) ?
                        Shapes.block():shape;
            }
        }
        return shape;
    }
    
    //    @Override
//    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
//        return super.getShape(pState, pLevel, pPos, pContext);
//    }
}
