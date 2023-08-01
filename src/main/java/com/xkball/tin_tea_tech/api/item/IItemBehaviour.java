package com.xkball.tin_tea_tech.api.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IItemBehaviour {
    
    default int stackSizeLimit(){
        return 64;
    }

    default InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand){
        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }
    default InteractionResult useOnBlock(UseOnContext pContext){
        return InteractionResult.PASS;
    }
    default InteractionResult beforeUseOnBlock(ItemStack stack, UseOnContext context){
        return InteractionResult.PASS;
    }
    default void tooltip(ItemStack pStack, @Nullable Level pLevel,
                         List<Component> pTooltipComponents, TooltipFlag pIsAdvanced){
        if(this instanceof IHoloGlassPlugin){
            pTooltipComponents.add(Component.translatable("tooltip.tin_tea_tech.holo_glass_plugin").withStyle(ChatFormatting.GRAY));
        }
    }
    
    default  InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand){
        return InteractionResult.PASS;
    }
    
}
