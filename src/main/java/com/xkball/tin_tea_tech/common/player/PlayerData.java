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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;

public class PlayerData implements INBTSerializable<CompoundTag> {
    
    public boolean displayInventoryInGUI = true;
    
    public boolean displayNBT = true;
    
    public boolean controlled = false;
    
    public boolean buildingMode = false;
    
    public boolean allowRidingByPlayer = false;
    
    public float leftImpulse = 0f;
    public float forwardImpulse = 0f;
    
    public Int2IntMap hgPluginMap = new Int2IntLinkedOpenHashMap();
    
    private final Player owner;
    
    public PlayerData(Player owner) {
        this.owner = owner;
    }
    
    
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
        loadHGPDataFromItem(itemStack,false);
    }
    
    public void loadHGPDataFromItem(ItemStack itemStack,boolean isUnload){
        if(itemStack.getItem() instanceof HoloGlass && itemStack.hasTag()){
            var tag = itemStack.getTag();
            assert tag != null;
            if(tag.contains("inner_container")){
                var container = new ItemStackHandler(9);
                container.deserializeNBT(tag.getCompound("inner_container"));
                for(int i=0;i<9;i++){
                    var m = IHoloGlassPlugin.getMode(container.getStackInSlot(i));
                    if(m!=-1){
                        this.modeChange(!isUnload,m);
                    }
                }
            }
            
        }
    }
    
    public void setDisplayNBT(boolean displayNBT) {
        this.displayNBT = displayNBT;
        sync();
    }
    
    @Override
    public CompoundTag serializeNBT() {
        var result = new CompoundTag();
        result.putBoolean("displayInventoryInGUI",displayInventoryInGUI);
        result.putBoolean("displayNBT",displayNBT);
        result.putBoolean("controlled",controlled);
        result.putBoolean("buildingMode",buildingMode);
        result.putBoolean("allowRidingByPlayer",allowRidingByPlayer);
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
        if(nbt.contains("displayNBT")){
            displayNBT = nbt.getBoolean("displayNBT");
        }
        if(nbt.contains("controlled")){
            controlled = nbt.getBoolean("controlled");
        }
        if(nbt.contains("buildingMode")){
            buildingMode = nbt.getBoolean("buildingMode");
        }
        if(nbt.contains("allowRidingByPlayer")){
            allowRidingByPlayer = nbt.getBoolean("allowRidingByPlayer");
        }
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
    
    @OnlyIn(Dist.CLIENT)
    public static PlayerData get(){
        return get(Minecraft.getInstance().player);
    }
    
    public void sync(){
        if(TinTeaTech.isClient()){
            TTNetworkHandler.sentToServer(new SyncGUIDataPacket(this));
        }
    }
    
    
    public void updateDateFromItem(ItemStack from,ItemStack to){
        if(from.equals(to,false)) return;
        if(from.getItem() instanceof HoloGlass){
            loadHGPDataFromItem(from,true);
        }
        if(to.getItem() instanceof HoloGlass){
            loadHGPDataFromItem(to,false);
        }
        if(!owner.isLocalPlayer()){
            TTNetworkHandler.sentToClientPlayer(new SyncGUIDataPacket(this),owner);
        }
    }
  
}
