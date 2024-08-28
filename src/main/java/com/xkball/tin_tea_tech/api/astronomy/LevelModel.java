package com.xkball.tin_tea_tech.api.astronomy;

import com.mojang.math.Axis;
import com.xkball.tin_tea_tech.utils.ClientUtils;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import org.joml.Vector2f;
import org.joml.Vector3f;

//仅用于主世界
public class LevelModel {
    
    private static final Vector3f YP = new Vector3f(0f,1f,0f);
    private static final Vector3f earthAxis = new Vector3f((float) -Math.sin(Math.toRadians(23.5f)), (float) Math.cos(Math.toRadians(23.5f)), 0f);
    
    private final DimensionSpecialEffects.SkyType skyType;
    
    
    private Vector3f sunVec = new Vector3f();
    
    public LevelModel(DimensionSpecialEffects.SkyType skyType) {
        this.skyType = skyType;
    }
    
    public void tick(long dayTime){
        if(skyType == DimensionSpecialEffects.SkyType.NORMAL){
           
            //todo: 用矩阵重写 渲染用矩阵处理 太阳角度靠单独的向量算
            double theta = Math.toRadians(((dayTime%24000f)/24000f)*360f);
            //从地球上看太阳角度,负的
            Vector3f sun = new Vector3f((float) -Math.cos(theta),0f, (float) -Math.sin(theta));
            var ll = ClientUtils.getLatitudeAndLongitude();
            var e = Math.toRadians(ll.x()+180);
            Vector2f self2d = new Vector2f((float) Math.cos(e),(float) Math.sin(e));
            Vector3f self = new Vector3f(self2d.x, 0,self2d.y);
            //应该是YN 但是很怪
            Vector3f crossSelf = self.rotate(Axis.YP.rotationDegrees(90),new Vector3f());
            self.rotate(Axis.of(crossSelf).rotationDegrees((float) ll.y()));
            self.rotate(Axis.ZP.rotationDegrees(23.5f));
            self.rotate(Axis.of(earthAxis).rotationDegrees((((((dayTime/24000f)%365f))/365f)*360f)));
            var axis2 = self.cross(YP,new Vector3f()).normalize();
            var theta2 = self.angle(YP);
            sun.rotate(Axis.of(axis2).rotation(theta2));
            this.sunVec = sun.normalize();
            //var day = (int) (dayTime/24000)%365;
           
        }
    }
    
    public Vector3f getSunVec() {
        return sunVec;
    }
}
