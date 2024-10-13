package com.xkball.tin_tea_tech.common.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xkball.tin_tea_tech.api.annotation.NonNullByDefault;
import com.xkball.tin_tea_tech.api.block.te.TTTileEntityBase;
import com.xkball.tin_tea_tech.common.mte.ticker.CreateMTETickerContext;
import com.xkball.tin_tea_tech.registry.TinTeaTechRegistries;
import com.xkball.tin_tea_tech.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@NonNullByDefault
public class TTTileEntityBlock extends BaseEntityBlock {
    
    private static final MapCodec<TTTileEntityBlock> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    propertiesCodec(),
                    TinTeaTechRegistries.MTE_TYPE_REGISTRY.byNameCodec().fieldOf("mte_type").forGetter(b -> ModUtils.getMTEType(b.mteLoc)))
                .apply(builder,(p,t) -> new TTTileEntityBlock(p,t.location)));
    
    public final ResourceLocation mteLoc;
    
    protected TTTileEntityBlock(Properties properties, ResourceLocation mteLoc) {
        super(properties);
        this.mteLoc = mteLoc;
    }
    
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        var result = new TTTileEntityBase(pos, state);
        var mteType = ModUtils.getMTEType(mteLoc);
        if (mteType != null) result.addMTE(mteType.newMetaTileEntity(pos,result));
        return result;
    }
    
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return new CreateMTETickerContext<>(mteLoc);
    }
}
