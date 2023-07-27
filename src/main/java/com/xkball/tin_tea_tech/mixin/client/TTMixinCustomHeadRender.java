package com.xkball.tin_tea_tech.mixin.client;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.tin_tea_tech.common.item.armor.HoloGlass;
import com.xkball.tin_tea_tech.common.player.IExtendedPlayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(CustomHeadLayer.class)
public abstract class TTMixinCustomHeadRender<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
    
    @Shadow
    public static void translateToHead(PoseStack pPoseStack, boolean pIsVillager) {}
    
    @Shadow @Final private ItemInHandRenderer itemInHandRenderer;
    
    @Shadow @Final private float scaleX;
    
    @Shadow @Final private float scaleY;
    
    @Shadow @Final private float scaleZ;
    
    @Shadow @Final private Map<SkullBlock.Type, SkullModelBase> skullModels;
    
    public TTMixinCustomHeadRender(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }
    
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At("HEAD"))
    public void onRender(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci){
        if(pLivingEntity instanceof Player player){
            var is = ((IExtendedPlayer)player).getHeadItem();
            if (!is.isEmpty()) {
                Item item = is.getItem();
                if(item instanceof HoloGlass){
                    return;
                }
                pMatrixStack.pushPose();
                pMatrixStack.scale(this.scaleX, this.scaleY, this.scaleZ);
                boolean flag = false;
                
                this.getParentModel().getHead().translateAndRotate(pMatrixStack);
                if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
                    pMatrixStack.scale(1.1875F, -1.1875F, -1.1875F);
                    
                    GameProfile gameprofile = null;
                    if (is.hasTag()) {
                        CompoundTag compoundtag = is.getTag();
                        if (compoundtag != null && compoundtag.contains("SkullOwner", 10)) {
                            gameprofile = NbtUtils.readGameProfile(compoundtag.getCompound("SkullOwner"));
                        }
                    }
                    
                    pMatrixStack.translate(-0.5D, 0.0D, -0.5D);
                    SkullBlock.Type skullblock$type = ((AbstractSkullBlock)((BlockItem)item).getBlock()).getType();
                    SkullModelBase skullmodelbase = this.skullModels.get(skullblock$type);
                    RenderType rendertype = SkullBlockRenderer.getRenderType(skullblock$type, gameprofile);
                    Entity entity = pLivingEntity.getVehicle();
                    WalkAnimationState walkanimationstate;
                    if (entity instanceof LivingEntity livingentity) {
                        walkanimationstate = livingentity.walkAnimation;
                    } else {
                        walkanimationstate = pLivingEntity.walkAnimation;
                    }
                    
                    float f3 = walkanimationstate.position(pPartialTicks);
                    SkullBlockRenderer.renderSkull(null, 180.0F, f3, pMatrixStack, pBuffer, pPackedLight, skullmodelbase, rendertype);
                } else {
                        translateToHead(pMatrixStack, flag);
                        this.itemInHandRenderer.renderItem(pLivingEntity, is, ItemDisplayContext.HEAD, false, pMatrixStack, pBuffer, pPackedLight);
                }
                
                pMatrixStack.popPose();
            }
        }
    }
}
