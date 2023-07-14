package com.xkball.tin_tea_tech.capability;

import com.xkball.tin_tea_tech.api.heat.IHeatSource;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class TTCapability {
    public static final Capability<IHeatSource> HEAT = CapabilityManager.get(new CapabilityToken<>() {});
}
