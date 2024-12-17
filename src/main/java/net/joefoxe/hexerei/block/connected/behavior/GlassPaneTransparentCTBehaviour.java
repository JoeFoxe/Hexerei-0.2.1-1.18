package net.joefoxe.hexerei.block.connected.behavior;

import net.joefoxe.hexerei.block.connected.CTSpriteShiftEntry;
import net.joefoxe.hexerei.block.connected.ConnectedTextureTransparentLayer;
import net.joefoxe.hexerei.block.connected.SimpleCTBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlassPaneTransparentCTBehaviour extends GlassPaneCTBehaviour implements ConnectedTextureTransparentLayer {

    protected CTSpriteShiftEntry transparentShift;
    public GlassPaneTransparentCTBehaviour(CTSpriteShiftEntry shift, CTSpriteShiftEntry transparentShift) {
        super(shift);
        this.transparentShift = transparentShift;
    }

    @Override
    public @Nullable CTSpriteShiftEntry getTransparentShift(BlockState state, Direction direction, @NotNull TextureAtlasSprite sprite) {
        return transparentShift;
    }
}