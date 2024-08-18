package com.xkball.tin_tea_tech.api.reg;

import com.mojang.datafixers.util.Either;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AutoRegHolder<T,R extends T> implements Holder<T>, Supplier<R> {
    
    private final Supplier<R> rawSupplier;
    private DeferredHolder<T,R> innerHolder;
    private ResourceKey<T> key;
    
    //调用时需要写明T :(
    public static <T,R extends T> AutoRegHolder<T,R> create(Supplier<R> supplier){
        return new AutoRegHolder<>(supplier);
    }
    
    private AutoRegHolder(Supplier<R> supplier){
        this.rawSupplier = supplier;
    }
    
    //还是给不提供直接提供名称的注册留一种途径
    public AutoRegHolder<T,R> bind(DeferredRegister<T> register,String name) {
        if(innerHolder != null) throw new IllegalStateException("Already bind");
        this.innerHolder = register.register(name,rawSupplier);
        this.key = innerHolder.getKey();
        return this;
    }
    
    private void checkValid(){
        if (innerHolder == null) throw new NullPointerException(key+": innerHolder is null");
    }
    
    @Override
    public T value() {
        checkValid();
        return innerHolder.get();
    }
    
    @Override
    public boolean isBound() {
        checkValid();
        return innerHolder.isBound();
    }
    
    @Override
    public boolean is(ResourceLocation resourceLocation) {
        checkValid();
        return innerHolder.is(resourceLocation);
    }
    
    @Override
    public boolean is(ResourceKey<T> resourceKey) {
        checkValid();
        return innerHolder.is(resourceKey);
    }
    
    @Override
    public boolean is(Predicate<ResourceKey<T>> predicate) {
        checkValid();
        return innerHolder.is(predicate);
    }
    
    @Override
    public boolean is(TagKey<T> tagKey) {
        checkValid();
        return innerHolder.is(tagKey);
    }
    
    @Override
    @Deprecated
    public boolean is(Holder<T> holder) {
        checkValid();
        return innerHolder.is(holder);
    }
    
    @Override
    public Stream<TagKey<T>> tags() {
        checkValid();
        return innerHolder.tags();
    }
    
    @Override
    public Either<ResourceKey<T>, T> unwrap() {
        checkValid();
        return innerHolder.unwrap();
    }
    
    @Override
    public Optional<ResourceKey<T>> unwrapKey() {
        checkValid();
        return innerHolder.unwrapKey();
    }
    
    @Override
    public Kind kind() {
        return Kind.REFERENCE;
    }
    
    @Override
    public boolean canSerializeIn(HolderOwner<T> holderOwner) {
        checkValid();
        return innerHolder.canSerializeIn(holderOwner);
    }
    
    @Override
    public R get() {
        checkValid();
        return innerHolder.get();
    }
    
    @Override
    public <T1> @Nullable T1 getData(DataMapType<T, T1> type) {
        checkValid();
        return innerHolder.getData(type);
    }
}
