package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;

import javax.annotation.Nullable;

public class MahoganyWood extends RotatedPillarBlock {
    public MahoganyWood(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        boolean rightClickedWithAxe = ItemAbilities.AXE_STRIP.equals(itemAbility);
        BlockState toReturn = ModBlocks.MAHOGANY_WOOD.get().defaultBlockState();

        if(rightClickedWithAxe){
            toReturn = ModBlocks.STRIPPED_MAHOGANY_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        }

        return toReturn;
    }
}

