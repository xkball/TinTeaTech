package com.xkball.tin_tea_tech.common.blocks.te;

import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.MTEPipe;
import com.xkball.tin_tea_tech.data.tag.TTItemTags;
import com.xkball.tin_tea_tech.utils.ItemUtils;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import com.xkball.tin_tea_tech.utils.TTUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
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
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PipeMTEBlock extends TTTileEntityBlock{
    private static final double dx1 = 0.99d;
    private static final double dx2 = 1.01d;
    private static final VoxelShape shape = Block.box(6*dx1,6*dx1,6*dx1,10*dx2,10*dx2,10*dx2);
    private static final VoxelShape UP_shape = Block.box(6*dx1,10*dx1,6*dx1,10*dx2,16,10*dx2);
    private static final VoxelShape DOWN_shape= Block.box(6*dx1,0*dx1,6*dx1,10*dx2,6*dx2,10*dx2);
    private static final VoxelShape WEST_shape = Block.box(0,6*dx1,6*dx1,6*dx2,10*dx2,10*dx2);
    private static final VoxelShape EAST_shape = Block.box(10*dx1,6*dx1,6*dx1,16,10*dx2,10*dx2);
    private static final VoxelShape NORTH_shape = Block.box(6*dx1,6*dx1,0,10*dx2,10*dx2,6*dx2);
    private static final VoxelShape SOUTH_shape = Block.box(6*dx1,6*dx1,10*dx1,10*dx2,10*dx2,16);
    
    private static final VoxelShape[] shapes = new VoxelShape[]{UP_shape,DOWN_shape,NORTH_shape,EAST_shape,SOUTH_shape,WEST_shape};
    
    public static final Int2ObjectMap<VoxelShape> collisions;
    
    static {
        collisions = new Int2ObjectLinkedOpenHashMap<>();
        for(int i=0;i<64;i++){
            var bit = TTUtils.forIntToBitSet(i,6);
            ArrayList<VoxelShape> used = new ArrayList<>();
            for(int j=0;j<6;j++){
                if(bit.get(j)) used.add(shapes[j]);
            }
            collisions.put(i,Shapes.or(shape,used.toArray(new VoxelShape[0])));
        }
    }
    
    //实际上是一半的厚度
    //这个值用于渲染,代表像素数
    private final int thickness;
    
    
    public PipeMTEBlock(String mteName, int thickness) {
        super(mteName);
        this.thickness = thickness;
//        var s = 8-thickness;
//        var e = 8+thickness;
        if(thickness<0 || thickness>8){
            throw new IllegalArgumentException("invalid thickness");
        }
    }
    
    @SuppressWarnings("unused")
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
        return _getShape(pState,pLevel,pPos,pContext,Shapes.block());
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return _getShape(pState,pLevel,pPos,pContext,shape);
    }
    
    public VoxelShape _getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext,VoxelShape defaultShape){
        if(pContext instanceof EntityCollisionContext ec){
            var entity = ec.getEntity();
            if(entity instanceof Player player){
                if(ItemUtils.hasTagInHand(player,TTItemTags.get("tool"),TTItemTags.get("pipe"),TTItemTags.get("cover")))
                    return Shapes.block();
                var s = shape;
                var mte = LevelUtils.getMTE(pLevel,pPos);
                if(mte instanceof MTEPipe p){
                    s = collisions.get(p.getCollision());
                }
                return s;
            }
        }
        return defaultShape;
    }
}
