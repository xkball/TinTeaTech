package com.xkball.tin_tea_tech.api.data;

import com.xkball.tin_tea_tech.client.shape.Point2D;
import com.xkball.tin_tea_tech.common.saveddata.LatitudeAndLongitude;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface LatitudeAndLongitudeAccess {
    Point2D tin_tea_tech$getLatitudeAndLongitude(Player player);
    void tin_tea_tech$reInitLatitudeAndLongitude(CompoundTag tag);
    
    LatitudeAndLongitude tin_tea_tech$getLALSelf();
}
