package com.xkball.tin_tea_tech.common.tile_entity;

import com.xkball.tin_tea_tech.common.blocks.te.TTTileEntityBlock;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TTTileEntityBase extends BlockEntity {
    
    protected final Int2ObjectMap<byte[]> needUpdateData = new Int2ObjectArrayMap<>(10);
    final MetaTileEntity mte;
    
    final int offset;
    
    boolean firstTicked = false;
    
    public TTTileEntityBase(BlockPos pos, BlockState blockState) {
        super(AutoRegManager.TILE_ENTITY_BASE.get(), pos, blockState);
        if(blockState.getBlock() instanceof TTTileEntityBlock teBlock){
            this.mte = MetaTileEntity.mteMap.get(teBlock.getMteName()).newMetaTileEntity(pos,this);
        }
        else {
            throw new RuntimeException("");
        }
        offset = ThreadLocalRandom.current().nextInt(20);
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, TTTileEntityBase entity){
        if(!entity.firstTicked){
            entity.firstTicked = true;
            entity.mte.firstTick();
        }
        entity.mte.tick();
    }
    
    public static void clientTick(Level level,BlockPos pos,BlockState state,TTTileEntityBase entity){
        if(!entity.firstTicked){
            entity.firstTicked = true;
            entity.mte.firstClientTick();
        }
        entity.mte.clientTick();
    }
    
    public void writeInitData(CompoundTag byteBuf){
        this.mte.writeInitData(byteBuf);
    }
    
    public void readInitData(CompoundTag byteBuf){
        this.mte.readInitData(byteBuf);
    }
    
    public void sentCustomData(int id, Consumer<ByteBuf> bufConsumer){
        var byteBuff = Unpooled.buffer();
        bufConsumer.accept(byteBuff);
        byte[] data = Arrays.copyOfRange(byteBuff.array(),0,byteBuff.writerIndex());
        needUpdateData.put(id,data);
        if(this.level != null){
            var state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition,state,state,2);
        }
    }
    
    
    public void readCustomData(int id,ByteBuf byteBuf){
        this.mte.readCustomData(id,byteBuf);
    }
    
    @Override
    public void saveToItem(ItemStack itemStack) {
        BlockItem.setBlockEntityData(itemStack, this.getType(), this.saveWithoutMetadata()
                .merge(getUpdateTag()));
    }
    
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        if(!needUpdateData.isEmpty()){
            var tag = new CompoundTag();
            var list = new ListTag();
            for(var entry : needUpdateData.int2ObjectEntrySet()){
                var tagIn = new CompoundTag();
                tagIn.putByteArray(String.valueOf(entry.getIntKey()),entry.getValue());
                list.add(tagIn);
            }
            tag.put("data",list);
            tag.putByte("type",list.getElementType());
            needUpdateData.clear();
            return ClientboundBlockEntityDataPacket.create(this,(e) -> tag);
        }
        return super.getUpdatePacket();
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        //super.onDataPacket(net, pkt); //按理说里面并不会有forge的数据
        var tag = pkt.getTag();
        if(tag != null){
            var list = tag.getList("data",tag.getByte("type"));
            for(int i = 0;i < list.size();i++){
                var tagIn = list.getCompound(i);
                //实际上不会循环
                for(String id : tagIn.getAllKeys()){
                    ByteBuf byteBuf = Unpooled.copiedBuffer(tagIn.getByteArray(id));
                    readCustomData(Integer.parseInt(id),byteBuf);
                }
            }
        }
    }
//    @Override
//    public CompoundTag getUpdateTag() {
//        var result = super.getUpdateTag();
//        //var byteBuff = Unpooled.buffer();
//        writeInitData(result);
//        //result.putByteArray("data", Arrays.copyOfRange(byteBuff.array(),0,byteBuff.writerIndex()));
//        return result;
//    }
//
//    @Override
//    public void handleUpdateTag(CompoundTag tag) {
//        super.handleUpdateTag(tag); //forge data
////        byte[] data = tag.getByteArray("data");
////        ByteBuf byteBuf = Unpooled.copiedBuffer(data);
//        readInitData(tag);
//    }
    
    
    
    //暂时不清楚需不需要
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        readInitData(nbt);
    }

    @Override
    public CompoundTag serializeNBT() {
        var result = super.serializeNBT();
        writeInitData(result);
        return result;
    }
    
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        readInitData(pTag);
    }
    
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        writeInitData(pTag);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        var mteResult = mte.getCapability(cap,side);
        if(mteResult.isPresent()){
           return mteResult;
        }
        return super.getCapability(cap, side);
    }
    
    public MetaTileEntity getMte() {
        return mte;
    }
    
    public int getOffset() {
        return offset;
    }
}
