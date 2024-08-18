package com.xkball.tin_tea_tech.registry;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;

public enum AutoRegManager {
    INSTANCE;
    
    public static void init(ModContainer container) {
        var logger = LogManager.getLogger();
        if(container instanceof FMLModContainer fmlModContainer) {
            try {
                var scanResultField = FMLModContainer.class.getDeclaredField("scanResults");
                scanResultField.setAccessible(true);
                AutoRegManager.INSTANCE._init(container.getModId(),(ModFileScanData) scanResultField.get(fmlModContainer));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private void _init(String modid,ModFileScanData modFileScanData){
    
    }
}
