package com.xkball.tin_tea_tech.api.mte;

import com.xkball.tin_tea_tech.api.block.te.TTTileEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

//BlockEntityType: 像啊 很像啊
public class MTEType<T extends IMTEBehaviour<T>> {
    
    public final ResourceLocation location;
    private final BiFunction<BlockPos,TTTileEntityBase,T> supplier;
    
    public MTEType(ResourceLocation location, BiFunction<BlockPos, TTTileEntityBase, T> suppiler) {
        this.location = location;
        this.supplier = suppiler;
    }
    
    public IMTEBehaviour<T> newMetaTileEntity(BlockPos pos, TTTileEntityBase te){
        return supplier.apply(pos, te);
    }
}
