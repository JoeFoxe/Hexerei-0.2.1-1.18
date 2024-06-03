package net.joefoxe.hexerei.block.connected.behavior;


import net.joefoxe.hexerei.block.connected.CTSpriteShiftEntry;
import net.joefoxe.hexerei.block.connected.ConnectedTextureBehaviour;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CarpetCTBehaviour extends ConnectedTextureBehaviour.Base {

    protected CTSpriteShiftEntry topShift;
    protected CTSpriteShiftEntry layerShift;

    public CarpetCTBehaviour(CTSpriteShiftEntry layerShift) {
        this(layerShift, null);
    }



    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos,
                              Direction face) {
        if (state.hasProperty(ConnectingCarpetDyed.COLOR) && other.hasProperty(ConnectingCarpetDyed.COLOR))
            if (state.getValue(ConnectingCarpetDyed.COLOR) != other.getValue(ConnectingCarpetDyed.COLOR))
                return false;
        return state.getBlock() == other.getBlock() && pos.getY() == otherPos.getY();
    }

    public CarpetCTBehaviour(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry topShift) {
        this.layerShift = layerShift;
        this.topShift = topShift;
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        return direction.getAxis()
                .isHorizontal() ? layerShift : topShift;
    }

}