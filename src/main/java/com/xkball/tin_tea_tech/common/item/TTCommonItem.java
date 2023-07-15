package com.xkball.tin_tea_tech.common.item;

import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.registration.TTRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TTCommonItem extends Item {
    final IItemBehaviour itemBehaviour;
    
    public TTCommonItem(IItemBehaviour iItemBehaviour) {
        super(TTRegistration.getItemProperty().stacksTo(iItemBehaviour.stackSizeLimit()));
        this.itemBehaviour = iItemBehaviour;
    }
    
//    @Override
//    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
//        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
//    }
//
    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        return itemBehaviour.useOnBlock(pContext);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return itemBehaviour.use(pLevel,pPlayer,pUsedHand);
    }
    
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        itemBehaviour.tooltip(pStack,pLevel,pTooltipComponents,pIsAdvanced);
    }
    
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return itemBehaviour.beforeUseOnBlock(stack,context);
    }
    
    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        return itemBehaviour.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }
}
