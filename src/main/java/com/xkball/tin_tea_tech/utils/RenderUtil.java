package com.xkball.tin_tea_tech.utils;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public class RenderUtil {
    public static Quaternionf r90x =new Quaternionf(new AxisAngle4f((float) (Math.PI/2),1,0,0));
    public static Quaternionf r90y =new Quaternionf(new AxisAngle4f((float) (Math.PI/2),0,1,0));
    public static Quaternionf r90z =new Quaternionf(new AxisAngle4f((float) (Math.PI/2),0,0,1));
}
