package com.xkball.tin_tea_tech.capability;

import com.xkball.tin_tea_tech.api.capability.heat.IHeatSource;
import com.xkball.tin_tea_tech.api.capability.multiblock.IEnergyOutput;
import com.xkball.tin_tea_tech.api.capability.multiblock.IFluidInput;
import com.xkball.tin_tea_tech.api.capability.multiblock.ISteamInput;
import com.xkball.tin_tea_tech.api.capability.multiblock.ISteamOutput;
import com.xkball.tin_tea_tech.api.capability.steam.ISteamHolder;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class TTCapability {
    public static final Capability<IHeatSource> HEAT = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IEnergyOutput> MULTI_BLOCK_FE_OUT = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ISteamOutput> MULTI_BLOCK_STEAM_OUT = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ISteamInput> MULTI_BLOCK_STEAM_IN = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IFluidInput> MULTI_BLOCK_FLUID_IN = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ISteamHolder> STEAM = CapabilityManager.get(new CapabilityToken<>(){});
}
