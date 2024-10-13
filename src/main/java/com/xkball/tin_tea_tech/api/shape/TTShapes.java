package com.xkball.tin_tea_tech.api.shape;

import com.xkball.tin_tea_tech.api.shape.def.Line2D;
import com.xkball.tin_tea_tech.api.shape.def.Point2D;
import com.xkball.tin_tea_tech.api.shape.def.Shape2D;

public class TTShapes {
    //#形
    public static final Shape2D biCrossMusk;
    //X形
    public static final Shape2D crossMusk;
    
    public static final Shape2D upCross;
    public static final Shape2D downCross;
    public static final Shape2D leftCross;
    public static final Shape2D rightCross;
    
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
}
