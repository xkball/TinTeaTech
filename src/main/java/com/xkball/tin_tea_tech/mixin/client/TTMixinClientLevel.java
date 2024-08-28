package com.xkball.tin_tea_tech.mixin.client;

import com.xkball.tin_tea_tech.api.astronomy.AstronomyAccess;
import com.xkball.tin_tea_tech.api.astronomy.LevelModel;
import com.xkball.tin_tea_tech.api.data.LatitudeAndLongitudeAccess;
import com.xkball.tin_tea_tech.client.shape.Point2D;
import com.xkball.tin_tea_tech.common.player.PlayerData;
import com.xkball.tin_tea_tech.common.saveddata.LatitudeAndLongitude;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import com.xkball.tin_tea_tech.network.packet.SyncLatitudeAndLongitudePacket;
import com.xkball.tin_tea_tech.utils.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;



@SuppressWarnings("rawtypes")
@Mixin(ClientLevel.class)
public abstract class TTMixinClientLevel extends Level implements LatitudeAndLongitudeAccess, AstronomyAccess {
    
    
    @Shadow @Final private Minecraft minecraft;
    
    @Shadow public abstract DimensionSpecialEffects effects();
    
    @Unique
    private LatitudeAndLongitude tin_tea_tech$latitudeAndLongitude = LatitudeAndLongitude.create(RandomSource.create().nextLong());
    
    @Unique
    private LevelModel tin_tea_tech$levelModel;
    
    @Inject(method = "<init>",at = @At("RETURN"))
    public void onInit(ClientPacketListener pConnection, ClientLevel.ClientLevelData pClientLevelData, ResourceKey pDimension, Holder pDimensionType, int pViewDistance, int pServerSimulationDistance, Supplier pProfiler, LevelRenderer pLevelRenderer, boolean pIsDebug, long pBiomeZoomSeed, CallbackInfo ci){
        tin_tea_tech$levelModel = new LevelModel(this.effects().skyType());
    }
    
    @Inject(method = "tickTime",at = @At("RETURN"))
    public void onTickTime(CallbackInfo ci){
        tin_tea_tech$levelModel.tick(this.getDayTime());
    }
    
    @Override
    public Point2D tin_tea_tech$getLatitudeAndLongitude(Player player) {
        return tin_tea_tech$latitudeAndLongitude.getLatitudeAndLongitude(player.getOnPos());
    }
    
    @Override
    public LevelModel tin_tea_tech$getLevelModel() {
        return tin_tea_tech$levelModel;
    }
    
    @Override
    public void tin_tea_tech$reInitLatitudeAndLongitude(CompoundTag tag) {
        tin_tea_tech$latitudeAndLongitude = LatitudeAndLongitude.load(tag);
    }
    
    @Override
    public LatitudeAndLongitude tin_tea_tech$getLALSelf() {
        return tin_tea_tech$latitudeAndLongitude;
    }
   
    protected TTMixinClientLevel(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
    }
    
    @Override
    public float getTimeOfDay(float pPartialTick) {
        if(minecraft.level == null) return super.getTimeOfDay(pPartialTick);
        var sun = tin_tea_tech$levelModel.getSunVec();
        var l = sun.length();
        return PlayerData.get().modeAvailable(5)
                ? 0.5f :
                PlayerData.get().modeAvailable(6) ? 0.999f :
                        //根据模拟模型计算的太阳高度修正
                        //根据client使用timeOfDay的分析发现这个值在日出日落时影响较大,对应0.95-0.05和0.45-0.55
                        //下面给出值在cos和clamp之后跟原来函数拟合应该较好
                        //大约在太阳高度小于二十三度变成动态的值
                        Math.abs(sun.y/l)>0.4f?
                                (sun.y>0?0.999f:0.5f):
                                0.25f+(float)(Math.acos(sun.y/l)-1.5707f)*0.5f;
    }
    
    @Redirect(method = "getStarBrightness",at = @At(value = "INVOKE",target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    public float onGetStarBrightness(float pValue, float pMin, float pMax){
        var sun = ClientUtils.getLevelModel().getSunVec();
        var l = sun.length();
        var sinY = sun.y/l;
        if(Math.abs(sinY)>0.4f){
            return sun.y>0?pMin:pMax;
        }
        else{
            return (float) Mth.clamp(1-Math.sin(Math.asin(sinY)+Math.toRadians(12)),pMin,pMax);
        }
    }
}
