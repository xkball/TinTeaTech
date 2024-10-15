package com.xkball.tin_tea_tech.common.blockentity;

import com.xkball.tin_tea_tech.api.annotation.NonNullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@NonNullByDefault
public abstract class CodecBlockEntity<T extends CodecBlockEntity<T>> extends BlockEntity {
    
    public CodecBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {

        super.loadAdditional(tag, registries);
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        var result = super.getUpdateTag(registries);

        return result;
    }
    
    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {

        super.handleUpdateTag(tag, registries);
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
