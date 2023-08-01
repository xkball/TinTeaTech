package com.xkball.tin_tea_tech.common.item.itemblock;

import com.xkball.tin_tea_tech.api.mte.ColorGetter;
import com.xkball.tin_tea_tech.common.blocks.te.TTTileEntityBlock;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class MTEItemBlock extends BlockItem {
    private final TTTileEntityBlock mteBlock;
    
    
    private final MetaTileEntity defaultMTE;
    private final int colorOverlay;
    public MTEItemBlock(TTTileEntityBlock pBlock, Properties pProperties) {
        super(pBlock, pProperties);
        this.mteBlock = pBlock;
        this.defaultMTE = mteBlock.getDefaultMTE();
        if(defaultMTE instanceof ColorGetter cg) {
            colorOverlay = cg.defaultColor();
        }
        else {
            colorOverlay = 0;
        }
    }
    
    @Nullable
    @Override
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext pContext) {
        return mteBlock.getDefaultMTE().updateBlockPlaceContext(pContext);
    }
    
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        mteBlock.getDefaultMTE().appendHoverText(pStack,pLevel,pTooltip,pFlag);
    }
    
    @Override
    public boolean isFoil(ItemStack pStack) {
        return getDefaultMTE().isFoil(pStack);
    }
    
    public TTTileEntityBlock getMteBlock() {
        return mteBlock;
    }
    
    public MetaTileEntity getDefaultMTE() {
        return defaultMTE;
    }
    
    public int defaultColor(){
        return colorOverlay;
    }
    
    
}
