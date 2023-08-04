package com.xkball.tin_tea_tech.common.meta_tile_entity.pipe;

import com.xkball.tin_tea_tech.api.annotation.*;
import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.api.pipe.network.PipeNet;
import com.xkball.tin_tea_tech.client.render.PipeRender;
import com.xkball.tin_tea_tech.common.blocks.te.PipeMTEBlock;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.pipe.net.FEEnergyPipeNet;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
@AutomaticRegistration(needSpecialName = true,name = "fe_energy_pipe")
@AutomaticRegistration.MTE(block = PipeMTEBlock.class,renderer = "com.xkball.tin_tea_tech.client.render.PipeRender")
@Model(resources = {"tin_tea_tech:block/pipe_default"})
@I18N(chinese = "FE能量管道",english = "FE Energy Pipe")
@Tag.Item({"pipe"})
public class MTEFEEnergyPipe extends MTEPipe{
    
    public MTEFEEnergyPipe(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
    }
    
    @Override
    public int defaultColor() {
        return ColorUtils.getColor(199,125,187,255);
    }
    
    @Override
    public MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te) {
        return new MTEFEEnergyPipe(pos, te);
    }
    
    @Override
    public Collection<Component> getInfo() {
        var result = new ArrayList<>(super.getInfo());
        var b = getBelongs();
        if(b instanceof FEEnergyPipeNet net){
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
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY){
            if (side == null || isConnected(Connections.fromDirection(side)))
                return getBelongs().getCapability(cap, side);
            else return LazyOptional.empty();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable("tooltip.tin_tea_tech.not_ti").withStyle(ChatFormatting.GRAY));
    }
    
    @Override
    public boolean havePipeNet() {
        return true;
    }
    
    @Override
    public PipeNet createPipeNet() {
        return new FEEnergyPipeNet(this);
    }
    
    
    @Override
    public Class<? extends BlockEntityRenderer<?>> getRendererClass() {
        return PipeRender.class;
    }
}
