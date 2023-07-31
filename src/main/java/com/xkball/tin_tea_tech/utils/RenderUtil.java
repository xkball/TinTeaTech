package com.xkball.tin_tea_tech.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xkball.tin_tea_tech.client.shape.Line2D;
import com.xkball.tin_tea_tech.client.shape.Line3D;
import com.xkball.tin_tea_tech.client.shape.Point2D;
import com.xkball.tin_tea_tech.client.shape.Shape2D;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public class RenderUtil {
    public static final double H01;
    public static final double H1;
    public static final double H2;
    
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
}
