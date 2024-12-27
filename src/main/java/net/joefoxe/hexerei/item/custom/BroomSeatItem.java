package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class BroomSeatItem extends BroomAttachmentItem {

    public BroomSeatItem(Properties properties) {
        super(properties);
    }

    public interface ItemHandlerConsumer {
        void register(ItemColor handler, ItemLike... items);
    }

    public static int getColorValue(DyeColor color, ItemStack stack) {
        int dyeCol = HexereiUtil.getDyeColor(stack, 0x563B24);
        if(color == null && dyeCol != -1)
            return dyeCol;
        return color.getTextureDiffuseColor();
    }


    public static DyeColor getDyeColorNamed(ItemStack stack) {
        return HexereiUtil.getDyeColorNamed(stack.getHoverName().getString(), 0);
    }

}
