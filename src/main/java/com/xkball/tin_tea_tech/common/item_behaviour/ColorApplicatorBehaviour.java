package com.xkball.tin_tea_tech.common.item_behaviour;

import com.xkball.tin_tea_tech.api.annotation.*;
import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

@AutomaticRegistration
@AutomaticRegistration.Item
@CreativeTag(tab = TTCreativeTab.TTMiscTab.class)
@I18N(chinese = "染色器",english = "ColorApplicator")
//@Model(resources = {"tin_tea_tech:item/color_aooli"})
public class ColorApplicatorBehaviour implements IItemBehaviour {
    @Override
    public int stackSizeLimit() {
        return 1;
    }
    
    @Override
    public InteractionResult useOnBlock(UseOnContext pContext) {
        if(!pContext.getLevel().isClientSide){
            var item = pContext.getItemInHand();
            var pos= pContext.getClickedPos();
            var color = ColorUtils.getColor(pos.getX()%255,pos.getY()%255,pos.getZ()%255,255);
            var tag = item.getOrCreateTag();
            tag.putInt("color",color);
            item.setTag(tag);
        }
        return IItemBehaviour.super.useOnBlock(pContext);
    }
}
