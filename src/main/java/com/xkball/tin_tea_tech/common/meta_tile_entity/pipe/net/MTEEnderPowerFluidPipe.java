package com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.net;

import com.xkball.tin_tea_tech.api.annotation.*;
import com.xkball.tin_tea_tech.api.pipe.network.PipeNet;
import com.xkball.tin_tea_tech.client.render.PipeRender;
import com.xkball.tin_tea_tech.common.blocks.te.PipeMTEBlock;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.pipe.net.FluidPipeNet;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
@AutomaticRegistration
@AutomaticRegistration.MTE(block = PipeMTEBlock.class,renderer = "com.xkball.tin_tea_tech.client.render.PipeRender")
@Model(resources = {"tin_tea_tech:block/pipe_default"})
@I18N(chinese = "末影流体管道",english = "Ender Power Fluid Pipe")
@Tag.Item({"pipe"})
public class MTEEnderPowerFluidPipe extends MTEPipeWithNet {
    
    public MTEEnderPowerFluidPipe(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
    }
    
    @Override
    public int defaultColor() {
        return ColorUtils.getColor(0x429E9D,255);
    }
    
    @Override
    public MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te) {
        return new MTEEnderPowerFluidPipe(pos, te);
    }
    
    @Override
    public Collection<Component> getInfo() {
        var result = new ArrayList<>(super.getInfo());
        var b = getBelongs();
        if(b instanceof FluidPipeNet net){
            for(var i:net.getInputs().entrySet()){
                result.add(Component.literal("input:"+i.getKey().toString()+" "+i.getValue().name()));
            }
            for(var o:net.getOutputs().entrySet()){
                result.add(Component.literal("output:"+o.getKey().toString()+" "+o.getValue().name()));
            }
        }
        
        return result;
    }
    
    @Override
    public boolean havePipeNet() {
        return true;
    }
    
    @Override
    public PipeNet createPipeNet() {
        return new FluidPipeNet(this);
    }
    
    @Override
    public Class<? extends BlockEntityRenderer<?>> getRendererClass() {
        return PipeRender.class;
    }
}
