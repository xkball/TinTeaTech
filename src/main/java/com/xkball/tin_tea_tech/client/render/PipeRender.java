package com.xkball.tin_tea_tech.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.common.meta_tile_entity.pipe.MTEPipe;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PipeRender implements BlockEntityRenderer<TTTileEntityBase> {
    
    public static final int testColor = ColorUtils.getColor(255,185,55,255);
    private static final ResourceLocation texture = TinTeaTech.ttResource("block/pipe_overlay");
    
    private static final float md = 2.3284271F;
    @Override
    public void render(TTTileEntityBase pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if(pBlockEntity.getMte() instanceof MTEPipe mte){
            var level = pBlockEntity.getLevel();
            if(level != null) {
                var light = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, mte.getPos()), level.getBrightness(LightLayer.SKY, mte.getPos()));
                light = (light & 0xFFFF) | (((light >> 16) & 0xFFFF) << 16);
                //绘制底色
                var buffer1 = pBuffer.getBuffer(RenderType.textBackground());
                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                RenderSystem.disableBlend();
                var b0 = !mte.haveConnection();
                var b1 = mte.haveConnection(Connections.verticalConnections);
                var b2 = mte.haveConnection(Connections.XRoundConnections);
                var b3 = mte.haveConnection(Connections.YRoundConnections);
                var b4 = mte.haveConnection(Connections.ZRoundConnections);
                if(b1 || b0 )
                    renderCenter(mte,pPoseStack,buffer1,light,0,0,1,1,null);
                if(b1){
                    for(var vc:Connections.verticalConnections){
                        if(mte.isConnected(vc)) renderVerticalConnection(vc,pPoseStack,buffer1,light,0,0,1,1);
                    }
                }
                if(b2){
                    renderCenter(mte,pPoseStack,buffer1,light,0,0,1,1,Direction.Axis.X);
                    for(var vc:Connections.XRoundConnections){
                        if(mte.isConnected(vc)) renderCommonConnection(vc, Direction.Axis.X,pPoseStack,buffer1,light,0,0,1,1);
                    }
                }
                if (b3) {
                    renderCenter(mte,pPoseStack,buffer1,light,0,0,1,1,Direction.Axis.Y);
                    for(var vc:Connections.YRoundConnections){
                        if(mte.isConnected(vc)) renderCommonConnection(vc, Direction.Axis.Y,pPoseStack,buffer1,light,0,0,1,1);
                    }
                }
                if (b4) {
                    renderCenter(mte,pPoseStack,buffer1,light,0,0,1,1,Direction.Axis.Z);
                    for(var vc:Connections.ZRoundConnections){
                        if(mte.isConnected(vc)) renderCommonConnection(vc, Direction.Axis.Z,pPoseStack,buffer1,light,0,0,1,1);
                    }
                }
                
                RenderSystem.enableBlend();
                //绘制贴图
                @SuppressWarnings("deprecation")
                var t = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
                RenderSystem.setShaderTexture(0, texture);
                var buffer2 = pBuffer.getBuffer(RenderType.cutout());
                if(b1 || b0 )
                    renderCenter(mte,pPoseStack,buffer2,light,t.getU0(),t.getV0(),t.getU1(),t.getV1(),null);
                if(b1){
                    for(var vc:Connections.verticalConnections){
                        if(mte.isConnected(vc)) renderVerticalConnection(vc,pPoseStack,buffer1,light,t.getU0(),t.getV0(),t.getU1(),t.getV1());
                    }
                }
                if(b2){
                    renderCenter(mte,pPoseStack,buffer2,light,t.getU0(),t.getV0(),t.getU1(),t.getV1(), Direction.Axis.X);
                    for(var vc:Connections.XRoundConnections){
                        if(mte.isConnected(vc)) renderCommonConnection(vc, Direction.Axis.X,pPoseStack,buffer1,light,t.getU0(),t.getV0(),t.getU1(),t.getV1());
                    }
                }
                if(b3){
                    renderCenter(mte,pPoseStack,buffer2,light,t.getU0(),t.getV0(),t.getU1(),t.getV1(), Direction.Axis.Y);
                    for(var vc:Connections.YRoundConnections){
                        if(mte.isConnected(vc)) renderCommonConnection(vc, Direction.Axis.Y,pPoseStack,buffer1,light,t.getU0(),t.getV0(),t.getU1(),t.getV1());
                    }
                }
                if(b4){
                    renderCenter(mte,pPoseStack,buffer2,light,t.getU0(),t.getV0(),t.getU1(),t.getV1(), Direction.Axis.Z);
                    for(var vc:Connections.ZRoundConnections){
                        if(mte.isConnected(vc)) renderCommonConnection(vc, Direction.Axis.Z,pPoseStack,buffer1,light,t.getU0(),t.getV0(),t.getU1(),t.getV1());
                    }
                }
               
            }
        }
    }
    
    public void renderCenter(MTEPipe mte, PoseStack pPoseStack, VertexConsumer buffer, int light, float u0, float v0, float u1, float v1,@Nullable Direction.Axis axis){
        pPoseStack.pushPose();
        var pose = pPoseStack.last().pose();
        var normal = pPoseStack.last().normal();
        pPoseStack.translate(0.5,0.5,0.5);
        pPoseStack.scale(0.25f,0.25f,0.25f);
        pPoseStack.translate(-0.5,-0.5,-0.5);
        pPoseStack.scale(1.02f,1.02f,1.02f);
        if(axis!=null){
            pPoseStack.scale(1.01F,1.01F,1.01F);
            pPoseStack.rotateAround(axis2Axis(axis).rotationDegrees(45),0.5F,0.5F,0.5F);
        }
        if(!mte.isConnected(Connections.SOUTH)) renderFace(pose, normal, buffer, testColor,light, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH,u0,v0,u1,v1);
        if(!mte.isConnected(Connections.NORTH)) renderFace(pose, normal, buffer, testColor,light, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH,u0,v0,u1,v1);
        if(!mte.isConnected(Connections.EAST)) renderFace(pose, normal, buffer, testColor,light, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST,u0,v0,u1,v1);
        if(!mte.isConnected(Connections.WEST)) renderFace(pose, normal, buffer, testColor,light, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST,u0,v0,u1,v1);
        if(!mte.isConnected(Connections.DOWN)) renderFace(pose, normal, buffer, testColor,light, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN,u0,v0,u1,v1);
        if(!mte.isConnected(Connections.UP)) renderFace(pose, normal, buffer, testColor,light, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP,u0,v0,u1,v1);
        pPoseStack.popPose();
    }
    
    public void renderCommonConnection(Connections connection, Direction.Axis axis,PoseStack pPoseStack,VertexConsumer buffer,int light,float u0,float v0,float u1,float v1){
        var direction = connection.toDirection();
        pPoseStack.pushPose();
        var pose = pPoseStack.last().pose();
        var normal = pPoseStack.last().normal();
        pPoseStack.rotateAround(axis2Axis(axis).rotationDegrees(45),0.5F,0.5F,0.5F);
        pPoseStack.translate(0.5F,0.5F,0.5F);
        pPoseStack.scale(0.25f,0.25f,0.25f);
        pPoseStack.translate(-0.5F,-0.5F,-0.5F);
        var t = direction.getNormal();
        var da = direction.getAxis();
        pPoseStack.translate(negativeMultiply(t.getX(),md),negativeMultiply(t.getY(),md),negativeMultiply(t.getZ(),md));
        pPoseStack.scale(da==Direction.Axis.X?md:1F,da==Direction.Axis.Y?md:1F,da==Direction.Axis.Z?md:1F);
        if(Direction.SOUTH != direction.getOpposite()) renderFace(pose, normal, buffer, testColor,light, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH,u0,v0,u1,v1);
        if(Direction.NORTH != direction.getOpposite()) renderFace(pose, normal, buffer, testColor,light, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH,u0,v0,u1,v1);
        if(Direction.EAST != direction.getOpposite()) renderFace(pose, normal, buffer, testColor,light, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST,u0,v0,u1,v1);
        if(Direction.WEST != direction.getOpposite()) renderFace(pose, normal, buffer, testColor,light, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST,u0,v0,u1,v1);
        if(Direction.DOWN != direction.getOpposite()) renderFace(pose, normal, buffer, testColor,light, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN,u0,v0,u1,v1);
        if(Direction.UP != direction.getOpposite()) renderFace(pose, normal, buffer, testColor,light, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP,u0,v0,u1,v1);
        pPoseStack.popPose();
        
    }
    
    public static Axis axis2Axis(Direction.Axis axis){
        return switch (axis){
            case X -> Axis.XP;
            case Y -> Axis.YP;
            case Z -> Axis.ZP;
        };
    }
    
    public void renderVerticalConnection(Connections connection,PoseStack pPoseStack,VertexConsumer buffer,int light,float u0,float v0,float u1,float v1){
        var direction = connection.toDirection();
        var axis = direction.getAxis();
        pPoseStack.pushPose();
        var pose = pPoseStack.last().pose();
        var normal = pPoseStack.last().normal();
        pPoseStack.translate(0.5F,0.5F,0.5F);
        pPoseStack.scale(0.25f,0.25f,0.25f);
        pPoseStack.translate(-0.5F,-0.5F,-0.5F);
        pPoseStack.scale(1.01f,1.01f,1.01f);
        var t = direction.getNormal();
        pPoseStack.translate(negativeMultiply(t.getX(),1.5F),negativeMultiply(t.getY(),1.5F),negativeMultiply(t.getZ(),1.5F));
        pPoseStack.scale(axis==Direction.Axis.X?1.5F:1F,axis==Direction.Axis.Y?1.5F:1F,axis==Direction.Axis.Z?1.5F:1F);
        if(Direction.SOUTH != direction.getOpposite()) renderFace(pose, normal, buffer, testColor,light, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH,u0,v0,u1,v1);
        if(Direction.NORTH != direction.getOpposite()) renderFace(pose, normal, buffer, testColor,light, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH,u0,v0,u1,v1);
        if(Direction.EAST != direction.getOpposite()) renderFace(pose, normal, buffer, testColor,light, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST,u0,v0,u1,v1);
        if(Direction.WEST != direction.getOpposite()) renderFace(pose, normal, buffer, testColor,light, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST,u0,v0,u1,v1);
        if(Direction.DOWN != direction.getOpposite()) renderFace(pose, normal, buffer, testColor,light, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN,u0,v0,u1,v1);
        if(Direction.UP != direction.getOpposite()) renderFace(pose, normal, buffer, testColor,light, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP,u0,v0,u1,v1);
        pPoseStack.popPose();
    }
    
    public static float negativeMultiply(float i,float m){
        return i<0?i*m:i;
    }
    
    
    public static void renderFace(Matrix4f pPose, Matrix3f fn, VertexConsumer bufferBuilder, int color,int light,
                                  float pX0, float pX1, float pY0, float pY1, float pZ0, float pZ1, float pZ2, float pZ3, Direction direction,
                                  float u0,float v0,float u1,float v1){
        var normal = direction.getNormal();
        var nx = normal.getX();
        var ny = normal.getY();
        var nz = normal.getZ();
        bufferBuilder.vertex(pPose, pX0, pY0, pZ0).color(color).uv(u0,v0).uv2(light).normal(fn,nx,ny,nz).endVertex();
        bufferBuilder.vertex(pPose, pX1, pY0, pZ1).color(color).uv(u0,v1).uv2(light).normal(fn,nx,ny,nz).endVertex();
        bufferBuilder.vertex(pPose, pX1, pY1, pZ2).color(color).uv(u1,v1).uv2(light).normal(fn,nx,ny,nz).endVertex();
        bufferBuilder.vertex(pPose, pX0, pY1, pZ3).color(color).uv(u1,v0).uv2(light).normal(fn,nx,ny,nz).endVertex();
    }
    
    @Override
    public int getViewDistance() {
        return 256;
    }
    
}
