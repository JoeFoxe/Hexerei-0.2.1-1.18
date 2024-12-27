package net.joefoxe.hexerei.block.connected;

import net.joefoxe.hexerei.item.custom.CleaningClothItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;

import static net.joefoxe.hexerei.item.custom.WaxBlendItem.WAX_OFF_BY_BLOCK;

public class WaxedGlassPaneBlock extends IronBarsBlock implements Waxed {

    public WaxedGlassPaneBlock(Properties p_55926_) {
        super(p_55926_);
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

    @Override
    public BlockState getUnWaxed(BlockState state, UseOnContext context, ItemAbility itemAbility){
        boolean cloth = CleaningClothItem.CLOTH_WAX_OFF.equals(itemAbility);
        WAX_OFF_BY_BLOCK.get().get(state.getBlock());
        BlockState toReturn = state;

        if(cloth){
            toReturn = WAX_OFF_BY_BLOCK.get().get(state.getBlock()).defaultBlockState().setValue(NORTH, state.getValue(NORTH)).setValue(SOUTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(EAST)).setValue(WEST, state.getValue(WEST)).setValue(WATERLOGGED, state.getValue(WATERLOGGED));
        }

        return toReturn;
    }
}

