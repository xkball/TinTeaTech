package com.xkball.tin_tea_tech.common.item_behaviour;

import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.item.IHoloGlassPlugin;
import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AutomaticRegistration
@AutomaticRegistration.Item
@CreativeTag(tab = TTCreativeTab.TTMiscTab.class)
@I18N(chinese = "电磁弹射器",english = "ElectromagneticCatapult")
@Model(resources = {"tin_tea_tech:item/electromagnetic_catapult"})
public class ElectromagneticCatapultBehaviour implements IItemBehaviour, IHoloGlassPlugin {
    
    @Override
    public int stackSizeLimit() {
        return 1;
    }
    
    @Override
    public int mode() {
        return 7;
    }
    
    @Override
    public Component buttonText() {
        return Component.empty();
    }
    
    @Override
    public void tooltip(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        IItemBehaviour.super.tooltip(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("tooltip.tin_tea_tech.warn.ec").withStyle(ChatFormatting.RED));
    }
}
