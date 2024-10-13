package com.xkball.tin_tea_tech.utils.codec;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.xkball.tin_tea_tech.api.annotation.CodecProvider;
import net.minecraft.world.item.ItemStack;

public class TTCodecs {
    
    @CodecProvider
    public static Codec<Integer> INT = Codec.INT;
    
    @CodecProvider
    public static Codec<String> STRING = Codec.STRING;
    
    @CodecProvider
    public static Codec<Float> FLOAT = Codec.FLOAT;
    
    @CodecProvider
    public static Codec<Double> DOUBLE = Codec.DOUBLE;
    
    @CodecProvider
    public static Codec<ItemStack> ITEM_STACK = ItemStack.CODEC;
    
    @CodecProvider
    public static Codec<Unit> EMPTY = MapCodec.of(Encoder.empty(), Decoder.unit(Unit.INSTANCE)).codec();
    
}
