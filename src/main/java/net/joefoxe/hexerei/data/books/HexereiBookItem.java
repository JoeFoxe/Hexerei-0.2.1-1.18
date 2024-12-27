package net.joefoxe.hexerei.data.books;


import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.BookColorData;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.*;

import java.util.List;

public class HexereiBookItem extends Item {



    public HexereiBookItem(Properties properties) {
        super(properties.component(ModDataComponents.BOOK, BookData.EMPTY).component(ModDataComponents.BOOK_COLORS, BookColorData.EMPTY));
    }

    public static ItemStack withColors(int color1, int color2) {
        ItemStack stack = new ItemStack(ModItems.BOOK_OF_SHADOWS.get());
        stack.set(ModDataComponents.BOOK_COLORS, new BookColorData(color1, color2));

        return stack;
    }

    public static int getColor1(ItemStack stack) {

        BookColorData bookColorData = stack.getOrDefault(ModDataComponents.BOOK_COLORS, BookColorData.EMPTY);

        return bookColorData.color1();
    }


    public static int getColor2(ItemStack stack) {

        BookColorData bookColorData = stack.getOrDefault(ModDataComponents.BOOK_COLORS, BookColorData.EMPTY);

        return bookColorData.color2();
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.book_of_shadows_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}

