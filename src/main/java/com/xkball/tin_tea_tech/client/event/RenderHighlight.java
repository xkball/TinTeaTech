package com.xkball.tin_tea_tech.client.event;

import com.xkball.tin_tea_tech.common.item_behaviour.TricorderBehaviour;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import com.xkball.tin_tea_tech.utils.RenderUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderHighlight {
    

    
    @SubscribeEvent
    public static void onRenderingHighlight(RenderHighlightEvent.Block event){
        var entity = event.getCamera().getEntity();
        if(entity instanceof Player player){
            //todo 改为用tag识别物品
            Item itemC = (Item) AutoRegManager.getRegistryObject(TricorderBehaviour.class).get();
        
            if(player.getItemInHand(InteractionHand.MAIN_HAND).is(itemC)
                || player.getItemInHand(InteractionHand.OFF_HAND).is(itemC)){
                var hitResult = event.getTarget();
                var pos = hitResult.getBlockPos();
                var direction = hitResult.getDirection();
                //var axis = direction.getAxis();
                
                var camera = event.getCamera();
                var bufferSource = event.getMultiBufferSource();
                var vertexConsumer = bufferSource.getBuffer(RenderType.LINES);
                var poseStack = event.getPoseStack();
                
                var cPos = camera.getPosition();
                var cx = cPos.x;
                var cy = cPos.y;
                var cz = cPos.z;
                
                cx = pos.getX() - cx;
                cy = pos.getY() - cy;
                cz = pos.getZ() - cz;
                
                RenderUtil.drawShape(vertexConsumer,poseStack,RenderUtil.biCrossMusk,direction,
                        cx,cy,cz,0f,0f,0f,0.4f);
//                switch (axis){
//
//                    case X -> {
//                        var x = direction == Direction.WEST ? 0.0 : 1.0;
//                        drawLine(vertexConsumer,poseStack,
//                                x,0.0,H1,x,1.0,H1,cx,cy,cz,0f,0f,0f,0.4f);
//                        drawLine(vertexConsumer,poseStack,
//                                x,0.0,H2,x,1.0,H2,cx,cy,cz,0f,0f,0f,0.4f);
//                        drawLine(vertexConsumer,poseStack,
//                                x,H1,0.0,x,H1,1.0,cx,cy,cz,0f,0f,0f,0.4f);
//                        drawLine(vertexConsumer,poseStack,
//                                x,H2,0.0,x,H2,1.0,cx,cy,cz,0f,0f,0f,0.4f);
//
//                    }
//                    case Y -> {
//                        var y = direction == Direction.DOWN ? 0.0 : 1.0;
//                        drawLine(vertexConsumer,poseStack,
//                                0.0,y,H1,1.0,y,H1,cx,cy,cz,0f,0f,0f,0.4f);
//                        drawLine(vertexConsumer,poseStack,
//                                0.0,y,H2,1.0,y,H2,cx,cy,cz,0f,0f,0f,0.4f);
//                        drawLine(vertexConsumer,poseStack,
//                                H1,y,0.0,H1,y,1.0,cx,cy,cz,0f,0f,0f,0.4f);
//                        drawLine(vertexConsumer,poseStack,
//                                H2,y,0.0,H2,y,1.0,cx,cy,cz,0f,0f,0f,0.4f);
//
//                    }
//                    case Z -> {
//                        var z = direction == Direction.NORTH ? 0.0 : 1.0;
//                        drawLine(vertexConsumer,poseStack,
//                                0.0,H1,z,1.0,H1,z,cx,cy,cz,0f,0f,0f,0.4f);
//                        drawLine(vertexConsumer,poseStack,
//                                0.0,H2,z,1.0,H2,z,cx,cy,cz,0f,0f,0f,0.4f);
//                        drawLine(vertexConsumer,poseStack,
//                                H1,0.0,z,H1,1.0,z,cx,cy,cz,0f,0f,0f,0.4f);
//                        drawLine(vertexConsumer,poseStack,
//                                H2,0.0,z,H2,1.0,z,cx,cy,cz,0f,0f,0f,0.4f);
//
//                    }
//                }
            }
        }
    }
}
