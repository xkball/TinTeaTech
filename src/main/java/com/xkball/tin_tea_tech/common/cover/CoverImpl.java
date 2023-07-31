package com.xkball.tin_tea_tech.common.cover;

import com.xkball.tin_tea_tech.api.mte.cover.Cover;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class CoverImpl implements Cover {
    
    private final MetaTileEntity mte;
    private final Direction direction;
    private String name = null;
    
    public CoverImpl(MetaTileEntity mte, Direction direction) {
        this.mte = mte;
        this.direction = direction;
    }
    public String getName(){
        if(name == null){
            name = AutoRegManager.fromClassName(this.getClass());
        }
        return name;
    }
    
    @Override
    public void tick() {
    
    }
    
    @Override
    public void onRemove() {
    
    }
    
    @Override
    public ItemStack asItemStack() {
        return ((Item)AutoRegManager.getRegistryObject(getName()).get()).getDefaultInstance();
    }
    
    @Override
    public MetaTileEntity getMTE() {
        return mte;
    }
    
    @Override
    public Direction getDirection() {
        return direction;
    }
}
