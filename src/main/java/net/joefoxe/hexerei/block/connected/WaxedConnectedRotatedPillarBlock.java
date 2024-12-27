package net.joefoxe.hexerei.block.connected;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;

public class WaxedConnectedRotatedPillarBlock extends ConnectedPillarBlock implements Waxed {

	public WaxedConnectedRotatedPillarBlock(Properties p_55926_) {
		super(p_55926_);
	}

	@Nullable
	@Override
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		return getUnWaxed(state, context, itemAbility);
	}

}