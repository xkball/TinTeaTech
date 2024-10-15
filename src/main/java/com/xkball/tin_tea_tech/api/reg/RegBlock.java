package com.xkball.tin_tea_tech.api.reg;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.registry.TinTeaTechRegistries;
import com.xkball.tin_tea_tech.utils.VanillaUtils;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RegBlock<T extends Block> implements ItemLike {
    
    public static final Map<ResourceLocation, RegBlock<?>> REG_BLOCK_POOL = new Object2ReferenceOpenHashMap<>();
    
    private final AutoRegHolder<Block, T> holder;
    private final String name;
    private I18NEntry i18n;
    private RegItem<? extends BlockItem> blockItemHolder;
    private ResourceLocation blockEntityLocation;
    private Class<? extends BlockEntityRenderer<?>> renderClass;
    private ResourceLocation blockStateLocation;
    private ResourceLocation itemModelLocation;
    
    public RegBlock(String name, Supplier<T> supplier) {
        this.name = name;
        this.holder = AutoRegHolder.<Block, T>create(supplier).bind(TinTeaTechRegistries.BLOCK, name);
        REG_BLOCK_POOL.put(VanillaUtils.modRL(name), this);
    }
    
    public RegBlock<T> setBlockItem(Supplier<? extends BlockItem> supplier) {
        blockItemHolder = new RegItem<>(this.name, supplier);
        return this;
    }
    
    public RegBlock<T> simpleBlockItem() {
        blockItemHolder = new RegItem<>(this.name, () -> new BlockItem(holder.get(), new Item.Properties()));
        return this;
    }
    
    public RegBlock<T> setI18n(String en_us, String zh_cn) {
        this.i18n = new I18NEntry("block." + TinTeaTech.MODID + "." + name, en_us, zh_cn);
        return this;
    }
    
    public RegBlock<T> setBlockEntityLocation(ResourceLocation location) {
        this.blockEntityLocation = location;
        RegBlockEntity.addBlockForTE(this, location);
        return this;
    }
    
    public RegBlock<T> setBlockStateLocation(ResourceLocation location) {
        this.blockStateLocation = location;
        return this;
    }
    
    public RegBlock<T> setItemModelParent(ResourceLocation location) {
        this.itemModelLocation = location;
        return this;
    }
    
    public RegBlock<T> setItemModelLocation(ResourceLocation location) {
        this.blockItemHolder.setModelLocation(location);
        return this;
    }
    
    public RegBlock<T> setCreativeTab(Holder<CreativeModeTab> tab) {
        this.blockItemHolder.setCreativeTab(tab);
        return this;
    }
    
    @Nullable
    public ResourceLocation getItemModelLocation() {
        return itemModelLocation;
    }
    
    @Nullable
    public ResourceLocation getBlockStateLocation() {
        return blockStateLocation;
    }
    
    @Nullable
    public RegItem<? extends BlockItem> getBlockItemHolder() {
        return blockItemHolder;
    }
    
    @Nullable
    public I18NEntry getI18n() {
        return i18n;
    }
    
    
    public T get() {
        return holder.get();
    }
    
    @Override
    public Item asItem() {
        assert blockItemHolder != null;
        return blockItemHolder.asItem();
    }
    
    public ResourceLocation getBlockEntityLocation() {
        return blockEntityLocation;
    }
    
    public String getName() {
        return name;
    }
}
