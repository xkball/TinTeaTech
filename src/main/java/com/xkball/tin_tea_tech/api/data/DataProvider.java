package com.xkball.tin_tea_tech.api.data;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;

import static net.minecraft.network.chat.Component.literal;

public interface DataProvider {
    Collection<Component> getInfo();
    
    static Component crossLine(){
        return literal("------------------------------------------").withStyle(ChatFormatting.AQUA);
    }
    
    static Collection<Component> blockInfo(BlockState state, BlockPos pos){
        ArrayList<Component> result = new ArrayList<>();
        result.add(crossLine());
        result.add(translatable("block_info").withStyle(ChatFormatting.BOLD));
        result.add(translatable("block_name")
                .append(state.getBlock().getName()));
        result.add(translatable("block_pos")
                .append(literal(" x: "+pos.getX()+" y: "+pos.getY()+" z: "+pos.getZ())));
        return result;
    }
    
    
    
    static MutableComponent      translatable(String key){
        return Component.translatable("info.tin_tea_tech."+key);
    }
}
