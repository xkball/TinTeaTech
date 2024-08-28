package com.xkball.tin_tea_tech.utils;

import ca.weblite.objc.Client;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.client.shape.*;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;

public class RenderUtil {
    public static final double H01;
    public static final double H1;
    public static final double H2;
    
    private static final Vector3f YP = new Vector3f(0f,1f,0f);
    
    private static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
    
    static {
        H01 = 1d/16d;
        H1 = (1d/16d)*3;
        H2 = 1-H1;
        biCrossMusk = new Shape2D()
            .addLine(new Line2D(new Point2D(0.0,H1),new Point2D(1.0,H1)))
            .addLine(new Line2D(new Point2D(0.0,H2),new Point2D(1.0,H2)))
            .addLine(new Line2D(new Point2D(H1,0.0),new Point2D(H1,1.0)))
            .addLine(new Line2D(new Point2D(H2,0.0),new Point2D(H2,1.0))).end();
        crossMusk = new Shape2D()
                .addLine(new Line2D(new Point2D(H1,H1),new Point2D(H2,H2)))
                .addLine(new Line2D(new Point2D(H1,H2),new Point2D(H2,H1))).end();
        leftCross = new Shape2D()
                .addLine(new Line2D(new Point2D(0,H1),new Point2D(H1,H2)))
                .addLine(new Line2D(new Point2D(0,H2),new Point2D(H1,H1))).end();
        rightCross = new Shape2D()
                .addLine(new Line2D(new Point2D(H2,H1),new Point2D(1,H2)))
                .addLine(new Line2D(new Point2D(H2,H2),new Point2D(1,H1))).end();
        upCross = new Shape2D()
                .addLine(new Line2D(new Point2D(H1,H2),new Point2D(H2,1)))
                .addLine(new Line2D(new Point2D(H1,1),new Point2D(H2,H2))).end();
        downCross = new Shape2D()
                .addLine(new Line2D(new Point2D(H1,0),new Point2D(H2,H1)))
                .addLine(new Line2D(new Point2D(H1,H1),new Point2D(H2,0))).end();
        
    }
    public static Quaternionf r90x =new Quaternionf(new AxisAngle4f((float) (Math.PI/2),1,0,0));
    public static Quaternionf r90y =new Quaternionf(new AxisAngle4f((float) (Math.PI/2),0,1,0));
    public static Quaternionf r90z =new Quaternionf(new AxisAngle4f((float) (Math.PI/2),0,0,1));
    
    
    public static class FluidRenderType extends RenderType {
        public static final RenderType FLUID = RenderType.create(
                TinTeaTech.MODID + ":block_render_type",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 512, false, true,
                RenderType.CompositeState.builder()
                        .setLightmapState(LIGHTMAP)
                        .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                        .setTextureState(BLOCK_SHEET_MIPPED)
                        //.setDepthTestState(NO_DEPTH_TEST)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .createCompositeState(false));
        
        public FluidRenderType(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
            super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
        }
    }
    
    
    //#形
    public static final Shape2D biCrossMusk;
    //X形
    public static final Shape2D crossMusk;
    
    public static final Shape2D upCross;
    public static final Shape2D downCross;
    public static final Shape2D leftCross;
    public static final Shape2D rightCross;
    public static void drawShape(VertexConsumer pConsumer, PoseStack poseStack,Shape2D shape, Direction direction,
                                 double pX, double pY, double pZ,
                                 float r, float g, float b, float a){
        for(var line : shape.toDraw(direction)){
            drawLine(pConsumer,poseStack,line,pX,pY,pZ,r,g,b,a);
        }
    }
    
    public static void drawLine(VertexConsumer pConsumer, PoseStack poseStack,
                                Line3D line,
                                double pX, double pY, double pZ,
                                float r, float g, float b, float a){
        drawLine(pConsumer,poseStack,line.start().x(),line.start().y(),line.start().z(),
                line.end().x(),line.end().y(),line.end().z(),pX,pY,pZ,r,g,b,a);
    }
    
    public static void drawLine(VertexConsumer pConsumer, PoseStack poseStack,
                                double sx, double sy, double sz,
                                double ex, double ey, double ez,
                                double pX, double pY, double pZ,
                                float r, float g, float b, float a){
        PoseStack.Pose posestack$pose = poseStack.last();
        float f = (float)(ex - sx);
        float f1 = (float)(ey - sy);
        float f2 = (float)(ez - sz);
        float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
        f /= f3;
        f1 /= f3;
        f2 /= f3;
        pConsumer.vertex(posestack$pose.pose(), (float)(sx + pX), (float)(sy + pY), (float)(sz + pZ))
                .color(r, g, b, a)
                .normal(posestack$pose.normal(), f, f1, f2).endVertex();
        pConsumer.vertex(posestack$pose.pose(), (float)(ex + pX), (float)(ey + pY), (float)(ez + pZ))
                .color(r, g, b, a)
                .normal(posestack$pose.normal(), f, f1, f2).endVertex();
        
    }
    
    public static int getLight(Level level, BlockPos pos){
        var light = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));
        return  (light & 0xFFFF) | (((light >> 16) & 0xFFFF) << 16);
    }
    
    public static int getLight(MetaTileEntity mte){
        return getLight(mte.getLevel(),mte.getPos());
    }
    
    public static void renderFace(Matrix4f pPose, Matrix3f fn, VertexConsumer bufferBuilder, int color, int light,
                                  Vec8f vertex, Direction direction, TextureAtlasSprite texture){
        var normal = direction.getNormal();
        var nx = normal.getX();
        var ny = normal.getY();
        var nz = normal.getZ();
        var pX0 = vertex.pX0();
        var pY0 = vertex.pY0();
        var pX1 = vertex.pX1();
        var pY1 = vertex.pY1();
        var pZ0 = vertex.pZ0();
        var pZ1 = vertex.pZ1();
        var pZ2 = vertex.pZ2();
        var pZ3 = vertex.pZ3();
        var u0= texture.getU0();
        var u1 = texture.getU1();
        var v0 = texture.getV0();
        var v1 = texture.getV1();
        
        bufferBuilder.vertex(pPose, pX0, pY0, pZ0).color(color).uv(u0,v0).uv2(light).normal(fn,nx,ny,nz).endVertex();
        bufferBuilder.vertex(pPose, pX1, pY0, pZ1).color(color).uv(u0,v1).uv2(light).normal(fn,nx,ny,nz).endVertex();
        bufferBuilder.vertex(pPose, pX1, pY1, pZ2).color(color).uv(u1,v1).uv2(light).normal(fn,nx,ny,nz).endVertex();
        bufferBuilder.vertex(pPose, pX0, pY1, pZ3).color(color).uv(u1,v0).uv2(light).normal(fn,nx,ny,nz).endVertex();
        
    }
    
    //还需要调整:fog的红雾
    //time of day曲线
    //star brightness曲线
    // 重写太阳高度
    public static void renderSky(LevelRenderer levelRenderer, ClientLevel level,PoseStack pPoseStack, Matrix4f pProjectionMatrix,float pPartialTick,VertexBuffer skyBuffer,VertexBuffer starBuffer,Runnable pSkyFogSetup){
        //light sky
        Vec3 vec3 = level.getSkyColor(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), pPartialTick);
        float f = (float)vec3.x;
        float f1 = (float)vec3.y;
        float f2 = (float)vec3.z;
        pPoseStack.pushPose();
        var skyDarken = level.getSkyDarken(pPartialTick);
        pPoseStack.translate(0,(1-skyDarken)*10,0);
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(f, f1, f2, Mth.clamp(skyDarken*2,0f,1f));
        ShaderInstance shaderinstance = RenderSystem.getShader();
        skyBuffer.bind();
        skyBuffer.drawWithShader(pPoseStack.last().pose(), pProjectionMatrix, shaderinstance);
        pPoseStack.popPose();
        VertexBuffer.unbind();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        var ll = ClientUtils.getLatitudeAndLongitude();
        //star
        RenderSystem.enableBlend();
        float brightness = level.getStarBrightness(pPartialTick)*(1.0F - level.getRainLevel(pPartialTick));
        if(brightness>0F){
            
            pPoseStack.pushPose();
            pPoseStack.mulPose(Axis.XP.rotationDegrees((float) ll.y()));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees((level.getDayTime()%24000f)/24000f * 360.0F));
            brightness = Mth.clamp(brightness*2,0f,1f);
            RenderSystem.setShaderColor(1f,1f,1f,brightness);
            FogRenderer.setupNoFog();
            //FogRenderer.setupNoFog();
            starBuffer.bind();
            starBuffer.drawWithShader(pPoseStack.last().pose(), pProjectionMatrix, GameRenderer.getPositionShader());
            VertexBuffer.unbind();
            pSkyFogSetup.run();
            
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            pPoseStack.popPose();
        }
        //sun
        //RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        pPoseStack.pushPose();
        float f11 = 1.0F - level.getRainLevel(pPartialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f11);
        var sun = ClientUtils.getLevelModel().getSunVec();
        //var axis = sun.cross(YP,new Vector3f());
        //var theta = sun.angle(YP);
        var axis = sun.cross(YP,new Vector3f());
        var theta = sun.angle(YP);
        pPoseStack.mulPose(Axis.of(axis).rotation(theta));
        Matrix4f matrix4f1 = pPoseStack.last().pose();
        float f12 = 30.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_LOCATION);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, -f12).uv(0.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, f12, 100.0F, -f12).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, f12, 100.0F, f12).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, -f12, 100.0F, f12).uv(0.0F, 1.0F).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        pPoseStack.popPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }
}
