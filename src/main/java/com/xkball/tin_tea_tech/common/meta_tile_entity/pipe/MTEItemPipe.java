package com.xkball.tin_tea_tech.common.meta_tile_entity.pipe;

import com.xkball.tin_tea_tech.api.annotation.*;
import com.xkball.tin_tea_tech.api.capability.item.TTItemHandler;
import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.capability.item.TTCommonItemHandler;
import com.xkball.tin_tea_tech.client.render.PipeRender;
import com.xkball.tin_tea_tech.common.blocks.te.PipeMTEBlock;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import com.xkball.tin_tea_tech.utils.ItemUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
@AutomaticRegistration
@AutomaticRegistration.MTE(block = PipeMTEBlock.class,renderer = "com.xkball.tin_tea_tech.client.render.PipeRender")
@Model(resources = {"tin_tea_tech:block/pipe_default"})
@I18N(chinese = "物品管道",english = "Item Pipe")
@Tag.Item({"pipe"})
public class MTEItemPipe extends MTEPipe{
    
    
    protected Connections lastInput = null;
    public MTEItemPipe(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
    }
    
    @Override
    public int defaultColor() {
        return ColorUtils.getColor(52,131,244,255);
    }
    
    @Override
    public void tick() {
        super.tick();
        if(getOffsetTick()%5==0 && !itemHandler.get().isEmpty()){
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            for(var c : Connections.values()){
                //blocked=不能输出
                if(isConnected(c) && !isBlocked(c)){
                    if(c == lastInput) continue;
                    var pos = getPos(c);
                    var te = getLevel().getBlockEntity(pos);
                    if(te != null){
                        var cap = te.getCapability(ForgeCapabilities.ITEM_HANDLER,c.nullableToDirection());
                        cap.ifPresent((facing) -> {
                            var b = ItemUtils.transportItem(itemHandler.get(),facing,1);
                            atomicBoolean.set(b);
                        });
                        if(atomicBoolean.get()){
                            if(te instanceof TTTileEntityBase){
                                var mte = ((TTTileEntityBase) te).getMte();
                                if(mte instanceof MTEItemPipe pipe){
                                    pipe.lastInput = c;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            lastInput = null;
        }
    }
    
    @Override
    public MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te) {
        return new MTEItemPipe(pos,te);
    }
    
    @Override
    protected Supplier<TTItemHandler> getItemHandlerSupplier() {
        return () -> new TTCommonItemHandler(1){
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }
        };
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER && (side == null || isConnected(Connections.fromDirection(side)))){
            return itemHandlerCap.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public Class<? extends BlockEntityRenderer<?>> getRendererClass() {
        return PipeRender.class;
    }
}
