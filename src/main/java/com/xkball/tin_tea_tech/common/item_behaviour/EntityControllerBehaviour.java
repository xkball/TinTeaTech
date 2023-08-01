package com.xkball.tin_tea_tech.common.item_behaviour;

import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;

@AutomaticRegistration
@AutomaticRegistration.Item
@CreativeTag(tab = TTCreativeTab.TTMiscTab.class)
@Model(resources = {"tin_tea_tech:item/entity_controller"})
@I18N(chinese = "实体方向盘",english = "Entity Controller")
public class EntityControllerBehaviour implements IItemBehaviour {
    @Override
    public int stackSizeLimit() {
        return 1;
    }
}
