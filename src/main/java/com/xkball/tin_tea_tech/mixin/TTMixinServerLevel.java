package com.xkball.tin_tea_tech.mixin;

import com.mojang.logging.LogUtils;
import com.xkball.tin_tea_tech.api.data.LatitudeAndLongitudeAccess;
import com.xkball.tin_tea_tech.client.shape.Point2D;
import com.xkball.tin_tea_tech.common.saveddata.LatitudeAndLongitude;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
@Mixin(ServerLevel.class)
public abstract class TTMixinServerLevel extends Level implements LatitudeAndLongitudeAccess {
    protected TTMixinServerLevel(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
    }
    
    @Shadow public abstract DimensionDataStorage getDataStorage();
    
    @Shadow public abstract long getSeed();
    
    @Unique
    private LatitudeAndLongitude tin_tea_tech$latitudeAndLongitude;
    @Inject(method = "<init>",at = @At("RETURN"))
    public void onInit(MinecraftServer pServer, Executor pDispatcher, LevelStorageSource.LevelStorageAccess pLevelStorageAccess, ServerLevelData pServerLevelData, ResourceKey pDimension, LevelStem pLevelStem, ChunkProgressListener pProgressListener, boolean pIsDebug, long pBiomeZoomSeed, List pCustomSpawners, boolean pTickTime, RandomSequences pRandomSequences, CallbackInfo ci){
        tin_tea_tech$latitudeAndLongitude = getDataStorage().computeIfAbsent(
                LatitudeAndLongitude::load,
                () -> LatitudeAndLongitude.create(this.getSeed()),
                "tin_tea_tech_latitude_and_longitude"
                );
        
    }
    
    @Override
    public Point2D tin_tea_tech$getLatitudeAndLongitude(Player player) {
        return tin_tea_tech$latitudeAndLongitude.getLatitudeAndLongitude(player.getOnPos());
    }
    
    @Override
    public void tin_tea_tech$reInitLatitudeAndLongitude(CompoundTag tag) {
        if(FMLEnvironment.production){
            LogUtils.getLogger().warn("You cannot reset your latitude and longitude after create a level.");
        }
        else {
            throw new UnsupportedOperationException("You cannot reset your latitude and longitude after create a level.");
        }
    }
    
    @Override
    public LatitudeAndLongitude tin_tea_tech$getLALSelf() {
        return tin_tea_tech$latitudeAndLongitude;
    }
}
