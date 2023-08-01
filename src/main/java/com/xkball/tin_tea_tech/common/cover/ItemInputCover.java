package com.xkball.tin_tea_tech.common.cover;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.annotation.*;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.ItemUtils;
import com.xkball.tin_tea_tech.utils.TTUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.ParametersAreNonnullByDefault;

@AutomaticRegistration
@AutomaticRegistration.Cover(type = 1)
@I18N(chinese = "物品输入覆盖版",english = "Item Input Cover")
@CreativeTag(tab = TTCreativeTab.TTMiscTab.class)
@Model(resources = {"tin_tea_tech:item/item_input_cover"})
@Tag.Item("cover")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemInputCover extends CoverImpl{
    
    public ItemInputCover(MetaTileEntity mte, Direction direction) {
        super(mte, direction);
    }
    
    @Override
    public void tick() {
        if(getMTE().getOffsetTick()%20==0){
            
            var be = getMTE().getLevel().getBlockEntity(getMTE().getPos().relative(getDirection()));
            if(be != null){
                var itemHandlerSelf = getMTE().getCapability(ForgeCapabilities.ITEM_HANDLER,getDirection());
                var itemHandlerFacing = be.getCapability(ForgeCapabilities.ITEM_HANDLER,getDirection().getOpposite());
                if(itemHandlerFacing.isPresent() && itemHandlerSelf.isPresent()){
                    var self = itemHandlerSelf.orElseThrow(TTUtils.exceptionSupplier);
                    var facing = itemHandlerFacing.orElseThrow(TTUtils.exceptionSupplier);
                    
                    ItemUtils.transportItem(facing,self,1);
                }
            }
        }
    }
    
    @Override
    public int type() {
        return 1;
    }
    
    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
    
    }
}
