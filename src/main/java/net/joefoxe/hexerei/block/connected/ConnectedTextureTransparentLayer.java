package net.joefoxe.hexerei.block.connected;


import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface ConnectedTextureTransparentLayer {

    @Nullable CTSpriteShiftEntry getTransparentShift(BlockState state, Direction direction,
                                                     @NotNull TextureAtlasSprite sprite);

}