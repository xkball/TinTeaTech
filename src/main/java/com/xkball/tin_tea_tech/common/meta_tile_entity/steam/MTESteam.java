package com.xkball.tin_tea_tech.common.meta_tile_entity.steam;

import com.xkball.tin_tea_tech.api.capability.steam.ISteamHolder;
import com.xkball.tin_tea_tech.capability.TTCapability;
import com.xkball.tin_tea_tech.capability.steam.SteamHolder;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.utils.FinalObj;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class MTESteam extends MetaTileEntity {
    
    protected final FinalObj<ISteamHolder> steamHandler = new FinalObj<>();
    protected final LazyOptional<ISteamHolder> steamHandlerCap;
    
    public MTESteam(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
        steamHandlerCap = LazyOptional.of(steamHandler::get);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == TTCapability.STEAM){
            return steamHandlerCap.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void createInventory() {
        super.createInventory();
        steamHandler.set(getSteamHandlerSupplier().get());
    }
    
    @Override
    public void writeInitData(CompoundTag tag) {
        super.writeInitData(tag);
        tag.put("steamHandler",steamHandler.get().serializeNBT());
    }
    
    @Override
    public void readInitData(CompoundTag tag) {
        super.readInitData(tag);
        steamHandler.get().deserializeNBT(tag.getCompound("steamHandler"));
    }
    
    public Supplier<ISteamHolder> getSteamHandlerSupplier(){
        return () -> new SteamHolder(10);
    }
    
}
