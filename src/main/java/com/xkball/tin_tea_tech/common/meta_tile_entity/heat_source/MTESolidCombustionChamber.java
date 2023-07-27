package com.xkball.tin_tea_tech.common.meta_tile_entity.heat_source;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.TTValue;
import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.data.DataProvider;
import com.xkball.tin_tea_tech.api.item.TTItemHandler;
import com.xkball.tin_tea_tech.api.facing.FacingType;
import com.xkball.tin_tea_tech.capability.item.TTCommonItemHandler;
import com.xkball.tin_tea_tech.common.blocks.te.HorizontalMTEBlock;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
@AutomaticRegistration
@AutomaticRegistration.MTE(block = HorizontalMTEBlock.class)
@Model(resources = {"flamereaction:block/solid_fuel_burning_box_on","flamereaction:block/solid_fuel_burning_box"})
@I18N(chinese = "固态燃烧室",english = "Solid Combustion Chamber")
public class MTESolidCombustionChamber extends MTEHeatSource implements DataProvider {
    
    private static final ResourceLocation LIT = TinTeaTech.frcResource("block/solid_fuel_burning_box_on");
    private static final ResourceLocation OFF = TinTeaTech.frcResource("block/solid_fuel_burning_box");
    
    public MTESolidCombustionChamber(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
        heatProducedPT = 100;
    }
    
    @Override
    ResourceLocation getLitModel() {
        return LIT;
    }
    
    @Override
    ResourceLocation getOffModel() {
        return OFF;
    }
    
    @Override
    protected Supplier<TTItemHandler> getItemHandlerSupplier() {
        return () -> new TTCommonItemHandler(1){
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return ForgeHooks.getBurnTime(stack,null) > 0;
            }
            
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }
        };
    }
    
    @Override
    public void tick() {
        super.tick();
        if(enabled && timeLeft<5 && !itemHandler.get().isEmpty()){
            var itemStack = itemHandler.get().getStackInSlot(0);
            timeLeft += ( ForgeHooks.getBurnTime(itemStack,null)/5);
            itemStack.shrink(1);
            sentCustomData(TTValue.DATA_UPDATE,(b) -> b.writeInt(timeLeft));
            markDirty();
        }
        if(getOffsetTick() % 100 == 0 && heatSource.heatValue()<0){
            heatSource.reset(0,0);
        }
    }
    
    @Override
    public void syncRenderData() {
        sentCustomData(TTValue.ENABLED,(b) -> b.writeBoolean(enabled));
        sentCustomData(TTValue.DATA_UPDATE,(b) -> b.writeInt(timeLeft));
    }
    
    @Override
    public MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te) {
        return new MTESolidCombustionChamber(pos,te);
    }
    
    @Override
    public @Nullable ResourceLocation getItemModel() {
        return OFF;
    }
    
    @Override
    public void onFacingChanged(Direction direction,FacingType facingType, BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if(facingType == FacingType.MainFace){
            var neighborState = pLevel.getBlockState(pFromPos);
            //noinspection deprecation
            if(enabled && neighborState.isSolid() && Block.isFaceFull(neighborState.getShape(pLevel,pFromPos),direction)){
                enabled = false;
                sentCustomData(TTValue.ENABLED,(b) -> b.writeBoolean(false));
                sentCustomData(TTValue.DATA_UPDATE,(b) -> b.writeInt(timeLeft));
                markDirty();
            }
        }
    }
    
    @Override
    public InteractionResult use(Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        var item = pPlayer.getItemInHand(pHand);
        if(!enabled && item.is(Items.FLINT_AND_STEEL)){
            item.hurtAndBreak(1,pPlayer,(b) -> b.broadcastBreakEvent(pHand));
            this.enabled = true;
            sentCustomData(TTValue.ENABLED,(b) -> b.writeBoolean(true));
            sentCustomData(TTValue.DATA_UPDATE,(b) -> b.writeInt(timeLeft));
            markDirty();
            return InteractionResult.SUCCESS;
        }
        if(!item.isEmpty() && itemHandler.get().isItemValid(0,item)){
            var returnItem = itemHandler.get().insertItem(0,item.copy(),false);
            item.setCount(returnItem.getCount());
            return InteractionResult.SUCCESS;
        }
        //pPlayer.sendSystemMessage(Component.literal("?"));
        return InteractionResult.PASS;
    }
    
    @Override
    public InteractionResult useShift(Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        var item = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);
        if(item.isEmpty()){
            LevelUtils.dropItem(pPlayer,getLevel(),getPos(),itemHandler
                    .get());
        }
        //pPlayer.sendSystemMessage(Component.literal("¿"));
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public void readCustomData(int id, ByteBuf byteBuf) {
        super.readCustomData(id, byteBuf);
        if(id == TTValue.ENABLED){
            enabled = byteBuf.readBoolean();
        }
        if(id == TTValue.DATA_UPDATE){
            timeLeft = byteBuf.readInt();
        }
    }
    
    @Override
    public Collection<Component> getInfo() {
        var list = new ArrayList<Component>(4);
        list.add(Component.literal("enabled: " + enabled)
                .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED));
        list.add(Component.literal("time left: "+ timeLeft));
        list.add(Component.literal("heat increase pt: "+ heatProducedPT));
        list.add(Component.literal(heatSource.toString()));
        return list;
    }
}
