package com.xkball.tin_tea_tech.common.meta_tile_entity.hatch;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.capability.multiblock.ISteamOutput;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
@AutomaticRegistration(needDataGenModel = true)
@AutomaticRegistration.MTE
@Model(resources = {"tin_tea_tech:block/steam_output_hatch"})
@I18N(chinese = "蒸汽输出仓",english = "Steam Output Hatch")
public class MTESteamOutputHatch extends MetaTileEntity implements ISteamOutput {
    
    private static final ResourceLocation MODEL = TinTeaTech.ttResource("block/steam_output_hatch");
    protected int outPT= 10;
    
    protected final LazyOptional<ISteamOutput> selfCap;
    
    public MTESteamOutputHatch(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
        selfCap = LazyOptional.of(() -> this);
    }
    
    @Override
    public void tick() {
        super.tick();
        if(getOffsetTick()%10==0){
            for(var d: Direction.values()){
                if(outPT==0) return;
                var te = getLevel().getBlockEntity(getPos().relative(d));
                if(te != null){
                    var feh = te.getCapability(TTCapability.STEAM,d.getOpposite());
                    feh.ifPresent((sh) -> sh.add(outPT*10,false));
                }
            }
        }
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == TTCapability.MULTI_BLOCK_STEAM_OUT){
            return selfCap.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te) {
        return new MTESteamOutputHatch(pos, te);
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
    public void setOutput(int output){
        this.outPT = output;
    }
    
    @Override
    public int getOutput() {
        return outPT;
    }
    
    @Override
    public void writeInitData(CompoundTag tag) {
        super.writeInitData(tag);
        tag.putInt("outputSteam",outPT);
    }
    
    @Override
    public void readInitData(CompoundTag tag) {
        super.readInitData(tag);
        outPT = tag.getInt("outputSteam");
    }
}
