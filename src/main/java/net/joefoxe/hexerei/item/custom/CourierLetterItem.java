package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.ClientboundOpenCourierLetterScreenPacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class CourierLetterItem extends BlockItem {

    public CourierLetterItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        return super.place(context);
    }


    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        // open message menu

        if (!pContext.isSecondaryUseActive()) {
            if (pContext.getPlayer() instanceof ServerPlayer player) {
                if (!pContext.getLevel().isClientSide) {
                    if (!pContext.getPlayer().isSteppingCarefully() && pContext.getItemInHand().getCount() == 1) {
                        int slotIndex = pContext.getHand() == InteractionHand.OFF_HAND ? -1 : pContext.getPlayer().getInventory().selected;
                        HexereiPacketHandler.sendToPlayerClient(new ClientboundOpenCourierLetterScreenPacket(slotIndex, pContext.getHand()), player);;
                    }
                }
            }
        }

        return pContext.isSecondaryUseActive() ? super.useOn(pContext) : InteractionResult.CONSUME;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);

        if (playerIn instanceof ServerPlayer player) {
            if (!playerIn.isSteppingCarefully() && itemstack.getCount() == 1) {
                int slotIndex = handIn == InteractionHand.OFF_HAND ? -1 : playerIn.getInventory().selected;
                HexereiPacketHandler.sendToPlayerClient(new ClientboundOpenCourierLetterScreenPacket(slotIndex, handIn), player);
            }
        }
        return itemstack.getCount() == 1 ? (isSealed(itemstack) ? InteractionResultHolder.fail(itemstack) : InteractionResultHolder.consume(itemstack)) : InteractionResultHolder.fail(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);

            if(!(data != null && data.copyTag().contains("Message"))) {
                // say how to open the menu and seal
                tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_letter_use").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_letter_menu").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_letter_send").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_letter_must_be_sealed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else
            {
                if(isSealed(stack)) {
                    // say how to deliver or how to open
                    tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_letter_send").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_letter_open").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                } else {
                    // must be sealed
                    tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_letter_must_be_sealed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
            }
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }
    }

    public static boolean isSealed(ItemStack stack) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        return data != null && data.copyTag().contains("Sealed") && data.copyTag().getBoolean("Sealed");
    }

}