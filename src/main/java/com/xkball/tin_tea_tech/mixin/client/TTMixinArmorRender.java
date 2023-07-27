package com.xkball.tin_tea_tech.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.tin_tea_tech.common.item.armor.HoloGlass;
import com.xkball.tin_tea_tech.common.player.IExtendedPlayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class TTMixinArmorRender <T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    @Shadow protected abstract void setPartVisibility(A pModel, EquipmentSlot pSlot);
    
    @Shadow protected abstract boolean usesInnerModel(EquipmentSlot pSlot);
    
    @Shadow(remap = false) protected abstract Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model);
    
    @Shadow(remap = false) public abstract ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type);
    
    @Shadow protected abstract A getArmorModel(EquipmentSlot pSlot);
    
    @Shadow(remap = false) protected abstract void renderTrim(ArmorMaterial pArmorMaterial, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ArmorTrim pTrim, Model pModel, boolean pInnerTexture);
    
    
    @Shadow(remap = false) protected abstract void renderModel(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ArmorItem pArmorItem, Model pModel, boolean pWithGlint, float pRed, float pGreen, float pBlue, ResourceLocation armorResource);
    
    public TTMixinArmorRender(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }
    
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At("RETURN"))
    public void onRender(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci){
        if(pLivingEntity instanceof Player player){
            var is = ((IExtendedPlayer)player).getHeadItem();
            var item = is.getItem();
            if(item instanceof HoloGlass holoGlass){
                var pSlot = EquipmentSlot.HEAD;
                var pModel = this.getArmorModel(EquipmentSlot.HEAD);
                this.getParentModel().copyPropertiesTo(pModel);
                this.setPartVisibility(pModel, pSlot);
                net.minecraft.client.model.Model model = getArmorModelHook(pLivingEntity, is, pSlot, pModel);
                boolean flag = this.usesInnerModel(pSlot);
                this.renderModel(pMatrixStack, pBuffer, pPackedLight, holoGlass, model, flag, 1.0F, 1.0F, 1.0F, this.getArmorResource(pLivingEntity, is, pSlot, null));
                
                
                ArmorTrim.getTrim(pLivingEntity.level().registryAccess(), is).ifPresent((p_289638_) ->
                        this.renderTrim(holoGlass.getMaterial(), pMatrixStack, pBuffer, pPackedLight, p_289638_, model, flag));
               
            }
        }
        
    }
}
