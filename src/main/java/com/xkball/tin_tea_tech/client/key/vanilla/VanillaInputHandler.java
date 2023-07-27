package com.xkball.tin_tea_tech.client.key.vanilla;

import com.mojang.blaze3d.platform.InputConstants;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.client.gui.screen.HoloGlassScreen;
import com.xkball.tin_tea_tech.common.item.armor.HoloGlass;
import com.xkball.tin_tea_tech.common.player.IExtendedPlayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
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
    }}
