package com.xkball.tin_tea_tech.common.player;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.item.IHoloGlassPlugin;
import com.xkball.tin_tea_tech.common.item.armor.HoloGlass;
import com.xkball.tin_tea_tech.network.TTNetworkHandler;
import com.xkball.tin_tea_tech.network.packet.SyncGUIDataPacket;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;

public class PlayerData implements INBTSerializable<CompoundTag> {
    
    public boolean displayInventoryInGUI = true;
    
    public Int2IntMap hgPluginMap = new Int2IntLinkedOpenHashMap();
    
    public void setDisplayInventoryInGUI(boolean displayInventoryInGUI) {
        if(this.displayInventoryInGUI != displayInventoryInGUI){
            this.displayInventoryInGUI = displayInventoryInGUI;
            sync();
        }
    }
    
    public void modeChange(boolean enable,int mode){
        if (hgPluginMap.containsKey(mode)) {
            var old = hgPluginMap.get(mode);
            hgPluginMap.put(mode,enable?old+1:old-1);
        }
        else if(enable){
            hgPluginMap.put(mode,1);
        }
    }
    
    public boolean modeAvailable(int mode){
        return hgPluginMap.getOrDefault(mode,0) > 0;
    }
    
    public void loadHGPDataFromItem(ItemStack itemStack){
        if(itemStack.getItem() instanceof HoloGlass && itemStack.hasTag()){
            var tag = itemStack.getTag();
            assert tag != null;
            if(tag.contains("inner_container")){
                var container = new ItemStackHandler(9);
                container.deserializeNBT(tag.getCompound("inner_container"));
                for(int i=0;i<9;i++){
                    var m = IHoloGlassPlugin.getMode(container.getStackInSlot(i));
                    if(m!=-1){
                        this.modeChange(true,m);
                    }
                }
            }
            
        }
    }
    
    @Override
    public CompoundTag serializeNBT() {
        var result = new CompoundTag();
        result.putBoolean("displayInventoryInGUI",displayInventoryInGUI);
        var list = new ListTag();
        hgPluginMap.int2IntEntrySet().forEach((entry) -> {
            var tag = new CompoundTag();
            tag.putInt("mode",entry.getIntKey());
            tag.putInt("value",entry.getIntValue());
            list.add(tag);
        });
        result.put("modeData",list);
        return result;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("displayInventoryInGUI"))
            displayInventoryInGUI = nbt.getBoolean("displayInventoryInGUI");
        if(nbt.contains("modeData")){
            var list = nbt.getList("modeData",10);
            for(int i=0;i<list.size();i++){
                var tag = list.getCompound(i);
                hgPluginMap.put(tag.getInt("mode"),tag.getInt("value"));
            }
        }
    }
    
    public static PlayerData get(Player player){
        return ((IExtendedPlayer)player).getPlayerData();
    }
    
    public static PlayerData get(){
        return get(Minecraft.getInstance().player);
    }
    
    public void sync(){
        if(TinTeaTech.isClient()){
            TTNetworkHandler.sentToServer(new SyncGUIDataPacket(this));
        }
    }
  
}
