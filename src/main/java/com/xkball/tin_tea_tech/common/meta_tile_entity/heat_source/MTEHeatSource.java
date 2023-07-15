package com.xkball.tin_tea_tech.common.meta_tile_entity.heat_source;

import com.xkball.tin_tea_tech.api.heat.IHeatSource;
import com.xkball.tin_tea_tech.capability.heat.HeatHandler;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MTEHeatSource extends MetaTileEntity {
    
    protected int timeLeft = 0;
    protected int heatProducedPT = 0;
    
    @OnlyIn(Dist.CLIENT)
    protected BakedModel litModel;
    @OnlyIn(Dist.CLIENT)
    protected BakedModel offModel;
    
    protected final IHeatSource heatSource = new HeatHandler();
    
    public MTEHeatSource(@Nonnull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
        if(isClient() && te != null){
            litModel = getModel(getLitModel());
            offModel = getModel(getOffModel());
        }
    }
    
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public BakedModel[] needToRender() {
        return new BakedModel[]{timeLeft > 0 && enabled ? litModel : offModel};
    }
    
    abstract ResourceLocation getLitModel();
    
    abstract ResourceLocation getOffModel();
    
    //5T进行一次运算
    @Override
    public void tick() {
        if(this.getOffsetTick() % 5 == 0){
            if(timeLeft > 0) {
                timeLeft--;
            }
            var b = !getLevel().getBlockState(getPos().above()).isAir();
            var dec = b ? 50 : 100;
            dec = heatSource.heatValue() <= 0 ? 0 : dec+(heatSource.heatValue());
            var inc = timeLeft>0 && enabled ? heatProducedPT*5 : 0;
            heatSource.decrease(dec - inc);
            markDirty();
        }
    }
    
    @Override
    public void clientTick() {
        if(enabled && this.getOffsetTick() % 5 == 0) {
            if (timeLeft > 0) {
                timeLeft--;
            }
        }
    }
    
    @Override
    public void writeInitData(CompoundTag byteBuf) {
        super.writeInitData(byteBuf);
        byteBuf.putInt("tl",timeLeft);
        byteBuf.putInt("hpt",heatProducedPT);
        byteBuf.put("hs",heatSource.serializeNBT());
//        byteBuf.writeInt(timeLeft);
//        byteBuf.writeInt(heatProducedPT);
//        DataUtils.writeTag(byteBuf,heatSource.serializeNBT());
//        DataUtils.writeTag(byteBuf,itemHandler.get().serializeNBT());
    }
    
    @Override
    public void readInitData(CompoundTag byteBuf) {
        super.readInitData(byteBuf);
        timeLeft = byteBuf.getInt("tl");
        heatProducedPT = byteBuf.getInt("hpt");
        heatSource.deserializeNBT(byteBuf.getCompound("hs"));
//        timeLeft = byteBuf.readInt();
//        heatProducedPT = byteBuf.readInt();
//        heatSource.deserializeNBT(DataUtils.readTag(byteBuf));
//        itemHandler.get().deserializeNBT(DataUtils.readTag(byteBuf));
    }
    
    
}
