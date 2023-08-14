package com.xkball.tin_tea_tech.common.player;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdditionalInventory extends SimpleContainer {
    
    //0为头后栏
    //1-9暂未决定用处 大概是给放工具的
    public AdditionalInventory() {
        super(10);
    }
    
//    @Override
//    public ItemStack getItem(int pIndex) {
//        return super.getItem(0);
//    }
    
//
//    @Override
//    public void setItem(int pIndex, ItemStack pStack) {
//        super.setItem(pIndex, pStack);
//
//    }
    
    @Override
    public void fromTag(ListTag pContainerNbt) {
        this.clearContent();
        
        for(int i = 0; i < pContainerNbt.size(); ++i) {
            ItemStack itemstack = ItemStack.of(pContainerNbt.getCompound(i));
            if (!itemstack.isEmpty()) {
                this.setItem(i,itemstack);
            }
            else {
                this.setItem(i,ItemStack.EMPTY);
            }
        }
        
    }
    
    @Override
    public ListTag createTag() {
        ListTag listtag = new ListTag();
        
        for(int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemstack = this.getItem(i);
            //if (!itemstack.isEmpty()) {
                listtag.add(itemstack.save(new CompoundTag()));
            //}
        }
        
        return listtag;
    }
}
