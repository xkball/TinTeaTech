package com.xkball.tin_tea_tech.utils;

import com.xkball.tin_tea_tech.TinTeaTech;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class VanillaUtils {
    
    public static ResourceLocation modRL(String path) {
        return rLOf(TinTeaTech.MODID, path);
    }
    
    public static ResourceLocation rLOf(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
    
    public static EquipmentSlot equipmentSlotFromHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }
    
    public static ItemInteractionResult itemInteractionFrom(InteractionResult result) {
        return switch (result){
            case SUCCESS, SUCCESS_NO_ITEM_USED -> ItemInteractionResult.SUCCESS;
            case CONSUME -> ItemInteractionResult.CONSUME;
            case CONSUME_PARTIAL -> ItemInteractionResult.CONSUME_PARTIAL;
            case PASS -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            case FAIL -> ItemInteractionResult.FAIL;
        };
    }
    
    public static void runCommand(String command, LivingEntity livingEntity) {
        // Raise permission level to 2, akin to what vanilla sign does
        CommandSourceStack cmdSrc = livingEntity.createCommandSourceStack().withPermission(2);
        var server = livingEntity.level().getServer();
        if (server != null) {
            server.getCommands().performPrefixedCommand(cmdSrc, command);
        }
    }
    
    //irrelevant vanilla(笑)
    public static int getColor(int r,int g,int b,int a){
        return a << 24 | r << 16 | g << 8 | b;
    }
}
