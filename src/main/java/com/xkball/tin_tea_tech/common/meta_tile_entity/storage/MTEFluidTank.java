package com.xkball.tin_tea_tech.common.meta_tile_entity.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.tin_tea_tech.TinTeaTech;
import com.xkball.tin_tea_tech.api.TTValue;
import com.xkball.tin_tea_tech.api.annotation.AutomaticRegistration;
import com.xkball.tin_tea_tech.api.annotation.CreativeTag;
import com.xkball.tin_tea_tech.api.annotation.I18N;
import com.xkball.tin_tea_tech.api.annotation.Model;
import com.xkball.tin_tea_tech.api.mte.cover.Cover;
import com.xkball.tin_tea_tech.client.shape.Box3D;
import com.xkball.tin_tea_tech.client.shape.Point3D;
import com.xkball.tin_tea_tech.common.meta_tile_entity.MetaTileEntity;
import com.xkball.tin_tea_tech.common.tile_entity.TTTileEntityBase;
import com.xkball.tin_tea_tech.registration.TTCreativeTab;
import com.xkball.tin_tea_tech.utils.DataUtils;
import com.xkball.tin_tea_tech.utils.LevelUtils;
import com.xkball.tin_tea_tech.utils.RenderUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@CreativeTag(tab = TTCreativeTab.TTMachineTab.class)
@AutomaticRegistration
@AutomaticRegistration.MTE
@Model(resources = {"tin_tea_tech:block/fluid_tank"})
@I18N(chinese = "流体储罐",english = "FluidTank")
public class MTEFluidTank extends MetaTileEntity {
    
    private static final ResourceLocation MODEL = TinTeaTech.ttResource("block/fluid_tank");
    private static final VoxelShape SHAPE = Block.box(2,0,2,14,16,14);
    private static final Point3D startPoint = new Point3D((1d/16d)*3,0.009,(1d/16d)*3);
    
    private static final Direction[] directions = new Direction[]{Direction.SOUTH,Direction.NORTH,Direction.EAST,Direction.WEST,Direction.DOWN,Direction.UP};
    
    //for render
    protected int filled = 0;
    protected String fluidName = "";
    
    public MTEFluidTank(@NotNull BlockPos pos, @Nullable TTTileEntityBase te) {
        super(pos, te);
    }
    
    @Override
    protected Supplier<IFluidHandler> getFluidHandlerSupplier() {
        return () -> new FluidTank(64000){
            @Override
            protected void onContentsChanged() {
                filled = getFluidAmount();
                syncRenderData();
                markDirty();
            }
        };
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderAdditional(TTTileEntityBase pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int light, int pPackedOverlay) {
        if(filled>0){
            var fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
            if(fluid != null){
                var p = IClientFluidTypeExtensions.of(fluid);
                var rl = p.getStillTexture();
                @SuppressWarnings("deprecation")
                var texture = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(rl);
                var h = (filled/64000d);
                
                var b = new Box3D(startPoint,new Point3D((1d/16d)*13,h,(1d/16d)*13));
                var l = RenderUtil.getLight(this);
                var buff = pBufferSource.getBuffer(RenderUtil.FluidRenderType.FLUID);
                
                for(var d : directions){
                    pPoseStack.pushPose();
                    RenderUtil.renderFace(pPoseStack.last().pose(),pPoseStack.last().normal(),buff,p.getTintColor(),l,b.toRender(d),d,texture);
                    pPoseStack.popPose();
                }
                
            }
        }
    }
    
    @Override
    public void syncRenderData() {
        super.syncRenderData();
        filled = fluidHandler.get().getFluidInTank(0).getAmount();
        this.sentCustomData(TTValue.DATA_UPDATE,(b) -> b.writeInt(filled));
        //不可能null
        var fluidNameNow = Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(fluidHandler.get().getFluidInTank(0).getFluid())).toString();
        if(!fluidName.equals(fluidNameNow)){
            fluidName = fluidNameNow;
            this.sentCustomData(TTValue.FLUID,(b) -> DataUtils.writeUTF8String(b,fluidName));
        }
    }
    
    @Override
    public void readCustomData(int id, ByteBuf byteBuf) {
        if(id == TTValue.DATA_UPDATE){
            filled = byteBuf.readInt();
        }
        if(id == TTValue.FLUID){
            fluidName = DataUtils.readUTF8String(byteBuf);
        }
        super.readCustomData(id, byteBuf);
    }
    
    @Override
    public boolean canApplyCover(Direction direction, Cover cover) {
        return direction.getAxis() == Direction.Axis.Y;
    }
    
    @Override
    public MetaTileEntity newMetaTileEntity(BlockPos pos, TTTileEntityBase te) {
        return new MTEFluidTank(pos,te);
    }
    
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public @Nullable ResourceLocation getItemModel() {
        return MODEL;
    }
    
    @Override
    public InteractionResult use(Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(LevelUtils.ioWithBlockFluid(getLevel(),getPos(),pPlayer,pPlayer.getItemInHand(pHand),pHit.getDirection())){
            return InteractionResult.SUCCESS;
        }
        return super.use(pPlayer, pHand, pHit);
    }
    
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    protected Supplier<BakedModel[]> getModels() {
        return () -> new BakedModel[]{
                getModel(MODEL)
        };
    }
}
