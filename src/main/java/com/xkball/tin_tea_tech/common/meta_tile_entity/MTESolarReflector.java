package com.xkball.tin_tea_tech.common.meta_tile_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.TTValue;
import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import io.netty.buffer.ByteBuf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
@AutomaticRegistration
@AutomaticRegistration.MTE
@Model(resources = {"tin_tea_tech:block/reflector","tin_tea_tech:block/iron_column"})
@I18N(chinese = "太阳能反射镜",english = "Solar Reflector")
public class MTESolarReflector extends MetaTileEntity{
    
    private static final ResourceLocation ITEM_MODEL = TinTeaTech.ttResource("block/reflector");
    private static final ResourceLocation BLOCK_MODEL = TinTeaTech.ttResource("block/iron_column");
    
    private static final VoxelShape SHAPE = Block.box(5,0,5,11,24,11);
    
    private int x;
    private int y;
    private int z;
    
    public MTESolarReflector(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
    }
    
    @Override
    public MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te) {
        return new MTESolarReflector(pos,te);
    }
    
    @Override
    public @Nullable ResourceLocation getItemModel() {
        return ITEM_MODEL;
    }
    
    @Override
    public InteractionResult useByWrench(Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        var item = pPlayer.getItemInHand(pHand);
        if(item.hasTag()){
            var tag = item.getTag();
            assert tag != null;
            if(tag.contains("x")) setX(tag.getInt("x"));
            if(tag.contains("y")) setY(tag.getInt("y"));
            if(tag.contains("z")) setZ(tag.getInt("z"));
            markDirty();
            syncRenderData();
        }
        return super.useByWrench(pPlayer, pHand, pHit);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderAdditional(TTTileEntityBase pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int light, int pPackedOverlay) {
        var x1 = x;
        var y1 = y;
        var z1 = z;
        var level = getLevel();
        
        var pos = pBlockEntity.getBlockPos();
        var x2 = pos.getX();
        var y2 = pos.getY();
        var z2 = pos.getZ();
        
        float dx = x1-x2;
        float dy = y1-y2;
        float dz = z1-z2;
        
        var toBlock = new Vec3(dx,dy,dz).normalize();
        double time = Math.toRadians(((level.getDayTime()%24000)/24000D)*360D);
        var toSky = new Vec3(Math.cos(time),Math.sin(time),0).normalize();
        var ro = toSky.add(toBlock).normalize();
        //var ro = toSky;
        //var rr = new Quaternionf(ro.x(),ro.y(),ro.z,1f).normalize();
        
        dx = (float) ro.x(); dy = (float) ro.y(); dz = (float) ro.z();
        
        double fy = dx/dz;
        double dis = Math.sqrt(dx*dx+dz*dz);
        double fx = dy/dis;
        
        pPoseStack.pushPose();
        pPoseStack.translate(0.5,0.7,-0.5);
        pPoseStack.scale(2F,2F,2F);
        pPoseStack.translate(-0.5F,0f,0f);
        
        
        if(x1 != 0 && y1 !=0 && z1 != 0){
            //pPoseStack.rotateAround(rr,0.5f,0.5f,0.5f);
            if(dz<0){
                pPoseStack.rotateAround(Axis.YP.rotation((float) Math.atan(fy)),0.5F,0.5F,0.5F);
            }
            else {
                pPoseStack.rotateAround(Axis.YP.rotation((float) (Math.atan(fy)+Math.toRadians(180))),0.5F,0.5F,0.5F);
            }
            pPoseStack.rotateAround(Axis.XP.rotation((float) Math.atan(fx)),0.5F,0.5F,0.5F);
        }
        //var l = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, getPos().above()), level.getBrightness(LightLayer.SKY, getPos().above()));
        var model = getModel(ITEM_MODEL);
        var render = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        var blockColor = Minecraft.getInstance().getBlockColors();
        var blockState = getBlockState();
        
        int i = blockColor.getColor(blockState, null, null, 0);
        float r = (float)(i >> 16 & 255) / 255.0F;
        float g = (float)(i >>  8 & 255) / 255.0F;
        float b = (float)(i       & 255) / 255.0F;
        for (net.minecraft.client.renderer.RenderType rt :
                model.getRenderTypes(blockState, RandomSource.create(42), ModelData.EMPTY)){
            render.renderModel(pPoseStack.last(),
                    pBufferSource.getBuffer( RenderTypeHelper.getEntityRenderType(rt, false)),
                    blockState, model, r, g, b, light, pPackedOverlay,ModelData.EMPTY, rt);
        }
        // Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemTransforms.TransformType.FIXED,pPackedLight,pPackedOverlay,pPoseStack,pBufferSource,1);
        pPoseStack.popPose();
    }
    
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    protected Supplier<BakedModel[]> getModels() {
        return () -> new BakedModel[]{getModel(BLOCK_MODEL)};
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public int getLight() {
        return LightTexture.pack(getLevel().getBrightness(LightLayer.BLOCK, getPos().above(20)), getLevel().getBrightness(LightLayer.SKY, getPos().above(2)));
    }
    
    @Override
    public void syncRenderData() {
        super.syncRenderData();
        sentCustomData(TTValue.DATA_UPDATE,(b) ->{
            b.writeInt(x);
            b.writeInt(y);
            b.writeInt(z);
        });
    }
    
    @Override
    public void readCustomData(int id, ByteBuf byteBuf) {
        super.readCustomData(id, byteBuf);
        if(id == TTValue.DATA_UPDATE){
            x = byteBuf.readInt();
            y = byteBuf.readInt();
            z = byteBuf.readInt();
        }
    }
    
    @Override
    public void writeInitData(CompoundTag tag) {
        super.writeInitData(tag);
        tag.putInt("px",x);
        tag.putInt("py",y);
        tag.putInt("pz",z);
        
    }
    
    @Override
    public void readInitData(CompoundTag tag) {
        super.readInitData(tag);
        x = tag.getInt("px");
        y = tag.getInt("py");
        z = tag.getInt("pz");
    }
    
//    public BlockPos getPos(){
//        return new BlockPos(x,y,z);
//    }
    
    public void setZ(int z) {
        this.z = z;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setX(int x) {
        this.x = x;
    }
}
