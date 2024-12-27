package net.joefoxe.hexerei.item.custom;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;
import java.util.List;

import static net.joefoxe.hexerei.block.custom.ConnectingCarpetStairs.COLOR;
import static net.joefoxe.hexerei.item.custom.WaxBlendItem.WAX_OFF_BY_BLOCK;

public class CleaningClothItem extends Item {
    public static final ItemAbility CLOTH_WAX_OFF = ItemAbility.get("cloth_wax_off");

    public CleaningClothItem(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        Player player = pContext.getPlayer();
        ItemStack itemstack = pContext.getItemInHand();

        BlockState cleanedState = getCleanedState(blockstate);
        if (cleanedState != null) {
            level.playSound(player, blockpos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, blockpos, 0);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, blockpos, itemstack);
            }

            if (blockstate.hasProperty(COLOR))
                cleanedState = cleanedState.trySetValue(COLOR, blockstate.getValue(COLOR));
            level.setBlockAndUpdate(blockpos, cleanedState);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, cleanedState));
            if (player != null) {
                itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(pContext.getHand()));
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            //add fail sound
            return InteractionResult.PASS;
        }
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, Property<T> property) {
        return to.setValue(property, from.getValue(property));
    }

    public static BlockState copyProperties(BlockState from, BlockState to) {
        for (Property<?> property : from.getProperties()) {
            if (to.hasProperty(property)) {
                to = copyProperty(from, to, property);
            }
        }
        return to;
    }


    public static BlockState getCleanedState(BlockState originalState) {
        if(WAX_OFF_BY_BLOCK.get().containsKey(originalState.getBlock())){
            Block block = WAX_OFF_BY_BLOCK.get().get(originalState.getBlock());
            BlockState toReturn = block.defaultBlockState();

            return copyProperties(originalState, toReturn);

//            if (block == null)
//                return null;
//            if (block.defaultBlockState().hasProperty(RotatedPillarBlock.AXIS))
//                return block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, originalState.getValue(RotatedPillarBlock.AXIS));
//            else if (block instanceof CrossCollisionBlock)
//                return block.defaultBlockState().setValue(CrossCollisionBlock.NORTH, originalState.getValue(CrossCollisionBlock.NORTH)).setValue(CrossCollisionBlock.SOUTH, originalState.getValue(CrossCollisionBlock.SOUTH)).setValue(CrossCollisionBlock.EAST, originalState.getValue(CrossCollisionBlock.EAST)).setValue(CrossCollisionBlock.WEST, originalState.getValue(CrossCollisionBlock.WEST)).setValue(CrossCollisionBlock.WATERLOGGED, originalState.getValue(CrossCollisionBlock.WATERLOGGED));
//            else
//                return block.defaultBlockState();
        } else {
            return null;
        }

    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.hexerei.cloth").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

}