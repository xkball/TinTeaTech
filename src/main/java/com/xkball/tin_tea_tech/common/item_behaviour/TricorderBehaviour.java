package com.xkball.tin_tea_tech.common.item_behaviour;

import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.data.DataProvider;
import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

@AutomaticRegistration
@AutomaticRegistration.Item
@CreativeTag(tab = TTCreativeTab.TTMiscTab.class)
@I18N(chinese = "三录仪",english = "Tricorder")
@Model(resources = {"tin_tea_tech:item/tricoder"})
public class TricorderBehaviour implements IItemBehaviour {
    
    @Override
    public int stackSizeLimit() {
        return 1;
    }
    
    @Override
    public InteractionResult useOnBlock(UseOnContext pContext) {
        if(!pContext.getLevel().isClientSide && pContext.getPlayer() != null ) {
            var pos = pContext.getClickedPos();
            var state = pContext.getLevel().getBlockState(pos);
            var te = pContext.getLevel().getBlockEntity(pos);
            var info = DataProvider.blockInfo(state, pos);
            if(te != null){
                te.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(
                        (itemHandler) -> {
                            info.add(DataProvider.crossLine());
                            info.add(DataProvider.translatable("inventory").withStyle(ChatFormatting.BOLD));
                            for(int i=0;i<itemHandler.getSlots();i++){
                                info.add(DataProvider.translatable("shot")
                                        .append(Component.literal(i+" "))
                                        .append(DataProvider.translatable("item"))
                                        .append(Component.literal(itemHandler.getStackInSlot(i).toString())));
                            }
                        }
                );
            }
            if (te instanceof TTTileEntityBase) {
                var mte = ((TTTileEntityBase) te).getMte();
                if(mte instanceof DataProvider){
                    info.add(DataProvider.crossLine());
                    info.add(DataProvider.translatable("mte_info").withStyle(ChatFormatting.BOLD));
                    info.addAll(((DataProvider)mte).getInfo());
                    mte.addTester(pContext.getPlayer());
                }
            }
            info.forEach((c) -> pContext.getPlayer().sendSystemMessage(c));
            
        }
        
        return InteractionResult.SUCCESS;
        
    }
}
