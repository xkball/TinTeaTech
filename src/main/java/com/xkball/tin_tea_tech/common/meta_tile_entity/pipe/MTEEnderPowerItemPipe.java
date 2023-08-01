package com.xkball.tin_tea_tech.common.meta_tile_entity.pipe;

import com.xkball.tin_tea_tech.api.annotation.*;
import com.xkball.tin_tea_tech.api.capability.item.TTItemHandler;
import com.xkball.tin_tea_tech.api.pipe.network.PipeNet;
import com.xkball.tin_tea_tech.capability.item.TTCommonItemHandler;
import com.xkball.tin_tea_tech.client.render.PipeRender;
import com.xkball.tin_tea_tech.common.blocks.te.PipeMTEBlock;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.pipe.net.ItemPipeNet;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
@AutomaticRegistration
@AutomaticRegistration.MTE(block = PipeMTEBlock.class,renderer = "com.xkball.tin_tea_tech.client.render.PipeRender")
@Model(resources = {"tin_tea_tech:block/pipe_default"})
@I18N(chinese = "末影物品管道",english = "Ender Power Item Pipe")
@Tag.Item({"pipe"})
public class MTEEnderPowerItemPipe extends MTEPipe{
    public MTEEnderPowerItemPipe(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
    }
    
    @Override
    public int defaultColor() {
        return ColorUtils.getColor(106,171,115,255);
    }
    
    @Override
    public MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te) {
        return new MTEEnderPowerItemPipe(pos,te);
    }
    
    @Override
    protected Supplier<TTItemHandler> getItemHandlerSupplier() {
        return () -> new TTCommonItemHandler(1){
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }
        };
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            return LazyOptional.empty();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public Collection<Component> getInfo() {
        var result = new ArrayList<>(super.getInfo());
        var b = getBelongs();
        if(b instanceof ItemPipeNet net){
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
    public PipeNet createPipeNet() {
        return new ItemPipeNet(this);
    }
    
    @Override
    public boolean havePipeNet() {
        return true;
    }
    
    @Override
    public Class<? extends BlockEntityRenderer<?>> getRendererClass() {
        return PipeRender.class;
    }
}
