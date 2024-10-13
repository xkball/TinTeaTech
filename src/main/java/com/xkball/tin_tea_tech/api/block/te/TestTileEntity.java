package com.xkball.tin_tea_tech.api.block.te;

import com.xkball.tin_tea_tech.api.annotation.DataField;
import com.xkball.tin_tea_tech.api.annotation.DataSyncAdapter;
import com.xkball.tin_tea_tech.api.data.ISyncDataOfNBTTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@DataSyncAdapter(DataSyncAdapter.Type.BlockEntity)
public class TestTileEntity extends BlockEntity implements ISyncDataOfNBTTag {
    
    @DataField
    private ItemStack itemStack1 = ItemStack.EMPTY;
    
    @DataField(syncPolicy = DataField.SyncPolicy.Sync2Client)
    private ItemStack itemStack2 = ItemStack.EMPTY;
    
    @DataField(syncPolicy = DataField.SyncPolicy.SyncBoth)
    private ItemStack itemStack3 = ItemStack.EMPTY;
    
    public TestTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
}
