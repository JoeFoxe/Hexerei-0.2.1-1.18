package net.joefoxe.hexerei.client.renderer.color;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.*;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;


@EventBusSubscriber(value = Dist.CLIENT)
public class ModItemColors {
    private ModItemColors() {}
    // FORGE: Use RegistryDelegates as non-Vanilla item crowList are not constant

    @SubscribeEvent
    public static void initItemColors(RegisterColorHandlersEvent.Item event) {
        event.getItemColors().register((stack, color) -> {
            DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
            return color == 0 ? -1 : ((WitchArmorItem)stack.getItem()).getColor(stack);
        }, ModItems.WITCH_HELMET.get(), ModItems.WITCH_CHESTPLATE.get(), ModItems.WITCH_BOOTS.get());

        event.getItemColors().register((stack, color) -> {
            Block block = Block.byItem(stack.getItem());
            if(block instanceof WaterlilyBlock) {
                return GrassColor.get(0.0D, 0.5D);
            }
            return 0;
        }, ModBlocks.LILY_PAD_BLOCK.get());

        CofferItem.ItemHandlerConsumer items = event.getItemColors()::register;
        items.register((s, t) -> t == 1 ? CofferItem.getColorValue(CofferItem.getDyeColorNamed(s), s) : -1, ModItems.COFFER.get());

        items.register((s, t) -> t == 0 ? MixingCauldronItem.getColorValue(MixingCauldronItem.getDyeColorNamed(s), s) : -1, ModItems.MIXING_CAULDRON.get());

        items.register((s, t) -> t == 0 ? BroomSeatItem.getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, ModItems.BROOM_SEAT.get());

        items.register((s, t) -> t == 1 ? SatchelItem.getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, ModItems.SMALL_SATCHEL.get());
        items.register((s, t) -> t == 1 ? SatchelItem.getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, ModItems.MEDIUM_SATCHEL.get());
        items.register((s, t) -> t == 1 ? SatchelItem.getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, ModItems.LARGE_SATCHEL.get());


        items.register((s, t) -> t == 1 ? CandleItem.getColorValue(CandleItem.getDyeColorNamed(s), s) : -1, ModItems.CANDLE.get());

    }


}