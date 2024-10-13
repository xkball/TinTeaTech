package com.xkball.tin_tea_tech.common.block;

import com.xkball.tin_tea_tech.api.block.te.TTTileEntityBase;
import com.xkball.tin_tea_tech.api.reg.RegBlock;
import com.xkball.tin_tea_tech.api.reg.RegBlockEntity;
import com.xkball.tin_tea_tech.common.item.blockitem.ScaffoldingBlockItem;
import com.xkball.tin_tea_tech.registry.TinTeaTechRegistries;

public class TinTeaTechBlocks {
    
    public static final RegBlockEntity<TTTileEntityBase> TILE_ENTITY_BASE = new RegBlockEntity<>("tin_tea_tech_tile_entity", TTTileEntityBase::new);
    
    public static final RegBlock<ScaffoldingBlock> SCAFFOLDING_BLOCK = new RegBlock<>(ScaffoldingBlock.NAME,ScaffoldingBlock::new)
            .setBlockItem(ScaffoldingBlockItem::new)
            .setI18n("Scaffolding Block","脚手架")
            .setCreativeTab(TinTeaTechRegistries.BLOCK_TAB);
    
    
    //仅用于触发类加载
    public static void init(){}
}
