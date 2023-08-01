package com.xkball.tin_tea_tech.common.meta_tile_entity.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.TTValue;
import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.facing.FacingType;
import com.xkball.tin_tea_tech.api.capability.item.TTItemHandler;
import com.xkball.tin_tea_tech.api.mte.cover.Cover;
import com.xkball.tin_tea_tech.capability.item.DrawerItemHandler;
import com.xkball.tin_tea_tech.common.blocks.te.HorizontalMTEBlock;
import com.xkball.tin_tea_tech.common.blocks.te.TTTileEntityBlock;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.ItemUtils;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
@AutomaticRegistration
@AutomaticRegistration.MTE(block = HorizontalMTEBlock.class)
@Model(resources = {"tin_tea_tech:block/copper_drawer"})
@I18N(chinese = "铜抽屉",english = "Copper Drawer")
public class MTECopperDrawer extends MetaTileEntity {
    
    private static final ResourceLocation MODEL = TinTeaTech.ttResource("block/copper_drawer");
    
    private Item item = Items.AIR;
    public MTECopperDrawer(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
    }
    
    @Override
    public MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te) {
        return new MTECopperDrawer(pos,te);
    }
    
    @Override
    protected Supplier<TTItemHandler> getItemHandlerSupplier() {
        return () -> new DrawerItemHandler(8192){
            @Override
            protected void onContentsChanged() {
                markDirty();
                if(item != getItem()){
                    item = getItem();
                    syncRenderData();
                }
            }
            
            @Override
            protected void onLoad() {
                item = getItem();
                syncRenderData();
            }
        };
    }
    
    @Override
    public void syncRenderData() {
        super.syncRenderData();
        var s = ItemUtils.toString(item);
        sentCustomData(TTValue.DATA_UPDATE,(b) -> {
            b.writeInt(s.getBytes(StandardCharsets.UTF_8).length);
            b.writeCharSequence(s,StandardCharsets.UTF_8);
        });
    }
    
    @Override
    public void readCustomData(int id, ByteBuf byteBuf) {
        super.readCustomData(id, byteBuf);
        if(id == TTValue.DATA_UPDATE){
            var s = byteBuf.readCharSequence(byteBuf.readInt(),StandardCharsets.UTF_8);
            this.item = ItemUtils.fromString(s.toString());
        }
    }
    
    @Override
    protected Supplier<BakedModel[]> getModels() {
        return () -> new BakedModel[]{getModel(MODEL)};
    }
    
    @Override
    public InteractionResult use(Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(this.getFacingType(getBlockState(),pHit.getDirection()) == FacingType.MainFace){
            var item = pPlayer.getItemInHand(pHand);
            if(!item.isEmpty() && itemHandler.get().isItemValid(0,item)){
                var returnItem = itemHandler.get().insertItem(0,item.copy(),false);
                item.setCount(returnItem.getCount());
                return InteractionResult.SUCCESS;
            }
            else if(pPlayer.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()){
                LevelUtils.dropItem(pPlayer,getLevel(),getPos(),itemHandler
                        .get(),1);
                return InteractionResult.SUCCESS;
            }
        }
     
        return super.use(pPlayer, pHand, pHit);
    }
    
    @Override
    public InteractionResult useShift(Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(this.getFacingType(getBlockState(),pHit.getDirection()) == FacingType.MainFace){
            LevelUtils.dropItem(pPlayer,getLevel(),getPos(),itemHandler
                    .get());
            return InteractionResult.SUCCESS;
            
        }
        return super.useShift(pPlayer,pHand,pHit);
    }
    
    @Override
    public boolean canApplyCover(Direction direction, Cover cover) {
        return direction != getFacingDirection(FacingType.MainFace);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderAdditional(TTTileEntityBase pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int light, int pPackedOverlay) {
        if(item != Items.AIR){
            var blockState = getBlockState();
            var block = blockState.getBlock();
            if(block instanceof TTTileEntityBlock){
                pPoseStack.pushPose();
                ((TTTileEntityBlock) block).rotateBlockModel(blockState,this,pPoseStack);
                var d = blockState.getValue(HorizontalMTEBlock.horizontalFacing);
                var v = d.getNormal();
                if(d.getAxis() == Direction.Axis.Z){
                    pPoseStack.translate(v.getX()==0?0.5:0f,v.getY()==0?0.5:0f,v.getZ()==0?0.5:0f);
                }
                else {
                    pPoseStack.translate(0.5f,0.5f,0f);
                }
                pPoseStack.scale(0.8f,0.8f,0.8f);
                var itemRender = Minecraft.getInstance().getItemRenderer();
                var model = itemRender.getModel(item.getDefaultInstance(),null,null,42);
                itemRender.render(item.getDefaultInstance(), ItemDisplayContext.FIXED,false,pPoseStack,pBufferSource,light,pPackedOverlay,model);
                pPoseStack.popPose();
            }
        }
        
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public @Nullable ResourceLocation getItemModel() {
        return MODEL;
    }
}
