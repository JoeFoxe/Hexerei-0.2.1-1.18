package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.List;

import static net.joefoxe.hexerei.block.custom.ConnectingCarpetStairs.COLOR;
import static net.joefoxe.hexerei.item.custom.CleaningClothItem.getCleanedState;
import static net.joefoxe.hexerei.item.custom.WaxBlendItem.getWaxed;

public class WaxingKitItem extends Item {

    boolean isCreative;

    public WaxingKitItem(Properties pProperties, boolean isCreative) {
        super(pProperties);
        this.isCreative = isCreative;
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;// super.isBarVisible(pStack);
    }

    public int getBarColor(ItemStack pStack) {
        CustomData data = pStack.get(DataComponents.CUSTOM_DATA);
        if(this.isCreative)
            return 16733695; //ChatFormatting.LIGHT_PURPLE.getColor();
        if(data != null && data.contains("waxCount") && data.copyTag().getInt("waxCount") > 0) {
            float f = Math.max(0.0F, getBarWidth(pStack) / 13f);
            return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
        } else {
            return 0x353838;
        }
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        //256

        CustomData data = pStack.get(DataComponents.CUSTOM_DATA);
        if(data != null && data.contains("waxCount") && data.copyTag().getInt("waxCount") > 0) {
            if(data.contains("waxCount"))
                return (int)((data.copyTag().getInt("waxCount") / 256f) * 13);

            return 0;
        } else {
            return 13;
        }


//        CompoundTag tag = new CompoundTag();
//        if(pStack.hasTag())
//            tag = pStack.getOrCreateTag();
//
//        if(tag.contains("waxCount"))
//            return (int)((tag.getInt("waxCount") / 256f) * 13);
//
//        return 0;//super.getBarWidth(pStack);
//        return (int) (Hexerei.getClientTicks()/20) % 13;//super.getBarWidth(pStack);
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        Player player = pContext.getPlayer();
        ItemStack itemstack = pContext.getItemInHand();
        InteractionResult result = InteractionResult.PASS;

        CustomData data = itemstack.get(DataComponents.CUSTOM_DATA);
        if(this.isCreative || (data != null && data.contains("waxCount") && data.copyTag().getInt("waxCount") > 0)){
            result = getWaxed(blockstate).map((newBlockstate) -> {
                if (blockstate.hasProperty(ConnectingCarpetDyed.COLOR))
                    newBlockstate.setValue(ConnectingCarpetDyed.COLOR, blockstate.getValue(ConnectingCarpetDyed.COLOR));
                if (player instanceof ServerPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
                }

                if(!this.isCreative) {
                    CompoundTag tag = data.copyTag();
                    tag.putInt("waxCount", tag.getInt("waxCount") - 1);
                    itemstack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                }
                if(blockstate.getBlock() instanceof CrossCollisionBlock) {
                    BlockState changeTo = newBlockstate.setValue(CrossCollisionBlock.NORTH, blockstate.getValue(CrossCollisionBlock.NORTH)).setValue(CrossCollisionBlock.SOUTH, blockstate.getValue(CrossCollisionBlock.SOUTH)).setValue(CrossCollisionBlock.EAST, blockstate.getValue(CrossCollisionBlock.EAST)).setValue(CrossCollisionBlock.WEST, blockstate.getValue(CrossCollisionBlock.WEST)).setValue(CrossCollisionBlock.WATERLOGGED, blockstate.getValue(CrossCollisionBlock.WATERLOGGED));
                    if (blockstate.hasProperty(ConnectingCarpetDyed.COLOR))
                        changeTo.setValue(ConnectingCarpetDyed.COLOR, blockstate.getValue(ConnectingCarpetDyed.COLOR));
                    level.setBlockAndUpdate(blockpos, changeTo);
                }
                else
                    level.setBlock(blockpos, newBlockstate, 11);
                level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, newBlockstate));
                level.levelEvent(player, 3003, blockpos, 0);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }).orElse(InteractionResult.PASS);
        }

        if(result == InteractionResult.PASS) {

            BlockState cleanedState = getCleanedState(blockstate);
            if (cleanedState != null) {
                level.playSound(player, blockpos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3004, blockpos, 0);
                if (player instanceof ServerPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, blockpos, itemstack);
                }

                if (blockstate.hasProperty(COLOR))
                    cleanedState = cleanedState.trySetValue(COLOR, blockstate.getValue(COLOR));
                level.setBlock(blockpos, cleanedState, 11);
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

        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            Component wax_blend = Component.translatable(ModItems.WAX_BLEND.get().getDescription().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6B5B06)));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.waxing_kit", wax_blend).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            tooltipComponents.add(Component.translatable("tooltip.hexerei.waxing_kit_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }
        int count = 0;
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if(data != null && data.contains("waxCount"))
            count = data.copyTag().getInt("waxCount");

        if(!this.isCreative)
            tooltipComponents.add(Component.translatable("%s: " + count + " / 256", Component.translatable("tooltip.hexerei.wax").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x99AE99)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        else
            tooltipComponents.add(Component.translatable("tooltip.hexerei.infinite_wax").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x99AE99))));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}