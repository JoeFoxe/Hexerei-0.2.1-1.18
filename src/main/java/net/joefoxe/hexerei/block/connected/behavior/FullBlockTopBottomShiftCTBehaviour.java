package net.joefoxe.hexerei.block.connected.behavior;


import net.joefoxe.hexerei.block.connected.BlockConnectivity;
import net.joefoxe.hexerei.block.connected.CTSpriteShiftEntry;
import net.joefoxe.hexerei.block.connected.ConnectedTextureBehaviour;
import net.joefoxe.hexerei.block.connected.ConnectedTextureTransparentLayer;
import net.joefoxe.hexerei.util.ClientProxy;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FullBlockTopBottomShiftCTBehaviour extends ConnectedTextureBehaviour.Base implements ConnectedTextureTransparentLayer {


    protected CTSpriteShiftEntry topShift;
    protected CTSpriteShiftEntry layerShift;
    protected CTSpriteShiftEntry topShiftTransparent;
    protected CTSpriteShiftEntry layerShiftTransparent;


    public FullBlockTopBottomShiftCTBehaviour(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry layerShiftTransparent, CTSpriteShiftEntry topShift, CTSpriteShiftEntry topShiftTransparent) {
        this.layerShift = layerShift;
        this.layerShiftTransparent = layerShiftTransparent;
        this.topShift = topShift;
        this.topShiftTransparent = topShiftTransparent;
    }
    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos,
                              Direction face) {
        if (isBeingBlocked(state, reader, pos, otherPos, face))
            return false;
        BlockConnectivity cc = ClientProxy.BLOCK_CONNECTIVITY;
        BlockConnectivity.Entry entry = cc.get(state);
        BlockConnectivity.Entry otherEntry = cc.get(other);
        if (entry == null || otherEntry == null)
            return false;
        if (!entry.isSideValid(state, face) || !otherEntry.isSideValid(other, face))
            return false;
        if (entry.getCTSpriteShiftEntry() != otherEntry.getCTSpriteShiftEntry())
            return false;
        return state.getBlock() == other.getBlock();
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        return direction.getAxis()
                .isHorizontal() ? layerShift : topShift;
    }

    @Override
    public @Nullable CTSpriteShiftEntry getTransparentShift(BlockState state, Direction direction, @NotNull TextureAtlasSprite sprite) {
        return direction.getAxis()
                .isHorizontal() ? layerShiftTransparent : topShiftTransparent;
    }
}