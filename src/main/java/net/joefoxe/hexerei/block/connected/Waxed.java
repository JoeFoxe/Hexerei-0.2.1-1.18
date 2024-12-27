package net.joefoxe.hexerei.block.connected;

import net.joefoxe.hexerei.item.custom.CleaningClothItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

import static net.joefoxe.hexerei.item.custom.WaxBlendItem.WAX_OFF_BY_BLOCK;
import static net.minecraft.world.level.block.RotatedPillarBlock.AXIS;

public interface Waxed {

    default BlockState getUnWaxed(BlockState state, UseOnContext context, ItemAbility itemAbility){
        boolean cloth = CleaningClothItem.CLOTH_WAX_OFF.equals(itemAbility);
        WAX_OFF_BY_BLOCK.get().get(state.getBlock());
        BlockState toReturn = state;

        if(cloth){
            toReturn = WAX_OFF_BY_BLOCK.get().get(state.getBlock()).defaultBlockState();

            if(state.hasProperty(AXIS) && toReturn.hasProperty(AXIS))
                toReturn = toReturn.setValue(AXIS, state.getValue(AXIS));
            context.getLevel().scheduleTick(context.getClickedPos(), (Block) this, 1);
        }

        return toReturn;
    }

}
