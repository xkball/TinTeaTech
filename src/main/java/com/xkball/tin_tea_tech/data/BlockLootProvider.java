package com.xkball.tin_tea_tech.data;

import com.xkball.tin_tea_tech.registration.AutoRegManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Set;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockLootProvider extends LootTableProvider {
    
    public BlockLootProvider(PackOutput pOutput) {
        super(pOutput,Set.of(),List.of(new SubProviderEntry(TTBlockLootSubProvider::new, LootContextParamSet.builder().build())));
    }
    
    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationcontext) {
        map.forEach((k,v) -> v.validate(validationcontext));
    }
    
    public static class TTBlockLootSubProvider extends BlockLootSubProvider {
        
        protected TTBlockLootSubProvider() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }
        
        @Override
        protected void generate() {
            for(var b : AutoRegManager.ttBlocks.get()){
                this.dropSelf(b);
            }
        }
        
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return AutoRegManager.ttBlocks.get();
        }
    }
}
