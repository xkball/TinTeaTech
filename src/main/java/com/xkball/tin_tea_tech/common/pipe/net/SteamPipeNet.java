package com.xkball.tin_tea_tech.common.pipe.net;

import com.xkball.tin_tea_tech.api.capability.steam.ISteamHolder;
import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.api.pipe.network.PipeNetImpl;
import com.xkball.tin_tea_tech.capability.TTCapability;
import com.xkball.tin_tea_tech.capability.steam.SteamHolder;
import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.net.MTEPipeWithNet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SteamPipeNet extends PipeNetImpl {
    protected final Map<BlockPos, Connections> inputs = new Object2ObjectOpenHashMap<>();
    protected final Map<BlockPos, Connections> outputs = new Object2ObjectOpenHashMap<>();
    
    public final SteamHolder storage = new SteamHolder(20){
        @Override
        public void onChange() {
            centerMTE.markDirty();
        }
    };
    
    public final LazyOptional<ISteamHolder> storageCap = LazyOptional.of(() -> storage);
    
    public SteamPipeNet(MTEPipeWithNet mte) {
        super(mte);
    }
    
    @Override
    public void newInput(BlockPos pos, Connections c) {
        var te = level.getBlockEntity(pos);
        if(te != null && te.getCapability(TTCapability.STEAM,c.nullableToDirection()).isPresent()){
            inputs.put(pos,c);
        }
    }
    
    @Override
    public void _checkNet(boolean force, BlockPos... removed) {
        super._checkNet(force, removed);
        var s = size();
        storage.setVolume(s*20);
    }
    
    @Override
    public void newOutput(BlockPos pos, Connections c) {
        var te = level.getBlockEntity(pos);
        if(te != null && te.getCapability(TTCapability.STEAM,c.nullableToDirection()).isPresent()){
            outputs.put(pos,c);
        }
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        storage.deserializeNBT(tag.getCompound("steam"));
    }
    
    @Override
    public CompoundTag _save() {
        var result = super._save();
        result.put("steam",storage.serializeNBT());
        return result;
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == TTCapability.STEAM)
            return storageCap.cast();
        return super.getCapability(cap, side);
    }
    
    @Override
    public Collection<Map<?,?>> needClear() {
        return List.of(inputs,outputs);
    }
    
    
    public Map<BlockPos, Connections> getInputs() {
        return inputs;
    }
    
    public Map<BlockPos, Connections> getOutputs() {
        return outputs;
    }
}
