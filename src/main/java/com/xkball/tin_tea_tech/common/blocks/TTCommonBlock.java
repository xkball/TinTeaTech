package com.xkball.tin_tea_tech.common.blocks;

import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class TTCommonBlock extends Block {
    public TTCommonBlock() {
        super(BlockBehaviour.Properties.of());
    }
    
    public TTCommonBlock(Properties properties){
        super(properties);
    }
    
    @AutomaticRegistration
    @AutomaticRegistration.Block
    @CreativeTag
    @I18N(chinese = "铜框架",english = "CopperFrame")
    public static class CopperFrame extends TTCommonBlock{
        public CopperFrame(){
            super(BlockBehaviour.Properties.of().noOcclusion());
        }
    }
    
    @AutomaticRegistration
    @AutomaticRegistration.Block
    @CreativeTag
    @I18N(chinese = "铜砖块",english = "CopperWallBlock")
    public static class CopperWall extends TTCommonBlock{}
    @AutomaticRegistration
    @AutomaticRegistration.Block
    @CreativeTag
    @I18N(chinese = "铜线圈",english = "CopperCoil")
    public static class CopperCoil extends TTCommonBlock{}
    
    @AutomaticRegistration
    @AutomaticRegistration.Block
    @CreativeTag
    @I18N(chinese = "高能机器外壳",english = "HighPowerCasing")
    public static class HighPowerCasing extends TTCommonBlock{
        
        
        @Override
        public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
            super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
            pTooltip.add(Component.translatable("tooltip.tin_tea_tech.from_tectech").withStyle(ChatFormatting.GRAY));
        }
    }
    
}
