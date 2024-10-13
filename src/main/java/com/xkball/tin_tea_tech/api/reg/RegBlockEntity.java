package com.xkball.tin_tea_tech.api.reg;

import com.mojang.datafixers.DSL;
import com.mojang.logging.LogUtils;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.utils.VanillaUtils;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnusedReturnValue")
@EventBusSubscriber(modid = TinTeaTech.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegBlockEntity<T extends BlockEntity> {
    
    public static final Map<ResourceLocation,RegBlockEntity<?>> REG_BLOCK_ENTITY_POOL = new Object2ReferenceOpenHashMap<>();
    private static final List<Pair<ResourceLocation,RegBlock<?>>> TEMP = new LinkedList<>();
    
    public final String name;
    public final BlockEntityType.BlockEntitySupplier<T> supplier;
    public final AutoRegHolder<BlockEntityType<?>,BlockEntityType<T>> holder;
    public final List<RegBlock<?>> supportBlocks = new ArrayList<>();
    
    public RegBlockEntity(String name, BlockEntityType.BlockEntitySupplier<T> supplier) {
        this.name = name;
        this.supplier = supplier;
        this.holder = AutoRegHolder.create(() -> {
            var blocks = REG_BLOCK_ENTITY_POOL.get(VanillaUtils.modRL(name)).supportBlocks.stream().map(RegBlock::get).toArray(Block[]::new);
            return BlockEntityType.Builder.of(supplier,blocks).build(DSL.remainderType());
        });
        var loc = VanillaUtils.modRL(name);
        REG_BLOCK_ENTITY_POOL.put(loc, this);
        var list = TEMP.stream().filter( p -> p.first().equals(loc)).toList();
        list.forEach( p -> {
            this.addSupportBlock(p.right());
            TEMP.remove(p);
        });
    }
    
    public RegBlockEntity<T> addSupportBlock(RegBlock<?> block) {
        supportBlocks.add(block);
        return this;
    }
    
    public static void addBlockForTE(RegBlock<?> block,ResourceLocation loc) {
        if(REG_BLOCK_ENTITY_POOL.containsKey(loc)) {
            RegBlockEntity.REG_BLOCK_ENTITY_POOL.get(loc).addSupportBlock(block);
        }
        else {
            TEMP.add(Pair.of(loc,block));
        }
    }
    
    @SubscribeEvent
    public static void onReg(NewRegistryEvent event){
        if(TEMP.isEmpty()) return;
        var logger = LogUtils.getLogger();
        logger.error("Missing {} block entities:", TEMP.size());
        for(Pair<ResourceLocation,RegBlock<?>> pair : TEMP) {
            logger.error("  missing {} for block {}", pair.first(), pair.second().getName());
        }
        throw new RuntimeException("Missing block entities");
    }
}
