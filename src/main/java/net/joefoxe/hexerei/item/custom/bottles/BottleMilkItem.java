package net.joefoxe.hexerei.item.custom.bottles;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.EffectCures;

public class BottleMilkItem extends HexBottleItem {

    public static FoodProperties FOOD = new FoodProperties.Builder().saturationModifier(1).nutrition(1).alwaysEdible().build();

    public BottleMilkItem(Properties properties) {
        super(properties.food(FOOD));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
        if (!world.isClientSide && entityLiving instanceof ServerPlayer player) {
            entityLiving.removeEffectsCuredBy(EffectCures.MILK);
        }
        return super.finishUsingItem(stack, world, entityLiving);
    }

    public Component getTooltip() {
        return Component.translatable("tooltip.hexerei.bottle_milk_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999)));
    }

}
