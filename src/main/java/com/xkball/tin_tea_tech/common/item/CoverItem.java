package com.xkball.tin_tea_tech.common.item;

import com.xkball.tin_tea_tech.common.cover.CoverFactory;
import com.xkball.tin_tea_tech.registration.TTRegistration;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CoverItem extends Item {

    protected final int type;
    public CoverItem(int type) {
        super(TTRegistration.getItemProperty());
        this.type = type;
    }
    
    
    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        var pos = pContext.getClickedPos();
        var level = pContext.getLevel();
        if(!level.isClientSide){
            var mte = LevelUtils.getMTE(level,pos);
            var item = pContext.getItemInHand();
            if(item.getItem() instanceof CoverItem self && mte != null){
                var direction = LevelUtils.getHitFacing(pContext).toDirection(pContext.getClickedFace());
                if(mte.getCoverHandler().addCover(direction,CoverFactory.getCover(mte, self.type,direction))){
                    item.shrink(1);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.FAIL;
            }
        }
        return super.useOn(pContext);
    }
}
