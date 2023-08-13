package com.xkball.tin_tea_tech.utils;

import com.xkball.tin_tea_tech.api.facing.RelativeFacing;
import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.client.shape.Point3D;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.net.MTEPipeWithNet;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
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
    
    public static Collection<MTEPipeWithNet> getConnected(Level level, BlockPos pos, boolean withSelf){
        var self = getMTE(level,pos);
        if(self instanceof MTEPipeWithNet pipe){
            var result = new LinkedList<MTEPipeWithNet>();
            if(withSelf){
                result.add(pipe);
            }
            for(Connections connection : Connections.values()){
                if(pipe.isConnected(connection) && pipe.isNeighborConnected(connection)){
                    result.add(pipe.getPipeWithNetAt(connection,true));
                }
            }
            return Collections.unmodifiableList(result);
        }
        return Collections.emptySet();
    }
    
    @Nullable
    public static MTEPipeWithNet getPipe(Level level,BlockPos pos){
        var mte = getMTE(level,pos);
        if(mte instanceof MTEPipeWithNet pipe){
            return pipe;
        }
        return null;
    }
    
    //事实上是去年的旧代码
    public static boolean ioWithBlockFluid(Level level, BlockPos pos, Player player, ItemStack item, Direction side){
        var result = false;
        //取出流体
        if(emptyTank(level,pos,player,item,side)) result = true;
            //取出物品
        else if(fillTank(level,pos,player,item,side)) result = true;
        
        return result;
    }
    
    //布尔为操作结果，true为成功
    //把手上液体给罐子
    public static boolean fillTank(Level level,BlockPos pos,Player player, ItemStack item,Direction side){
        //todo 完善流体交互
        //var b1 = FluidUtil.interactWithFluidHandler(player,player.getUsedItemHand(),level,pos,side);
        //if(!b1) {
        //输出桶
        //暂不支持手持储罐，只有桶
        return LevelUtils.emptyBucket(level,pos,player,item,side);
        //}
        //return true;
    }
    
    public static boolean emptyTank(Level level,BlockPos pos,Player player, ItemStack item,Direction side){
        //todo 完善流体交互
        //var b1 = FluidUtil.interactWithFluidHandler(player,player.getUsedItemHand(),level,pos,side);
        //if(!b1){
        return LevelUtils.fillBucket(level, pos, player, item, side);
        //}
        //return true;
    }
    
    public static boolean emptyBucket(Level level,BlockPos pos,Player player, ItemStack item,Direction side){
        if(item.getItem() instanceof BucketItem bucket){
            var blockEntity = level.getBlockEntity(pos);
            if(blockEntity != null){
                AtomicBoolean flag = new AtomicBoolean(false);
                var cap = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER,side);
                cap.ifPresent((iFluidHandler -> {
                    var fluid = new FluidStack(bucket.getFluid(),1000);
                    int c = iFluidHandler.getTanks();
                    for(int i=0;i<c;i++){
                        if(iFluidHandler.isFluidValid(i,fluid)){
                            var s = iFluidHandler.fill(fluid, IFluidHandler.FluidAction.SIMULATE);
                            if(s == 1000){
                                iFluidHandler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                                flag.set(true);
                                SoundEvent soundevent = fluid.getFluid().getFluidType().getSound(SoundActions.BUCKET_EMPTY);
                                if (soundevent != null) {
                                    player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                                }
                                break;
                            }
                        }
                    }
                    
                }));
                if(flag.get()){
                    if(!player.isCreative()) item.shrink(1);
                    var resultItem = new ItemStack(Items.BUCKET,1);
//                    if(!player.getInventory().add(resultItem)){
//                        player.drop(resultItem,false);
//                    }
                    dropItem(player,level,pos,resultItem);
                }
                return flag.get();
            }
        }
        return false;
    }
    
    public static boolean fillBucket(Level level, BlockPos pos, Player player, ItemStack bucket,@Nullable Direction side){
        if(bucket.is(Items.BUCKET)){
            var blockEntity = level.getBlockEntity(pos);
            if(blockEntity != null) {
                var cap = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER,side).orElse(EmptyFluidHandler.INSTANCE);
                if(!(cap instanceof EmptyFluidHandler)){
                    var stimulate = cap.drain(1000, IFluidHandler.FluidAction.SIMULATE);
                    if(stimulate.getAmount() == 1000){
                        var result = cap.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                        var resultItem = new ItemStack(result.getFluid().getBucket(),1);
                        if(!player.isCreative()) bucket.shrink(1);
                        SoundEvent soundevent = result.getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL);
                        if (soundevent != null) {
                            player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
//                        if(!player.getInventory().add(resultItem)){
//                            player.drop(resultItem,false);
//                        }
                        dropItem(player,level,pos,resultItem);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
}
