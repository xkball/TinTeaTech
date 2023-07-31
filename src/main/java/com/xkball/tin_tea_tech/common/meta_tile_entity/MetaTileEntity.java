package com.xkball.tin_tea_tech.common.meta_tile_entity;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.TTValue;
import com.xkball.tin_tea_tech.api.item.TTItemHandler;
import com.xkball.tin_tea_tech.api.mte.IMTEBehaviour;
import com.xkball.tin_tea_tech.api.mte.cover.VerticalCover;
import com.xkball.tin_tea_tech.common.cover.CoverHandler;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import com.xkball.tin_tea_tech.utils.FinalObj;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

//第一次创建MTE的时候te为null,pos为0,0,0
public abstract class MetaTileEntity implements IMTEBehaviour {
    public static Map<String,MetaTileEntity> mteMap = new HashMap<>();
    
    @Nullable
    protected final TTTileEntityBase te;
    
    //final不了...
    @OnlyIn(Dist.CLIENT)
    protected Class<? extends BlockEntityRenderer<?>> renderClass;
    
    protected final CoverHandler coverHandler;
    @OnlyIn(Dist.CLIENT)
    protected BakedModel[] models;
    protected final FinalObj<TTItemHandler> itemHandler = new FinalObj<>();
    
    protected final FinalObj<String> name = new FinalObj<>();
    
    protected boolean enabled = false;
    
    //cap
    protected final LazyOptional<IItemHandler> itemHandlerCap;
    
    
    public MetaTileEntity(@Nonnull BlockPos pos, @Nullable TTTileEntityBase te){
        this.te = te;
        this.createInventory();
        itemHandlerCap = LazyOptional.of(this.itemHandler::get);
        coverHandler = new CoverHandler(this);
        if(isClient()){
            if(te != null){
                models = getModels().get();
            }
            else {
                models = null;
            }
            renderClass = IMTEBehaviour.super.getRendererClass();
           
        }
    }
    
    @Override
    public void tick() {
        coverHandler.tick();
    }
    
    @Override
    public VerticalCover getCoverHandler() {
        return coverHandler;
    }
    
    @Override
    public void syncRenderData() {
        coverHandler.syncRender();
    }
    
    @Override
    public String getName() {
        if(name.isEmpty()){
            name.set(AutoRegManager.fromClassName(
                    this.getClass()
                    //TinTeaTech.STACK_WALKER.getCallerClass()
            ));
        }
        return name.get();
    }
    
    abstract protected Supplier<TTItemHandler> getItemHandlerSupplier();
    
   
    private void createInventory(){
        this.itemHandler.set(getItemHandlerSupplier().get());
    }
    
    @Override
    public int getOffsetTick() {
        if (te != null) {
            return te.getOffset() + TinTeaTech.ticks;
        }
        return 0;
    }
    
    @Override
    @Nullable
    public TTTileEntityBase getTileEntity() {
        return te;
    }
    
    @Override
    public void firstClientTick() {
        sentToServer((t) -> {});
    }
    
    @Override
    public void readClientData(CompoundTag data) {
        syncRenderData();
    }
    
    @Override
    public void sentCustomData(int id, Consumer<ByteBuf> bufConsumer) {
        if (this.te != null) {
            this.te.sentCustomData(id,bufConsumer);
        }
    }
    
    @Override
    public void onRemove() {
        LevelUtils.dropItem(getLevel(),getPos(),this.itemHandler.get());
        coverHandler.removeAll();
    }
    
    @Override
    public Class<? extends BlockEntityRenderer<?>> getRendererClass() {
        return renderClass;
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            return itemHandlerCap.cast();
        }
        return coverHandler.getCapability(cap,side);
    }
    
    @Override
    public void writeInitData(CompoundTag tag) {
        tag.put("ih",itemHandler.get().serializeNBT());
        tag.putBoolean("e",enabled);
        tag.put("cover",coverHandler.serializeNBT());
    }
    
    @Override
    public void readInitData(CompoundTag tag) {
        itemHandler.get().deserializeNBT(tag.getCompound("ih"));
        enabled = tag.getBoolean("e");
        coverHandler.deserializeNBT(tag.getCompound("cover"));
    }
    
    public boolean isClient(){
        return FMLEnvironment.dist == Dist.CLIENT;
    }
    
    //用于动态渲染
    @Override
    @OnlyIn(Dist.CLIENT)
    public BakedModel[] needToRender() {
        return models;
    }
    
    //用于静态的渲染
    @OnlyIn(Dist.CLIENT)
    protected Supplier<BakedModel[]> getModels(){
        return () -> new BakedModel[]{Minecraft.getInstance().getModelManager().getMissingModel()};
    }
    
    
    @OnlyIn(Dist.CLIENT)
    protected BakedModel getModel(ResourceLocation resourceLocation){
        return Minecraft.getInstance().getModelManager().getModel(resourceLocation);
    }
    
    @Override
    public void readCustomData(int id, ByteBuf byteBuf) {
        if(id == TTValue.COVER){
            coverHandler.loadRenderData(byteBuf);
        }
    }
    
    
}
