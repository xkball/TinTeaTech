package com.xkball.tin_tea_tech.common.item.armor;

import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.item.IHoloGlassPlugin;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.registration.TTRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.ParametersAreNonnullByDefault;


@AutomaticRegistration
@AutomaticRegistration.Item
@CreativeTag(tab = TTCreativeTab.TTMiscTab.class)
@I18N(chinese = "云钩",english = "Holo Glass")
@Model(resources = {"tin_tea_tech:item/holo_glass"})
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class HoloGlass extends ArmorItem  {
    public HoloGlass() {
        super(new ArmorMaterial() {
                  @Override
                  public int getDurabilityForType(Type pType) {
                      return -1;
                  }
                  
                  @Override
                  public int getDefenseForType(Type pType) {
                      return 0;
                  }
                  
                  @Override
                  public int getEnchantmentValue() {
                      return 0;
                  }
                  
                  @Override
                  public SoundEvent getEquipSound() {
                      return SoundEvents.ARMOR_EQUIP_CHAIN;
                  }
                  
                  @Override
                  public Ingredient getRepairIngredient() {
                      return Ingredient.EMPTY;
                  }
                  
                  @Override
                  public String getName() {
                      return "holo_glass";
                  }
                  
                  @Override
                  public float getToughness() {
                      return 0;
                  }
                  
                  @Override
                  public float getKnockbackResistance() {
                      return 0;
                  }
              },
                Type.HELMET,
                TTRegistration.getItemProperty().stacksTo(1));
    }
    
}
