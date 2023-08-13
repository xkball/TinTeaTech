package com.xkball.tin_tea_tech.mixin.client;

import com.xkball.tin_tea_tech.common.player.PlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class TTMixinClientLevel extends Level {
    
    
    @Shadow @Final private Minecraft minecraft;
    
    protected TTMixinClientLevel(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
    }
    
    
    @Override
    public float getTimeOfDay(float pPartialTick) {
        return minecraft.level != null && PlayerData.get().modeAvailable(5) ? this.dimensionType().timeOfDay(20000) : super.getTimeOfDay(pPartialTick);
    }
}
