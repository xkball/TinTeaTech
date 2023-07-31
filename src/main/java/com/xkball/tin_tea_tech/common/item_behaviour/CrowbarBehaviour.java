package com.xkball.tin_tea_tech.common.item_behaviour;

import com.xkball.tin_tea_tech.api.annotation.*;
import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

@AutomaticRegistration
@AutomaticRegistration.Item
@CreativeTag(tab = TTCreativeTab.TTMiscTab.class)
@Model(resources = {"tin_tea_tech:item/crowbar"})
@I18N(chinese = "撬棍",english = "Crowbar")
@Tag.Item("cover")
public class CrowbarBehaviour implements IItemBehaviour {
    
    @Override
    public int stackSizeLimit() {
        return 1;
    }
    
    @Override
    public InteractionResult useOnBlock(UseOnContext pContext) {
        var pos = pContext.getClickedPos();
        var level = pContext.getLevel();
        var mte = LevelUtils.getMTE(level,pos);
        if(mte != null){
            var direction = LevelUtils.getHitFacing(pContext).toDirection(pContext.getClickedFace());
            var cover = mte.getCoverHandler().removeCover(direction);
            if(cover != null){
                LevelUtils.dropItem(pContext.getPlayer(),level,pos,cover.asItemStack());
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }
        return IItemBehaviour.super.useOnBlock(pContext);
    }
}
