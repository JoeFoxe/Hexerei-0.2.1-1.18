package net.joefoxe.hexerei.block.connected;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;

public class WaxedGlassBlock extends TransparentBlock implements Waxed {

	public WaxedGlassBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		return super.skipRendering(state, adjacentBlockState, side);
	}

	@Nullable
	@Override
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		return getUnWaxed(state, context, itemAbility);
	}

}