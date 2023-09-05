package com.xkball.tin_tea_tech.common.meta_tile_entity.pipe;

import com.xkball.tin_tea_tech.api.annotation.*;
import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.api.pipe.Slice;
import com.xkball.tin_tea_tech.client.render.PipeRender;
import com.xkball.tin_tea_tech.common.blocks.te.PipeMTEBlock;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.robot.RobotData;
import com.xkball.tin_tea_tech.common.robot.RobotManager;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
@AutomaticRegistration
@AutomaticRegistration.MTE(block = PipeMTEBlock.class,renderer = "com.xkball.tin_tea_tech.client.render.PipeRender")
@Model(resources = {"tin_tea_tech:block/pipe_default"})
@I18N(chinese = "物流机器人管道",english = "Robot Pipe")
@Tag.Item({"pipe"})
public class MTERobotPipe extends MTEPipe{
    
   // EnumMap<Connections,BlockPos>
    
    public MTERobotPipe(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
    }
    
    @Override
    public MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te) {
        return new MTERobotPipe(pos,te);
    }
    
    @Override
    public int defaultColor() {
        return ColorUtils.getColor(77,78,81,255);
    }
    
    @Override
    public Class<? extends BlockEntityRenderer<?>> getRendererClass() {
        return PipeRender.class;
    }
    
    
    @Override
    public void firstTick() {
        super.firstTick();
        
    }
    
    public boolean isNode(){
        return !(connectionCount()==2);
    }
    
    @Override
    public void setConnection(Connections connection, boolean connected, boolean noticeNeighbor) {
        if(connected && (connectionCount(connection.getSlice())>= 2 || connectionCount()>=4)) return;
        super.setConnection(connection, connected, noticeNeighbor);
    }
    
    @Override
    public void setConnection(Connections connection) {
        if(!isConnected(connection) && (connectionCount(connection.getSlice())>= 2 || connectionCount()>=4)) return;
        super.setConnection(connection);
    }
    
    public int connectionCount(){
        var result = 0;
        for(Connections c : Connections.values()) {
            if (isConnected(c)) result++;
        }
        return result;
    }
    
    public int connectionCount(Slice slice){
        var result = 0;
        for(Connections c : slice.getAll()) {
            if (isConnected(c)) result++;
        }
        return result;
    }
    
    public RobotManager getRobotManager(){
        return RobotData.get(getLevel());
    }
}
