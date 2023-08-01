package com.xkball.tin_tea_tech.common.item_behaviour;

import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.item.IHoloGlassPlugin;
import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

@AutomaticRegistration
@AutomaticRegistration.Item
@CreativeTag(tab = TTCreativeTab.TTMiscTab.class)
@Model(resources = {"tin_tea_tech:item/icon"})
@I18N(chinese = "测试物品",english = "TestItem")
public class TestItemBehaviour implements IItemBehaviour, IHoloGlassPlugin {
    @Override
    public int mode() {
        return 1000;
    }
    
    @Override
    public Component buttonText() {
        return Component.literal("");
    }
    
//    @Override
//    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
//        if(!pLevel.isClientSide){
//            PlayerData.get(pPlayer).hgPluginMap.clear();
//            TTNetworkHandler.sentToClientPlayer(new SyncGUIDataPacket(PlayerData.get(pPlayer)),pPlayer);
//        }
//        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
//    }
    
    
    @Override
    public InteractionResult useOnBlock(UseOnContext pContext) {
        var te = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
        if(te != null){
            var i = te.getCapability(ForgeCapabilities.ITEM_HANDLER);
            i.ifPresent((ih) -> LevelUtils.dropItem(pContext.getPlayer(),pContext.getLevel(),pContext.getClickedPos(),ih));
            return InteractionResult.SUCCESS;
        }
        return IItemBehaviour.super.useOnBlock(pContext);
    }
}
