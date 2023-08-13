package com.xkball.tin_tea_tech.common.robot;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RobotData extends SavedData {
    private static final String NAME = "tin_tea_tech_robot_data";
    private final RobotManager robotManager = new RobotManager();
    
    public static RobotManager get(Level level){
        if(level instanceof ServerLevel serverLevel){
            var s = serverLevel.getDataStorage();
            return s.computeIfAbsent((tag) -> {
                var result = new RobotData(); result.load(tag); return result;
            },RobotData::new,NAME).robotManager;
        }
        throw new RuntimeException("you can only get data in server");
    }
    
    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        pCompoundTag.put("data",robotManager.serializeNBT());
        return pCompoundTag;
    }
    
    public void load(CompoundTag tag){
        robotManager.deserializeNBT(tag);
    }
    
    @Override
    public boolean isDirty() {
        return true;
    }
}
