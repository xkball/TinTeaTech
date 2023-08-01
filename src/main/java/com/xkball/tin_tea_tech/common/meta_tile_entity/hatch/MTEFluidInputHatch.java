package com.xkball.tin_tea_tech.common.meta_tile_entity.hatch;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.capability.multiblock.IFluidInput;
import com.xkball.tin_tea_tech.api.capability.multiblock.ISteamInput;
import com.xkball.tin_tea_tech.capability.TTCapability;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
@AutomaticRegistration(needDataGenModel = true)
@AutomaticRegistration.MTE
@Model(resources = {"tin_tea_tech:block/fluid_input_hatch"})
@I18N(chinese = "流体输入仓",english = "Fluid Input Hatch")
public class MTEFluidInputHatch extends MetaTileEntity implements IFluidInput {
    
    private static final ResourceLocation MODEL = TinTeaTech.ttResource("block/fluid_input_hatch");
    protected int lastIn = 0;
    
    protected final LazyOptional<IFluidInput> selfCap;
    
    public MTEFluidInputHatch(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
        selfCap = LazyOptional.of(() -> this);
    }
    
    @Override
    protected Supplier<IFluidHandler> getFluidHandlerSupplier() {
        return () -> new FluidTank(16000){
            @Override
            protected void onContentsChanged() {
                markDirty();
            }
        };
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == TTCapability.MULTI_BLOCK_FLUID_IN){
            return selfCap.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te) {
        return new MTEFluidInputHatch(pos, te);
    }
    
    @Override
    public @Nullable ResourceLocation getItemModel() {
        return MODEL;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    protected Supplier<BakedModel[]> getModels() {
        return () -> new BakedModel[]{getModel(MODEL)};
    }
    
    
    
    @Override
    public void writeInitData(CompoundTag tag) {
        super.writeInitData(tag);
        tag.putInt("inputSteam", lastIn);
    }
    
    @Override
    public void readInitData(CompoundTag tag) {
        super.readInitData(tag);
        lastIn = tag.getInt("inputSteam");
    }
    
    @Override
    public FluidStack inputFluid() {
        return fluidHandler.get().getFluidInTank(0);
    }
    
    @Override
    public void setInput(FluidStack fluidStack) {
        if(fluidHandler.get() instanceof FluidTank tank){
            tank.setFluid(fluidStack);
        }
        
    }
}
