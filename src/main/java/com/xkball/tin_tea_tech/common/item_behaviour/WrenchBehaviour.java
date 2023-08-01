package com.xkball.tin_tea_tech.common.item_behaviour;

import com.xkball.tin_tea_tech.api.annotation.*;
import com.xkball.tin_tea_tech.api.block.IRotatable;
import com.xkball.tin_tea_tech.api.facing.RelativeFacing;
import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.client.shape.Point3D;
import com.xkball.tin_tea_tech.mixin.TTMixinUseOnContext;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Rotation;

@AutomaticRegistration
@AutomaticRegistration.Item
@CreativeTag(tab = TTCreativeTab.TTMiscTab.class)
@I18N(chinese = "扳手",english = "Wrench")
@Model(resources = {"tin_tea_tech:item/wrench"})
@Tag.Item({"tool","pipe"})
public class WrenchBehaviour implements IItemBehaviour {
    @Override
    public int stackSizeLimit() {
        return 1;
    }
    
    @Override
    public InteractionResult useOnBlock(UseOnContext pContext) {
        var player = pContext.getPlayer();
        var level = pContext.getLevel();
        if(player != null && !level.isClientSide){
            var hitFacing = getHitFacing(pContext);
            var direction = pContext.getClickedFace();
            if(player.isShiftKeyDown()){
                var pos = pContext.getClickedPos();
                var mte = LevelUtils.getMTE(level,pos);
                if(mte != null){
                    var r =
                            mte.useByWrench(player,pContext.getHand(),((TTMixinUseOnContext)pContext).invokeGetHitResult());
                    if(r == InteractionResult.SUCCESS) return InteractionResult.SUCCESS;
                }
                
                var bs = level.getBlockState(pos);
                if(bs.getBlock() instanceof IRotatable r){
                    r.rotate(level,pos,bs,hitFacing.toDirection(direction));
                } else if (hitFacing == RelativeFacing.self) {
                    bs.rotate(level,pos,Rotation.CLOCKWISE_90);
                }
                var item = pContext.getItemInHand();
                var tag = item.getOrCreateTag();
                tag.putInt("x",pos.getX());
                tag.putInt("y",pos.getY());
                tag.putInt("z",pos.getZ());
                item.setTag(tag);
            }
//            else {
////                player.sendSystemMessage(Component.literal(hitFacing.toString()));
////                player.sendSystemMessage(Component.literal(hitFacing.toDirection(direction).toString()));
//            }
          }
        return InteractionResult.SUCCESS;
    }
    
    
    public static RelativeFacing getHitFacing(UseOnContext context){
        var pos = context.getClickedPos();
        var location = context.getClickLocation();
        var direction = context.getClickedFace();
        return new Point3D(location,pos).to2D(direction).toRelativeFacing();
    }
}
