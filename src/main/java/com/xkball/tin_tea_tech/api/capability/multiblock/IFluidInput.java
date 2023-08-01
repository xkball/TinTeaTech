package com.xkball.tin_tea_tech.api.capability.multiblock;

import net.minecraftforge.fluids.FluidStack;

public interface IFluidInput {
    FluidStack inputFluid();
    void setInput(FluidStack fluidStack);
}
