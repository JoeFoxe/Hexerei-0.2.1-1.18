package net.joefoxe.hexerei.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;


import net.minecraft.world.item.Item.Properties;

public class SeedMixtureItem extends Item {

    public static FoodProperties FOOD = new FoodProperties.Builder().saturationMod(1).nutrition(2).build();

    public SeedMixtureItem(Properties properties) {
        super(properties.food(FOOD));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
//        if (!level.isClientSide && entityLiving instanceof ServerPlayer) {
//            ServerPlayer player = (ServerPlayer) entityLiving;
//            ItemStack itemstack3 = new ItemStack(Items.GLASS_BOTTLE);
//            ItemStack itemstack = ((ServerPlayer) entityLiving).getItemInHand(InteractionHand.MAIN_HAND);
//            entityLiving.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
//            if (itemstack.isEmpty()) {
//                player.setItemInHand(InteractionHand.MAIN_HAND, itemstack3);
//            } else if (!player.inventory.add(itemstack3)) {
//                player.drop(itemstack3, false);
//            } else if (player instanceof ServerPlayer) {
//                player.initMenu(player.containerMenu);
//            }
//        }

        return super.finishUsingItem(stack, world, entityLiving);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

        tooltip.add(Component.translatable("tooltip.hexerei.seed_mixture_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));


        super.appendHoverText(stack, world, tooltip, flagIn);
    }

}
