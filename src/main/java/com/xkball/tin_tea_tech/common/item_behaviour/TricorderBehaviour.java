package com.xkball.tin_tea_tech.common.item_behaviour;

import com.xkball.tin_tea_tech.api.annotation.*;
import com.xkball.tin_tea_tech.api.data.DataProvider;
import com.xkball.tin_tea_tech.api.item.IHoloGlassPlugin;
import com.xkball.tin_tea_tech.api.item.IItemBehaviour;
import com.xkball.tin_tea_tech.capability.TTCapability;
import com.xkball.tin_tea_tech.common.player.IExtendedPlayer;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.AutoRegManager;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.Collection;

@AutomaticRegistration
@AutomaticRegistration.Item
@CreativeTag(tab = TTCreativeTab.TTMiscTab.class)
@I18N(chinese = "三录仪",english = "Tricorder")
@Model(resources = {"tin_tea_tech:item/tricoder"})
@Tag.Item({"tool","pipe"})
public class TricorderBehaviour implements IItemBehaviour, IHoloGlassPlugin {
    
    @Override
    public int stackSizeLimit() {
        return 1;
    }
    
    @Override
    public InteractionResult useOnBlock(UseOnContext pContext) {
        if(!pContext.getLevel().isClientSide && pContext.getPlayer() != null ) {
            var pos = pContext.getClickedPos();
            var info = scanBlock(pContext.getLevel(),pos, pContext.getPlayer(),true);
            info.forEach((c) -> pContext.getPlayer().sendSystemMessage(c));
        }
        return InteractionResult.SUCCESS;
        
    }
    
    public static Collection<Component> scanBlock(Level level, BlockPos pos, Player scanner, boolean recordTime){
        var state = level.getBlockState(pos);
        var te = level.getBlockEntity(pos);
        var info = DataProvider.blockInfo(state, pos);
        if(te != null){
            te.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(
                    (itemHandler) -> {
                        if(itemHandler.getSlots() != 0){
                            info.add(DataProvider.crossLine());
                            info.add(DataProvider.translatable("inventory").withStyle(ChatFormatting.BOLD));
                            for(int i=0;i<itemHandler.getSlots();i++){
                                info.add(DataProvider.translatable("slot")
                                        .append(Component.literal(i+" "))
                                        .append(DataProvider.translatable("item"))
                                        .append(Component.literal(itemHandler.getStackInSlot(i).toString())));
                            }
                        }
                    }
            );
            te.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(
                    (fluidHandler) ->{
                        if(fluidHandler.getTanks() != 0){
                            info.add(DataProvider.crossLine());
                            info.add(DataProvider.translatable("fluids").withStyle(ChatFormatting.BOLD));
                            for(int i=0;i<fluidHandler.getTanks();i++){
                                info.add(DataProvider.translatable("slot")
                                        .append(Component.literal(i+" "))
                                        .append(DataProvider.translatable("fluid"))
                                        .append(Component.literal(
                                                        fluidHandler.getFluidInTank(i).getFluid().getFluidType() +" "+fluidHandler.getFluidInTank(i).getAmount())));
                            }
                        }
                    }
            );
            te.getCapability(ForgeCapabilities.ENERGY).ifPresent(
                    (energyHandler) ->{
                        info.add(DataProvider.crossLine());
                        info.add(DataProvider.translatable("fe").withStyle(ChatFormatting.BOLD));
                        info.add(DataProvider.translatable("storage")
                                .append(Component.literal(energyHandler.getEnergyStored()+" "))
                                .append(DataProvider.translatable("max_storage"))
                                .append(Component.literal(String.valueOf(energyHandler.getMaxEnergyStored()))));
                        info.add(DataProvider.translatable("storage_percent").append(Component.literal(((double)energyHandler.getEnergyStored()/(double)energyHandler.getMaxEnergyStored())*100+"%")));
                    }
            );
            te.getCapability(TTCapability.STEAM).ifPresent(
                    (steamHandler) -> {
                        info.add(DataProvider.crossLine());
                        info.add(DataProvider.translatable("steam").withStyle(ChatFormatting.BOLD));
                        info.add(DataProvider.translatable("pressure")
                                .append(Component.literal(steamHandler.getPressure()+" kPa")));
                        info.add(DataProvider.translatable("volume")
                                .append(Component.literal(steamHandler.getVolume()+" L")));
                    }
            );
        }
        if (te instanceof TTTileEntityBase) {
            var mte = ((TTTileEntityBase) te).getMte();
            if(mte instanceof DataProvider){
                info.add(DataProvider.crossLine());
                info.add(DataProvider.translatable("mte_info").withStyle(ChatFormatting.BOLD));
                info.addAll(((DataProvider)mte).getInfo());
            }
            var ch = mte.getCoverHandler();
            var all = ch.allCovers();
            if(all.size() != 0){
                info.add(DataProvider.crossLine());
                info.add(DataProvider.translatable("cover_info").withStyle(ChatFormatting.BOLD));
                for(var c : all){
                    info.add(Component.literal("direction:"+ c.getDirection().toString()+" ,name:"+ AutoRegManager.fromClassName(c.getClass())));
                }
            }
            
            if(recordTime){
                mte.addTester(scanner);
            }
        }
        if(((IExtendedPlayer)scanner).getPlayerData().displayNBT){
            if(te != null){
                info.add(DataProvider.crossLine());
                info.add(Component.literal("Server Block looking at nbt: "));
                info.add(Component.literal(te.saveWithFullMetadata().toString()));
            }
            
            var item = scanner.getItemInHand(InteractionHand.MAIN_HAND);
            if(item.hasTag()){
                info.add(DataProvider.crossLine());
                info.add(Component.literal("Server Item in hand nbt: "));
                assert item.getTag() != null;
                info.add(Component.literal(item.getTag().toString()));
            }
        }
        return info;
    }
    
    @Override
    public int mode() {
        return 1;
    }
    
    @Override
    public Component buttonText() {
        return Component.literal("");
    }
}
