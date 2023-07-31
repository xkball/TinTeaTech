package com.xkball.tin_tea_tech.common.item.itemblock;

import com.xkball.tin_tea_tech.api.mte.ColorGetter;
import com.xkball.tin_tea_tech.common.blocks.te.TTTileEntityBlock;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

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
