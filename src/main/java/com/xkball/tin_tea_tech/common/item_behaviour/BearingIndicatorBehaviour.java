package com.xkball.tin_tea_tech.common.item_behaviour;

import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.item.IHoloGlassPlugin;
import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import net.minecraft.network.chat.Component;

@AutomaticRegistration
@AutomaticRegistration.Item
@CreativeTag(tab = TTCreativeTab.TTMiscTab.class)
@Model(resources = {"tin_tea_tech:item/bearing_indicator"})
@I18N(chinese = "方位指示器",english = "BearingIndicator")
public class BearingIndicatorBehaviour implements IItemBehaviour, IHoloGlassPlugin {
    @Override
    public int stackSizeLimit() {
        return 1;
    }
    
    @Override
    public int mode() {
        return 2;
    }
    
    @Override
    public Component buttonText() {
        return Component.literal("");
    }
}
