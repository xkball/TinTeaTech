package com.xkball.tin_tea_tech.common.meta_tile_entity.hatch;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.capability.multiblock.IEnergyOutput;
import com.xkball.tin_tea_tech.capability.TTCapability;
import com.xkball.tin_tea_tech.capability.energy.TTEnergyStorage;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.ItemUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
@AutomaticRegistration(needDataGenModel = true,needSpecialName = true,name = "fe_output_hatch")
@AutomaticRegistration.MTE
@Model(resources = {"tin_tea_tech:block/fe_output_hatch"})
@I18N(chinese = "FE输出仓",english = "FE Output Hatch")
public class MTEFEOutputHatch extends MetaTileEntity implements IEnergyOutput {
    
    private static final ResourceLocation MODEL = TinTeaTech.ttResource("block/fe_output_hatch");
    protected int outPT= 5000;
    
    protected final LazyOptional<IEnergyOutput> selfCap;
    
    public MTEFEOutputHatch(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
        selfCap = LazyOptional.of(() -> this);
    }
    
    @Override
    public MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te) {
        return new MTEFEOutputHatch(pos,te);
    }
    
    @Override
    protected Supplier<IEnergyStorage> getEnergyHandlerSupplier() {
        return () -> new TTEnergyStorage(50000);
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
    public void tick() {
        super.tick();
        this.feEnergyHandler.get().receiveEnergy(outPT,false);
        if(getOffsetTick()%10==0){
            for(var d: Direction.values()){
                if(feEnergyHandler.get().getEnergyStored() == 0) return;
                var te = getLevel().getBlockEntity(getPos().relative(d));
                if(te != null){
                    var feh = te.getCapability(ForgeCapabilities.ENERGY,d.getOpposite());
                    feh.ifPresent((eh) -> ItemUtils.transportFE(feEnergyHandler.get(),eh));
                }
            }
        }
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == TTCapability.MULTI_BLOCK_FE_OUT){
            return selfCap.cast();
        }
        return super.getCapability(cap, side);
    }
    
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
        tag.putInt("outputFE",outPT);
    }
    
    @Override
    public void readInitData(CompoundTag tag) {
        super.readInitData(tag);
        outPT = tag.getInt("outputFE");
    }
}
