package com.xkball.tin_tea_tech.common.pipe.net;

import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.api.pipe.network.PipeNetImpl;
import com.xkball.tin_tea_tech.capability.energy.TTEnergyStorage;
import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.net.MTEPipeWithNet;
import com.xkball.tin_tea_tech.utils.ItemUtils;
import com.xkball.tin_tea_tech.utils.TTUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FEEnergyPipeNet extends PipeNetImpl {
    
    protected final Map<BlockPos, Connections> inputs = new Object2ObjectOpenHashMap<>();
    protected final Map<BlockPos, Connections> outputs = new Object2ObjectOpenHashMap<>();
    
    public final TTEnergyStorage storage = new TTEnergyStorage(1000){
        @Override
        public void onEnergyChange() {
            centerMTE.markDirty();
        }
    };
    public final LazyOptional<IEnergyStorage> storageCap = LazyOptional.of(() -> storage);
    
    public FEEnergyPipeNet(MTEPipeWithNet mte) {
        super(mte);
    }
    
    @Override
    public void _checkNet(boolean force, BlockPos... removed) {
        super._checkNet(force, removed);
        var s = size();
        storage.setCapacity(s*5000);
        storage.setMaxExtract(s*1000);
        storage.setMaxReceive(s*1000);
    }
    
    @Override
    @SuppressWarnings("SuspiciousToArrayCall")
    public void doTick() {
        if(getOffsetTick()%5==0){
            var input = inputs.entrySet().stream().map((entry) -> {
                var te = level.getBlockEntity(entry.getKey());
                if(te != null){
                    return te.getCapability(ForgeCapabilities.ENERGY,entry.getValue().nullableToDirection());}
                return LazyOptional.empty();
            }).filter(LazyOptional::isPresent).map((o) -> o.orElseThrow(TTUtils.exceptionSupplier)).toArray(IEnergyStorage[]::new);
            var output = outputs.entrySet().stream().map((entry) -> {
                var te = level.getBlockEntity(entry.getKey());
                if(te != null){
                    return te.getCapability(ForgeCapabilities.ENERGY,entry.getValue().nullableToDirection());}
                return LazyOptional.empty();
            }).filter(LazyOptional::isPresent).map((o) -> o.orElseThrow(TTUtils.exceptionSupplier)).toArray(IEnergyStorage[]::new);
            
            for(var i : input){
                ItemUtils.transportFE(i,storage);
            }
            for(var o : output){
                ItemUtils.transportFE(storage,o);
            }
        }
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        storage.setEnergy(tag.getInt("fe"));
    }
    
    @Override
    public CompoundTag _save() {
        var result = super._save();
        result.putInt("fe",storage.getEnergyStored());
        return result;
    }
    
    @Override
    public void newInput(BlockPos pos, Connections c) {
        var te = level.getBlockEntity(pos);
        if(te != null && te.getCapability(ForgeCapabilities.ENERGY,c.nullableToDirection()).isPresent()){
            inputs.put(pos,c);
        }
    }
    
    @Override
    public void newOutput(BlockPos pos, Connections c) {
        var te = level.getBlockEntity(pos);
        if(te != null && te.getCapability(ForgeCapabilities.ENERGY,c.nullableToDirection()).isPresent()){
            outputs.put(pos,c);
        }
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY)
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
