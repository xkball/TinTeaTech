package com.xkball.tin_tea_tech.utils;

import com.xkball.tin_tea_tech.api.facing.RelativeFacing;
import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.client.shape.Point3D;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.MTEPipe;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.Consumer;

public class LevelUtils {
    
    @Nullable
    public static MetaTileEntity getMTE(BlockGetter level, BlockPos pos){
        var te = level.getBlockEntity(pos);
        if(te instanceof TTTileEntityBase){
            return ((TTTileEntityBase) te).getMte();
        }
        return null;
    }
    
    public static boolean isSameMTE(MetaTileEntity self,Level level,BlockPos pos) {
        var mte = getMTE(level,pos);
        return mte != null && mte.getName().equals(self.getName());
    }
    
    public static void getMTEAndExecute(Level level, BlockPos pos, Consumer<MetaTileEntity> todo){
        var mte = getMTE(level,pos);
        if(mte != null){
            todo.accept(mte);
        }
    }
    
    public static void dropItem(Level level, BlockPos pos, IItemHandler itemHandler){
        for(int i=0;i<itemHandler.getSlots();i++){
            var item = itemHandler.getStackInSlot(i);
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item);
        }
    }
    
    public static void dropItem(Player player,Level level, BlockPos pos, IItemHandler itemHandler){
       dropItem(player,level,pos,itemHandler,64);
    }
    
    public static void dropItem(Player player,Level level, BlockPos pos, IItemHandler itemHandler,int amount){
        for(int i=0;i<itemHandler.getSlots();i++){
            var item = itemHandler.extractItem(i,amount,false);
            if(item.isEmpty()) continue;
            if(putItemToMainHand(player,item)){
                continue;
            }
            if(!player.addItem(item)){
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item);
            }
        }
    }
    
    public static void dropItem(Player player,Level level,BlockPos pos,ItemStack itemStack){
        if(!itemStack.isEmpty()){
            if(!putItemToMainHand(player,itemStack)){
                if(!player.addItem(itemStack)){
                    Containers.dropItemStack(level,pos.getX(),pos.getY(),pos.getZ(),itemStack);
                }
            }
        }
    }
    
    public static void dropItem(Level level,BlockPos pos,ItemStack itemStack){
        Containers.dropItemStack(level,pos.getX(),pos.getY(),pos.getZ(),itemStack);
    }
    
    public static boolean putItemToMainHand(Player player, ItemStack item){
        if(!player.getMainHandItem().isEmpty()) return false;
        player.getInventory().setItem(player.getInventory().selected,item);
        return true;
    }
    
    public static RelativeFacing getHitFacing(UseOnContext context){
        var pos = context.getClickedPos();
        var location = context.getClickLocation();
        var direction = context.getClickedFace();
        return new Point3D(location,pos).to2D(direction).toRelativeFacing();
    }
    
    public static Connections useConnection(BlockHitResult hit){
        var locate = hit.getLocation();
        var direction = hit.getDirection();
        var pos = hit.getBlockPos();
        
        return new Point3D(locate,pos)
                .to2D(direction)
                .toRelativeFacing()
                .toConnection(direction);
    }
    public static Connections useConnection(UseOnContext hit){
        var locate = hit.getClickLocation();
        var direction = hit.getClickedFace();
        var pos = hit.getClickedPos();
        
        return new Point3D(locate,pos)
                .to2D(direction)
                .toRelativeFacing()
                .toConnection(direction);
    }
    
    public static Collection<MTEPipe> getConnected(Level level,BlockPos pos){
        var self = getMTE(level,pos);
        if(self instanceof MTEPipe pipe){
            var result = new LinkedList<MTEPipe>();
            for(Connections connection : Connections.values()){
                if(pipe.isConnected(connection) && pipe.isNeighborConnected(connection)){
                    result.add(pipe.getPipeAt(connection,true));
                }
            }
            return Collections.unmodifiableList(result);
        }
        return Collections.emptySet();
    }
    
}
