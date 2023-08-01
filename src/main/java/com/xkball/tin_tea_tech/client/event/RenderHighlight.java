package com.xkball.tin_tea_tech.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xkball.tin_tea_tech.api.facing.FacingType;
import com.xkball.tin_tea_tech.api.facing.RelativeFacing;
import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.MTEPipe;
import com.xkball.tin_tea_tech.data.tag.TTItemTags;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import com.xkball.tin_tea_tech.utils.ItemUtils;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import com.xkball.tin_tea_tech.utils.RenderUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static com.xkball.tin_tea_tech.client.render.PipeRender.axis2Axis;
import static com.xkball.tin_tea_tech.client.render.PipeRender.md;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderHighlight {
    
    public static final TagKey<Item> tool = TTItemTags.tagMap.get("tool");
    public static final TagKey<Item> pipe = TTItemTags.tagMap.get("pipe");
    private static final int lineCover = ColorUtils.getColor(0,0,0, (int) (255*0.4f));
    @SubscribeEvent
    public static void onRenderingHighlight(RenderHighlightEvent.Block event){
        var entity = event.getCamera().getEntity();
        if(entity instanceof Player player){
//            //DONE 改为用tag识别物品
//            Item itemC = (Item) AutoRegManager.getRegistryObject(TricorderBehaviour.class).get();
            if(ItemUtils.hasTagInHand(player,TTItemTags.get("tool"),TTItemTags.get("pipe"),TTItemTags.get("cover"))){
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
                double finalCx = cx;
                double finalCy = cy;
                double finalCz = cz;
                LevelUtils.getMTEAndExecute(player.level(),pos,
                        (mte) -> {
                        var bs = mte.getBlockState();
                        if(mte.getFacingType(bs,direction) == FacingType.MainFace){
                            RenderUtil.drawShape(vertexConsumer,poseStack,RenderUtil.crossMusk,direction,
                                    finalCx, finalCy, finalCz,0f,0f,0f,0.4f);
                        }
                        for(var rc : RelativeFacing.coverFacing){
                            var d = RelativeFacing.toDirection(direction,rc);
                            if(mte.getCoverHandler().haveCover(d)){
                                RenderUtil.drawShape(vertexConsumer,poseStack,RelativeFacing.getMusk(rc), direction,
                                        finalCx,finalCy,finalCz,0f,0f,0f,0.4f);
                            }
                        }
                        });
            }
            else {
                var hitResult = event.getTarget();
                var pos = hitResult.getBlockPos();
                var p = LevelUtils.getMTE(player.level(),pos);
                if(p instanceof MTEPipe mte){
                    var camera = event.getCamera();
                    var bufferSource = event.getMultiBufferSource();
                    var buffer1 = bufferSource.getBuffer(RenderType.LINES);
                    var pPoseStack = event.getPoseStack();
                    
                    var light = 0;
                    var color = lineCover;
                    var b2 = mte.b2;
                    var b3 = mte.b3;
                    var b4 = mte.b4;
                    
                    var cPos = camera.getPosition();
                    var cx = cPos.x;
                    var cy = cPos.y;
                    var cz = cPos.z;
                    
                    cx = pos.getX() - cx;
                    cy = pos.getY() - cy;
                    cz = pos.getZ() - cz;
                    if(b2){
                        //PipeRender.renderCenter(mte,pPoseStack,buffer1,color,light,0,0,1,1, Direction.Axis.X,false);
                        for(var vc:Connections.XRoundConnections){
                            if(mte.isConnected(vc)) renderCommonConnection(vc, Direction.Axis.X,pPoseStack,buffer1,color,light,0,0,1,1,false,cx,cy,cz);
                        }
                    }
                    if (b3) {
                        //PipeRender.renderCenter(mte,pPoseStack,buffer1,color,light,0,0,1,1,Direction.Axis.Y,false);
                        for(var vc:Connections.YRoundConnections){
                            if(mte.isConnected(vc)) renderCommonConnection(vc, Direction.Axis.Y,pPoseStack,buffer1,color,light,0,0,1,1,false,cx,cy,cz);
                        }
                    }
                    if (b4) {
                        //PipeRender.renderCenter(mte,pPoseStack,buffer1,color,light,0,0,1,1,Direction.Axis.Z,false);
                        for(var vc:Connections.ZRoundConnections){
                            if(mte.isConnected(vc)) renderCommonConnection(vc, Direction.Axis.Z,pPoseStack,buffer1,color,light,0,0,1,1,false,cx,cy,cz);
                        }
                    }
                }
                
            }
        }
        
    }
    
    //Todo Fix Normal
    public static void renderCommonConnection(Connections connection, Direction.Axis axis,PoseStack pPoseStack,VertexConsumer buffer,int color,int light,float u0,float v0,float u1,float v1,boolean needUV,double x,double y,double z){
        var direction = connection.toDirection();
        pPoseStack.pushPose();
        var pose = pPoseStack.last().pose();
        var normal = pPoseStack.last().normal();
        pPoseStack.translate(x,y,z);
        pPoseStack.rotateAround(axis2Axis(axis).rotationDegrees(45),0.5F,0.5F,0.5F);
        pPoseStack.translate(0.5F,0.5F,0.5F);
        pPoseStack.scale(0.25f,0.25f,0.25f);
        pPoseStack.translate(-0.5F,-0.5F,-0.5F);
        var t = direction.getNormal();
        var da = direction.getAxis();
        pPoseStack.translate(negativeMultiply(t.getX(),md),negativeMultiply(t.getY(),md),negativeMultiply(t.getZ(),md));
        pPoseStack.scale(da==Direction.Axis.X?md:1F,da==Direction.Axis.Y?md:1F,da==Direction.Axis.Z?md:1F);
        pPoseStack.scale(1.009F,1.009F,1.009F);
        if(Direction.SOUTH != direction.getOpposite()) renderFace(pose, normal, buffer, color,light, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH,axis,u0,v0,u1,v1,needUV);
        if(Direction.NORTH != direction.getOpposite()) renderFace(pose, normal, buffer, color,light, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH,axis,u0,v0,u1,v1,needUV);
        if(Direction.EAST != direction.getOpposite()) renderFace(pose, normal, buffer, color,light, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST,axis,u0,v0,u1,v1,needUV);
        if(Direction.WEST != direction.getOpposite()) renderFace(pose, normal, buffer, color,light, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST,axis,u0,v0,u1,v1,needUV);
        if(Direction.DOWN != direction.getOpposite()) renderFace(pose, normal, buffer, color,light, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN,axis,u0,v0,u1,v1,needUV);
        if(Direction.UP != direction.getOpposite()) renderFace(pose, normal, buffer, color,light, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP,axis,u0,v0,u1,v1,needUV);
        pPoseStack.popPose();
        
    }
    
    public static float negativeMultiply(float i,float m){
        return i<0?i*m:i;
    }
    
    public static void renderFace(Matrix4f pPose, Matrix3f fn, VertexConsumer bufferBuilder, int color, int light,
                                  float pX0, float pX1, float pY0, float pY1, float pZ0, float pZ1, float pZ2, float pZ3, Direction direction, Direction.Axis axis,
                                  float u0, float v0, float u1, float v1, boolean needUV){
        var normal = rotate45(axis,direction.getNormal());
        var nx = normal.x();
        var ny = normal.y();
        var nz = normal.z();
        if(needUV){
            bufferBuilder.vertex(pPose, pX0, pY0, pZ0).color(color).uv(u0,v0).uv2(light).normal(fn,nx,ny,nz).endVertex();
            bufferBuilder.vertex(pPose, pX1, pY0, pZ1).color(color).uv(u0,v1).uv2(light).normal(fn,nx,ny,nz).endVertex();
            bufferBuilder.vertex(pPose, pX1, pY1, pZ2).color(color).uv(u1,v1).uv2(light).normal(fn,nx,ny,nz).endVertex();
            bufferBuilder.vertex(pPose, pX0, pY1, pZ3).color(color).uv(u1,v0).uv2(light).normal(fn,nx,ny,nz).endVertex();
        }
        else {
            bufferBuilder.vertex(pPose, pX0, pY0, pZ0).color(color).normal(fn,nx,ny,nz).endVertex();
            bufferBuilder.vertex(pPose, pX1, pY0, pZ1).color(color).normal(fn,nx,ny,nz).endVertex();
            bufferBuilder.vertex(pPose, pX1, pY1, pZ2).color(color).normal(fn,nx,ny,nz).endVertex();
            bufferBuilder.vertex(pPose, pX0, pY1, pZ3).color(color).normal(fn,nx,ny,nz).endVertex();
            
        }
    }
    
    public static Vector3f rotate45(Direction.Axis axis, Vec3i vec3i){
        var vec3 = new Vector3f(vec3i.getX(),vec3i.getY(),vec3i.getZ());
        vec3.rotate(axis2Axis(axis).rotationDegrees(45));
        return vec3;
    }
}
