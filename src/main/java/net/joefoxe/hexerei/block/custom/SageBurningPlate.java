package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.tileentity.SageBurningPlateTile;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class SageBurningPlate extends Block implements ITileEntity<SageBurningPlateTile>, EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final IntegerProperty MODE = IntegerProperty.create("mode", 0, 3);

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.MODEL;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER).setValue(LIT, false).setValue(MODE, 0);
    }

    // hitbox REMEMBER TO DO THIS
    public static final VoxelShape SHAPE = Block.box(2, 0, 5, 14, 1, 11);

    public static final VoxelShape SHAPE_TURNED = Block.box(5, 0, 2, 11, 1, 14);

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        if (p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST || p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST)
            return SHAPE_TURNED;
        return SHAPE;
    }

    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity tileEntity = level.getBlockEntity(pos);


        if (stack.is(ModItems.CROW_FLUTE.get()) && stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandMode() == 2) {
            stack.useOn(new UseOnContext(player, hand, hitResult));
            return ItemInteractionResult.SUCCESS;
        }

        ItemStack itemstack = player.getItemInHand(hand);
        Random random = new Random();
        if (tileEntity instanceof SageBurningPlateTile sageBurningPlateTile) {
            if (itemstack.getItem() == Items.FLINT_AND_STEEL) {
                if (sageBurningPlateTile.getItems().get(0).is(ModItems.DRIED_SAGE_BUNDLE.get()) && !state.getValue(LIT)) {

                    level.setBlock(pos, state.setValue(BlockStateProperties.LIT, true), 11);
                    level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
                    itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

                    return ItemInteractionResult.sidedSuccess(level.isClientSide());
                } else
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

            } else if(itemstack.isEmpty() && !player.isShiftKeyDown())
            {
                level.setBlock(pos, state.setValue(MODE, state.getValue(MODE) + 1 > 3 ? 0 : state.getValue(MODE) + 1), 11);
                state = level.getBlockState(pos);
                String s = "display.hexerei.sage_plate_toggle_" + state.getValue(MODE);
                player.displayClientMessage(Component.translatable(s), true);

            }
            else
                ((SageBurningPlateTile)tileEntity).interactSageBurningPlate(player, hitResult);
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }


    public SageBurningPlate(Properties properties) {
        super(properties.noOcclusion());
        this.withPropertiesOf(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, WATERLOGGED, LIT, MODE);
    }

//
//    @SuppressWarnings("deprecation")
//    @Override
//    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
//        return !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
//    }
//
//    @SuppressWarnings("deprecation")
//    @Override
//    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
//        return canSupportCenter(worldIn, pos.below(), Direction.UP);
//    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            MutableComponent string = Component.translatable(HexConfig.SAGE_BURNING_PLATE_RANGE.get() + "").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)));
            MutableComponent itemText = Component.translatable(ModItems.DRIED_SAGE_BUNDLE.get().getDescriptionId()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x998800)));

            tooltipComponents.add(Component.translatable("tooltip.hexerei.sage_burning_plate_shift_1", itemText).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.sage_burning_plate_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.sage_burning_plate_shift_3", string).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.sage_burning_plate_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.sage_burning_plate_shift_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.sage_burning_plate_shift_6").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//            tooltip.add(Component.translatable("tooltip.hexerei.sage_burning_plate").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity != null) {
                SageBurningPlateTile te = (SageBurningPlateTile) level.getBlockEntity(pos);

                if(!te.getItems().get(0).isEmpty())
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, te.getItems().get(0)));
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public Class<SageBurningPlateTile> getTileEntityClass() {
        return SageBurningPlateTile.class;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SageBurningPlateTile(ModTileEntities.SAGE_BURNING_PLATE_TILE.get(), pos, state);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType){
        return entityType == ModTileEntities.SAGE_BURNING_PLATE_TILE.get() ?
                (world2, pos, state2, entity) -> ((SageBurningPlateTile)entity).tick() : null;
    }
}
