package com.xkball.tin_tea_tech.client.hud;

import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.common.player.PlayerData;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import com.xkball.tin_tea_tech.network.packet.ScanPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@AutomaticRegistration
public class TricorderHUD implements IGuiOverlay {
    
    public static List<Component> toDrawBlock = new LinkedList<>();
    public static List<Component> toDrawEntity = new LinkedList<>();
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (PlayerData.get().modeAvailable(1) && Minecraft.getInstance().screen == null){
//            var player = Minecraft.getInstance().player;
//            player.getLookAngle()
            var hitResult = Minecraft.getInstance().hitResult;
            if(hitResult != null && hitResult.getType() == HitResult.Type.BLOCK){
                var blockpos = ((BlockHitResult)hitResult).getBlockPos();
                TTNetworkHandler.sentToServer(new ScanPacket(blockpos));
                
            }
            if(hitResult != null && hitResult.getType() == HitResult.Type.ENTITY){
                assert hitResult instanceof EntityHitResult;
                var entityHitResult = (EntityHitResult)hitResult;
                var uuid = entityHitResult.getEntity().getUUID();
                TTNetworkHandler.sentToServer(new ScanPacket(uuid));
            }
            if(!toDrawBlock.isEmpty() || !toDrawEntity.isEmpty()){
                var toDraw = new LinkedList<>(toDrawBlock);
                toDraw.addAll(toDrawEntity);
                guiGraphics.renderComponentTooltip(Minecraft.getInstance().font,toDraw ,-5,20);
            }
        }
    }
}
