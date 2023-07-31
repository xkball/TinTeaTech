package com.xkball.tin_tea_tech.client.key.vanilla;

import com.mojang.blaze3d.platform.InputConstants;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.client.gui.screen.HoloGlassScreen;
import com.xkball.tin_tea_tech.common.item.armor.HoloGlass;
import com.xkball.tin_tea_tech.common.player.IExtendedPlayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class VanillaInputHandler {
    public static final KeyMapping OPEN_HOLO_GlASS_KEY = new KeyMapping("key.tin_tea_tech.open_holo_glass",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            "key.category." + TinTeaTech.MODID);
    
    
    public static final KeyMapping PRINT_NBT = new KeyMapping("key.tin_tea_tech.print_nbt",
            KeyConflictContext.IN_GAME,
            KeyModifier.ALT,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "key.category." + TinTeaTech.MODID);
    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.Key event) {
        if (OPEN_HOLO_GlASS_KEY.isDown()) {
            var player = Minecraft.getInstance().player;
            if(player != null && player.isAlive()){
                var is = ((IExtendedPlayer)player).getHeadItem();
                if(is.getItem() instanceof HoloGlass ){
                    var tag = is.getOrCreateTag();
                    tag.putInt("openedFromPlayerSlot",10000);
                    tag.put("openedFromItem",is.save(new CompoundTag()));
                    Minecraft.getInstance().setScreen(new HoloGlassScreen(tag));
                }
                is = player.getItemBySlot(EquipmentSlot.HEAD);
                if(is.getItem() instanceof HoloGlass){
                    var tag = is.getOrCreateTag();
                    tag.putInt("openedFromPlayerSlot",103);
                    tag.put("openedFromItem",is.save(new CompoundTag()));
                    Minecraft.getInstance().setScreen(new HoloGlassScreen(tag));
                }
            }
        }
        if(PRINT_NBT.isDown()){
            var player = Minecraft.getInstance().player;
            var hitResult = Minecraft.getInstance().hitResult;
            if (player != null){
                if(hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                    var blockpos = ((BlockHitResult) hitResult).getBlockPos();
                    var te = player.level().getBlockEntity(blockpos);
                    if(te != null){
                        player.sendSystemMessage(Component.literal("Client Block looking at nbt: "));
                        player.sendSystemMessage(Component.literal(te.saveWithFullMetadata().toString()));
                    }
                }
                var item = player.getItemInHand(InteractionHand.MAIN_HAND);
                if(item.hasTag()){
                    player.sendSystemMessage(Component.literal("Client Item in hand nbt: "));
                    assert item.getTag() != null;
                    player.sendSystemMessage(Component.literal(item.getTag().toString()));
                }
            }
            
        }
    }}
