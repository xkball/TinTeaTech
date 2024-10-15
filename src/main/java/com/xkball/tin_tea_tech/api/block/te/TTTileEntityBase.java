package com.xkball.tin_tea_tech.api.block.te;

import com.xkball.tin_tea_tech.api.annotation.NonNullByDefault;
import com.xkball.tin_tea_tech.api.mte.MTEType;
import com.xkball.tin_tea_tech.common.mte.ticker.MTEClientTicker;
import com.xkball.tin_tea_tech.common.mte.ticker.MTETicker;
import com.xkball.tin_tea_tech.api.mte.IMTEBehaviour;
import com.xkball.tin_tea_tech.common.block.TinTeaTechBlocks;
import com.xkball.tin_tea_tech.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NonNullByDefault
public class TTTileEntityBase extends BlockEntity {
    
    private final List<IMTEBehaviour<?>> behaviours = new ArrayList<>(2);
    private final List<IMTEBehaviour<?>> needInitBehaviours = new ArrayList<>(2);
    
    public TTTileEntityBase(BlockPos pos, BlockState blockState) {
        super(TinTeaTechBlocks.TILE_ENTITY_BASE.holder.get(), pos, blockState);
    }
    
    public void addMTE(IMTEBehaviour<?> behaviour){
        this.behaviours.add(behaviour);
        if(behaviour.needTicker(false) || behaviour.needTicker(true)) this.needInitBehaviours.add(behaviour);
        ModUtils.updateBlockEntityTicker(getLevel(),getBlockPos());
    }
    
    public boolean haveMTE(MTEType<?> type){
        if(type == null) return false;
        return behaviours.stream().anyMatch(b -> b.getType().equals(type));
    }
    
    
    public BlockEntityTicker<TTTileEntityBase> getTicker(Level level){
        if(level.isClientSide()){
            var l1 = behaviours.stream().filter(b -> b.needTicker(true)).toList();
            var l2 = needInitBehaviours.stream().filter(b -> b.needTicker(true)).toList();
            needInitBehaviours.removeAll(l2);
            if(l1.isEmpty()) return null;
            return new MTEClientTicker(l1,l2);
        }
        else{
            var l1 = behaviours.stream().filter(b -> b.needTicker(false)).toList();
            var l2 = needInitBehaviours.stream().filter(b -> b.needTicker(false)).toList();
            needInitBehaviours.removeAll(l2);
            if(l1.isEmpty()) return null;
            return new MTETicker(l1,l2);
        }
    }
    
    public void writeTo(CompoundTag tag,HolderLookup.Provider registries) {
        var ops = RegistryOps.create(NbtOps.INSTANCE,registries);
        for(var mte : behaviours){
        
        }
    }
    
    public void readFrom(CompoundTag tag,HolderLookup.Provider registries) {
    
    }

//todo 从客户端同步
//    @Override
//    public void update(CompoundTag tag, HolderLookup.Provider registries) {
//        readFrom(tag, registries);
//    }
//    @Override
//    public void writeToPacket(CompoundTag tag, HolderLookup.Provider registries) {
//        writeTo(tag, registries);
//    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.writeTo(tag,registries);
        super.saveAdditional(tag, registries);
    }
    
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.readFrom(tag,registries);
    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        var result = super.getUpdateTag(registries);
        this.writeTo(result,registries);
        return result;
    }
    
    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.readFrom(tag,lookupProvider);
        super.handleUpdateTag(tag, lookupProvider);
    }
    
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        this.handleUpdateTag(pkt.getTag(),lookupProvider);
    }
}
