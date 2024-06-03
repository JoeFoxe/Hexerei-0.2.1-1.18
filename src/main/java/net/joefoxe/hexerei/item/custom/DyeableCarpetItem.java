package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

import static net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed.COLOR;

public class DyeableCarpetItem extends BlockItem {
    public DyeableCarpetItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }


    public interface ItemHandlerConsumer {
        void register(ItemColor handler, ItemLike... items);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List< Component > tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        tooltip.add(Component.translatable("tooltip.hexerei.can_be_dyed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        super.appendHoverText(stack, world, tooltip, flagIn);
    }



    @org.jetbrains.annotations.Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext pContext) {
        BlockState blockState = pContext.getLevel().getBlockState(pContext.getClickedPos().below());
        Block block = blockState.getBlock();

        if(block instanceof SlabBlock && blockState.hasProperty(BlockStateProperties.SLAB_TYPE) && blockState.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
            if (ModBlocks.INFUSED_FABRIC_CARPET_SLAB.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_SLAB.get().getStateForPlacement(pContext);
            if (ModBlocks.WAXED_INFUSED_FABRIC_CARPET_SLAB.get().parentBlock == this.getBlock())
                return ModBlocks.WAXED_INFUSED_FABRIC_CARPET_SLAB.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_SLAB.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_SLAB.get().getStateForPlacement(pContext);
        }
        if(block instanceof StairBlock && blockState.hasProperty(BlockStateProperties.HALF) && blockState.getValue(BlockStateProperties.HALF) == Half.BOTTOM) {
            if (ModBlocks.INFUSED_FABRIC_CARPET_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.WAXED_INFUSED_FABRIC_CARPET_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.WAXED_INFUSED_FABRIC_CARPET_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get().getStateForPlacement(pContext);
            if (ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get().parentBlock == this.getBlock())
                return ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get().getStateForPlacement(pContext);
        }
        return super.getPlacementState(pContext);
    }


    @Override
    public Component getName(ItemStack pStack) {
        DyeColor color = DyeColor.WHITE;

        if (pStack.hasTag() && pStack.getTag() != null && pStack.getTag().contains("color"))
            color = DyeColor.byName(pStack.getOrCreateTag().getString("color"), DyeColor.WHITE);
        if (color == DyeColor.WHITE)
            return super.getName(pStack);
        return Component.translatable("color.minecraft." + color.getName()).append(" ").append(super.getName(pStack));
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "hexerei", bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ColorRegisterHandler
    {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void registerCarpetColors(RegisterColorHandlersEvent.Item event)
        {
            ItemHandlerConsumer items = event.getItemColors()::register;
            // s = stack, t = tint-layer
            items.register((s, t) -> t == 0 ? ConnectingCarpetDyed.getColorValue(s) : -1,

                    ModItems.INFUSED_FABRIC_CARPET.get(),

                    ModItems.WAXED_INFUSED_FABRIC_CARPET.get(),

                    ModItems.INFUSED_FABRIC_BLOCK.get(),

                    ModItems.WAXED_INFUSED_FABRIC_BLOCK.get());
        }
    }


}
