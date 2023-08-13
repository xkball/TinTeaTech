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
@Model(resources = {"tin_tea_tech:item/sun_glass"})
@I18N(chinese = "墨镜",english = "MoonGlass")
public class SunGlassBehaviour implements IItemBehaviour, IHoloGlassPlugin {
    @Override
    public int mode() {
        return 5;
    }
    
    @Override
    public Component buttonText() {
        return Component.empty();
    }
    
    @Override
    public int stackSizeLimit() {
        return 1;
    }
    
    @Override
    public void tooltip(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.tin_tea_tech.sun_glass").withStyle(ChatFormatting.GRAY));
    }
}
