package com.xkball.tin_tea_tech.common.saveddata;

import com.xkball.tin_tea_tech.client.shape.Point2D;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LatitudeAndLongitude extends SavedData {

    //东经
    private double E = 0;
    //北纬
    private double N = 0;
    
    public LatitudeAndLongitude(){
    
    }
    
    public static LatitudeAndLongitude load(CompoundTag tag){
        var ll = new LatitudeAndLongitude();
        ll.E = tag.getDouble("E");
        ll.N = tag.getDouble("N");
        return ll;
    }
    
    //no need
//    public static LatitudeAndLongitude create(ServerLevel level){
//        return create(level.getSeed());
//    }
    
    public static LatitudeAndLongitude create(long seed){
        var ll = new LatitudeAndLongitude();
        var random = RandomSource.create(seed);
        var negativeE = random.nextBoolean();
        var negativeN = random.nextBoolean();
        var rE = random.nextInt(180);
        ll.E = negativeE?-rE:rE;
        if(random.nextInt(100)>10){
            var rN = random.nextInt(60);
            ll.N = negativeN?-rN:rN;
        }else {
            var rN = random.nextInt(60,90);
            ll.N = negativeN?-rN:rN;
        }
        ll.setDirty();
        return ll;
    }
    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        pCompoundTag.putDouble("E",E);
        pCompoundTag.putDouble("N",N);
        return pCompoundTag;
    }
    
    public Point2D getLatitudeAndLongitude(BlockPos pos){
        double x = pos.getX()/3996000d;
        double y = pos.getY()/1998000d;
        var rE = E+180;
        var rN = N+90;
        rE = (rE+x*360)%360 - 180;
        rN = (rN+y*180)%180 - 90;
        return new Point2D(rE,rN);
    }
}
