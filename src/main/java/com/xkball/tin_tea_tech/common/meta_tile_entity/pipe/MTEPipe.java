package com.xkball.tin_tea_tech.common.meta_tile_entity.pipe;

import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.TTValue;
import com.xkball.tin_tea_tech.api.mte.ColorGetter;
import com.xkball.tin_tea_tech.api.mte.cover.Cover;
import com.xkball.tin_tea_tech.api.pipe.Connections;
import com.xkball.tin_tea_tech.common.item.TTCommonItem;
import com.xkball.tin_tea_tech.common.item_behaviour.ColorApplicatorBehaviour;
import com.xkball.tin_tea_tech.common.item_behaviour.TestItemBehaviour;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.utils.ColorUtils;
import com.xkball.tin_tea_tech.utils.ItemUtils;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import com.xkball.tin_tea_tech.utils.TTUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.BitSet;
import java.util.Collection;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MTEPipe extends MetaTileEntity implements ColorGetter {
    
    protected BitSet connections = new BitSet();
    
    protected int color;
    
    //在客户端才有意义 在服务端应该始终是0;
    protected int collision = 0;
    //依然是客户端数据
    public boolean b0 = false;
    public boolean b1 = false;
    public boolean b2 = false;
    public boolean b3 = false;
    public boolean b4 = false;
    
    public MTEPipe(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
        color = defaultColor();
    }
    
  
    
    @Override
    public void writeInitData(CompoundTag tag) {
        super.writeInitData(tag);
        tag.putInt("bitsetL",connections.length());
        tag.putLong("bitset", TTUtils.longValueOfBitSet(connections));
        tag.putInt("color",color);
    }
    
    @Override
    public void readInitData(CompoundTag tag) {
        super.readInitData(tag);
        var bl = tag.getInt("bitsetL");
        TTUtils.fromLongToBitSet(tag.getLong("bitset"),bl,connections);
        var c = tag.getInt("color");
        if(c != 0) color = c;
     
    }
    
  
    
    @Override
    public boolean canApplyCover(Direction direction, Cover cover) {
        return isConnected(Connections.fromDirection(direction));
    }
    
    public boolean isConnected(Connections connection){
        return connections.get(connection.toCID());
    }
    
    public boolean isNeighborConnected(Connections connection){
        var pipe = getPipeAt(getLevel(),getPos(connection),true);
        if(pipe != null){
            return pipe.isConnected(connection.getOpposite());
        }
        return false;
    }
    
    public boolean haveNeighbor(Connections connection){
        var pipe = getPipeAt(getLevel(),getPos(connection),true);
        return pipe != null;
    }
    
    @Override
    public int defaultColor() {
        return ColorUtils.getColor(255,185,55,255);
    }
    
 
    @Override
    public void firstTick() {
        var pos = getPos();
        if(TinTeaTech.lastPlace.containsKey(pos)){
            this.setConnection(TinTeaTech.lastPlace.get(pos),true,true);
            TinTeaTech.lastPlace.remove(pos);
        }
    }
    
    //blocked = 输入端
    public boolean isBlocked(Connections connection){
        return connections.get(connection.toBID());
    }
    
    @Override
    public InteractionResult useByWrench(Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        var con = LevelUtils.useConnection(pHit);
     //   pPlayer.sendSystemMessage(Component.literal(con.toString()));
        if(!pPlayer.isShiftKeyDown()){
            setConnection(con,!isConnected(con),true);
            return InteractionResult.SUCCESS;
        }
        else if(isConnected(con)){
            setBlocked(con);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    
    @Override
    public InteractionResult use(Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(TTCommonItem.getItemBehaviour(pPlayer.getItemInHand(pHand)) instanceof TestItemBehaviour){
            var con = LevelUtils.useConnection(pHit);
            setConnection(con);
            return InteractionResult.SUCCESS;
        }
        else if(ItemUtils.itemIs(pPlayer.getItemInHand(pHand), ColorApplicatorBehaviour.class)){
            var tag = pPlayer.getItemInHand(pHand).getOrCreateTag();
            if(tag.contains("color")){
                setColor(tag.getInt("color"));
            }
        }
        return InteractionResult.PASS;
    }
    
    public void updateConnection(){
        for(Connections connection : Connections.values()){
            if(haveNeighbor(connection)) this.setConnection(connection,isNeighborConnected(connection),false);
        }
    }
    
    public void updateConnection(Connections connection){
        this.setConnection(connection,isNeighborConnected(connection),false);
    }
    
    @Override
    public @Nullable BlockPlaceContext updateBlockPlaceContext(BlockPlaceContext context) {
        var originalPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        if(!LevelUtils.isSameMTE(this,context.getLevel(),originalPos)) return context;
        var con = LevelUtils.useConnection(context);
        var pos = getPos(originalPos,con);
        while (LevelUtils.isSameMTE(this,context.getLevel(),pos)){
            pos = getPos(pos,con);
        }
        if(context.getLevel().getBlockState(pos).canBeReplaced()){
            TinTeaTech.lastPlace.put(pos,con.getOpposite());
            return BlockPlaceContext.at(context,pos,context.getClickedFace());
        }
        return null;
    }
    
    public void setBlocked(Connections connection){
        connections.set(connection.toBID(),!isBlocked(connection));
        this.sentCustomData(TTValue.DATA_UPDATE,(b) -> {
            b.writeLong(TTUtils.longValueOfBitSet(connections));
            b.writeInt(connections.length());
        });
        markDirty();
    }
    
    public void setConnection(Connections connection,boolean connected,boolean noticeNeighbor){
        if(isConnected(connection) != connected){
            connections.set(connection.toCID(),connected);
            if(noticeNeighbor){
                var pipe = getPipeAt(connection,true);
                if(pipe!=null){
                    pipe.updateConnection(connection.getOpposite());
                }
            }
       
            coverHandler.checkCanMaintainCover();
            this.sentCustomData(TTValue.DATA_UPDATE,(b) -> {
                b.writeLong(TTUtils.longValueOfBitSet(connections));
                b.writeInt(connections.length());
            });
            markDirty();
        }
    }
    
    public void setConnection(Connections connection){
        connections.set(connection.toCID(),!isConnected(connection));
        coverHandler.checkCanMaintainCover();
        this.sentCustomData(TTValue.DATA_UPDATE,(b) -> {
            b.writeLong(TTUtils.longValueOfBitSet(connections));
            b.writeInt(connections.length());
        });
        markDirty();
    }
    
    @Override
    public void syncRenderData() {
        super.syncRenderData();
        this.sentCustomData(TTValue.DATA_UPDATE,(b) -> {
            b.writeLong(TTUtils.longValueOfBitSet(connections));
            b.writeInt(connections.length());
        });
        this.sentCustomData(TTValue.COLOR,(b) -> b.writeInt(color));
    }
    
    @Override
    public void readCustomData(int id, ByteBuf byteBuf) {
        super.readCustomData(id, byteBuf);
        if(id == TTValue.DATA_UPDATE){
            TTUtils.fromLongToBitSet(byteBuf.readLong(),byteBuf.readInt(),connections);
            var bit = new BitSet();
            for(var c : Connections.verticalConnections){
                if(isConnected(c)) bit.set(c.ordinal());
            }
            this.collision = TTUtils.intValueOfBitSet(bit);
            var mte = this;
            b0 = !mte.haveConnection();
            b1 = mte.haveConnection(Connections.verticalConnections);
            b2 = mte.haveConnection(Connections.XRoundConnections);
            b3 = mte.haveConnection(Connections.YRoundConnections);
            b4 = mte.haveConnection(Connections.ZRoundConnections);
        }
        if(id == TTValue.COLOR){
            this.color = byteBuf.readInt();
        }
    }
    
    @Override
    public @Nullable ResourceLocation getItemModel() {
        return TinTeaTech.ttResource("block/pipe_default");
    }
    
    @Override
    public void onNeighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        updateConnection();
    }
    

    
    public boolean havePipeNet(){
        return false;
    }
    
    public boolean haveConnection(Collection<Connections> connections){
        for(var c: connections){
            if(isConnected(c)) return true;
        }
        return false;
    }
    
    public boolean haveConnection(){
        return haveConnection(Connections.values);
    }
    
    public int getCollision() {
        return collision;
    }
    
    public BlockPos getPos(Connections connections){
        var v = connections.getRelativePos();
        return getPos().offset(v.getX(),v.getY(),v.getZ());
    }
    
    public BlockPos getPos(BlockPos blockPos,Connections connections){
        var v = connections.getRelativePos();
        return blockPos.offset(v.getX(),v.getY(),v.getZ());
    }
    
    public int getColor() {
        return color;
    }
    
    public void setColor(int color) {
        this.color = color;
        this.sentCustomData(TTValue.COLOR,(b) -> b.writeInt(color));
    }
    
    @Nullable
    public MTEPipe getPipeAt(@Nullable Level level,BlockPos pos,boolean requireSame){
        if(level!=null){
            var mte = LevelUtils.getMTE(level,pos);
            if(mte instanceof MTEPipe pipe){
                if(!requireSame) return pipe;
                if(pipe.getClass().equals(this.getClass())) return pipe;
            }
        }
        return null;
    }
    
    @Nullable
    public MTEPipe getPipeAt(Connections connections,boolean requireSame){
        return getPipeAt(getLevel(),getPos(connections),requireSame);
    }
    
    
  
}
