package com.xkball.tin_tea_tech.common.item_behaviour;

import com.xkball.tin_tea_tech.api.annotation.*;
import com.xkball.tin_tea_tech.api.data.DataProvider;
import com.xkball.tin_tea_tech.api.item.IHoloGlassPlugin;
import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nullable;
import java.util.Collection;

@AutomaticRegistration
@AutomaticRegistration.Item
@CreativeTag(tab = TTCreativeTab.TTMiscTab.class)
@I18N(chinese = "三录仪",english = "Tricorder")
@Model(resources = {"tin_tea_tech:item/tricoder"})
@Tag.Item({"tool","pipe"})
public class TricorderBehaviour implements IItemBehaviour, IHoloGlassPlugin {
    
    @Override
    public int stackSizeLimit() {
        return 1;
    }
    
    @Override
    public InteractionResult useOnBlock(UseOnContext pContext) {
        if(!pContext.getLevel().isClientSide && pContext.getPlayer() != null ) {
            var pos = pContext.getClickedPos();
            var info = scan(pContext.getLevel(),pos, pContext.getPlayer());
            info.forEach((c) -> pContext.getPlayer().sendSystemMessage(c));
        }
        return InteractionResult.SUCCESS;
        
    }
    
    public static Collection<Component> scan(Level level, BlockPos pos,@Nullable Player scanner){
        var state = level.getBlockState(pos);
        var te = level.getBlockEntity(pos);
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
                if(scanner != null){
                    mte.addTester(scanner);
                }
                
            }
        }
        return info;
    }
    
    @Override
    public int mode() {
        return 1;
    }
    
    @Override
    public Component buttonText() {
        return Component.literal("");
    }
}
