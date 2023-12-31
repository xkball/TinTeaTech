package com.xkball.tin_tea_tech.common.pipe.net;

import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.api.pipe.network.PipeNetImpl;
import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.net.MTEPipeWithNet;
import com.xkball.tin_tea_tech.utils.ItemUtils;
import com.xkball.tin_tea_tech.utils.TTUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FluidPipeNet extends PipeNetImpl {
    
    protected final Map<BlockPos, Connections> inputs = new Object2ObjectOpenHashMap<>();
    protected final Map<BlockPos, Connections> outputs = new Object2ObjectOpenHashMap<>();
    
    public FluidPipeNet(MTEPipeWithNet mte) {
        super(mte);
    }
    
    @Override
    @SuppressWarnings("SuspiciousToArrayCall")
    public void doTick() {
        if(getOffsetTick()%5==0){
            var input = inputs.entrySet().stream().map((entry) -> {
                var te = level.getBlockEntity(entry.getKey());
                if(te != null){
                    return te.getCapability(ForgeCapabilities.FLUID_HANDLER,entry.getValue().nullableToDirection());}
                return LazyOptional.empty();
            }).filter(LazyOptional::isPresent).map((o) -> o.orElseThrow(TTUtils.exceptionSupplier)).toArray(IFluidHandler[]::new);
            var output = outputs.entrySet().stream().map((entry) -> {
                var te = level.getBlockEntity(entry.getKey());
                if(te != null){
                    return te.getCapability(ForgeCapabilities.FLUID_HANDLER,entry.getValue().nullableToDirection());}
                return LazyOptional.empty();
            }).filter(LazyOptional::isPresent).map((o) -> o.orElseThrow(TTUtils.exceptionSupplier)).toArray(IFluidHandler[]::new);
            
            int max = Math.max(size()/3,1)*2000;
            for(var o : output){
                for(var i : input){
                    max = ItemUtils.transportFluidWithCount(i,o,max);
                    if(max<=0) return;
                }
            }
        }
    }
    
    @Override
    public void newInput(BlockPos pos, Connections c) {
        var te = level.getBlockEntity(pos);
        if(te != null && te.getCapability(ForgeCapabilities.FLUID_HANDLER,c.nullableToDirection()).isPresent()){
            inputs.put(pos,c);
        }
    }
    
    @Override
    public void newOutput(BlockPos pos, Connections c) {
        var te = level.getBlockEntity(pos);
        if(te != null && te.getCapability(ForgeCapabilities.FLUID_HANDLER,c.nullableToDirection()).isPresent()){
            outputs.put(pos,c);
        }
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
