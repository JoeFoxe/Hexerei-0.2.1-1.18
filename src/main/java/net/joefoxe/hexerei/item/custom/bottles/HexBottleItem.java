package net.joefoxe.hexerei.item.custom.bottles;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class HexBottleItem extends Item {

    public HexBottleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
        if (!world.isClientSide && entityLiving instanceof ServerPlayer player) {
            ItemStack itemstack3 = new ItemStack(Items.GLASS_BOTTLE);
            ItemStack itemstack = entityLiving.getItemInHand(InteractionHand.MAIN_HAND);
            if (itemstack.isEmpty()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, itemstack3);
            } else if (!player.getInventory().add(itemstack3)) {
                player.drop(itemstack3, false);
            } else {
                player.initMenu(player.containerMenu);
            }
        }

        return super.finishUsingItem(stack, world, entityLiving);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    public Component getTooltip() {
        return Component.translatable("");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(getTooltip());
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

}
