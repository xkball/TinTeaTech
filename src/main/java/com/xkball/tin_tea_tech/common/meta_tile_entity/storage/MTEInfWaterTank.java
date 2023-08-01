package com.xkball.tin_tea_tech.common.meta_tile_entity.storage;

import com.xkball.tin_tea_tech.api.TTValue;
import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import io.netty.buffer.ByteBuf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
@AutomaticRegistration
@AutomaticRegistration.MTE
@Model(resources = {"tin_tea_tech:block/fluid_tank"})
@I18N(chinese = "无限水储罐",english = "InfinityWaterTank")
public class MTEInfWaterTank extends MTEFluidTank{
    public MTEInfWaterTank(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
        this.filled = 63000;
        this.fluidName = Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(Fluids.WATER)).toString();
    }
    
    @Override
    protected Supplier<IFluidHandler> getFluidHandlerSupplier() {
        return () -> new FluidTank(64000){
            @Override
            public @NotNull FluidStack getFluid() {
                return new FluidStack(Fluids.WATER,100000000);
            }
            
            @Override
            public int getFluidAmount() {
                return 100000000;
            }
            
            @Override
            public int fill(FluidStack resource, FluidAction action) {
                return 0;
            }
            
            @Override
            public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
                return resource;
            }
            
            @Override
            public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
                return new FluidStack(Fluids.WATER,maxDrain);
            }
            
            @Override
            public void setFluid(FluidStack stack) {
                super.setFluid(stack);
            }
        };
    }
    
    @Override
    public void sentCustomData(int id, Consumer<ByteBuf> bufConsumer) {
        if(id == TTValue.FLUID || id == TTValue.DATA_UPDATE) return;
        super.sentCustomData(id, bufConsumer);
    }
    
    @Override
    public MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te) {
        return new MTEInfWaterTank(pos,te);
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
