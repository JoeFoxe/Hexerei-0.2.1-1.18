package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.tileentity.BroomStandTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class BroomStand extends Block implements ITileEntity<BroomStandTile>, EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    VoxelShape shape_turned = Stream.of(
            Block.box(2, 0, 2, 14, 3, 14),
            Block.box(6.5, 3, 6.5, 9.5, 10, 9.5),
            Block.box(9.5, 10, 11, 12.5, 13, 14),
            Block.box(3.5, 10, 11, 6.5, 13, 14),
            Block.box(6.5, 10, 2, 9.5, 13, 14),
            Block.box(3.5, 10, 2, 6.5, 13, 5),
            Block.box(9.5, 10, 2, 12.5, 13, 5),
            Block.box(9.5, 13, 2, 12.5, 15, 5),
            Block.box(9.5, 13, 11, 12.5, 15, 14),
            Block.box(3.5, 13, 11, 6.5, 15, 14),
            Block.box(3.5, 13, 2, 6.5, 15, 5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    VoxelShape shape = Stream.of(
            Block.box(2, 0, 2, 14, 3, 14),
            Block.box(6.5, 3, 6.5, 9.5, 10, 9.5),
            Block.box(11, 10, 3.5, 14, 13, 6.5),
            Block.box(11, 10, 9.5, 14, 13, 12.5),
            Block.box(2, 10, 6.5, 14, 13, 9.5),
            Block.box(2, 10, 9.5, 5, 13, 12.5),
            Block.box(2, 10, 3.5, 5, 13, 6.5),
            Block.box(2, 13, 3.5, 5, 15, 6.5),
            Block.box(11, 13, 3.5, 14, 15, 6.5),
            Block.box(11, 13, 9.5, 14, 15, 12.5),
            Block.box(2, 13, 9.5, 5, 15, 12.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public BroomStand(Properties pProperties){
        super(pProperties);
        registerDefaultState(super.defaultBlockState()
                .setValue(WATERLOGGED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if(!pState.hasProperty(HorizontalDirectionalBlock.FACING))
            return shape;

        Direction dir = pState.getValue(HorizontalDirectionalBlock.FACING);

        if(dir == Direction.NORTH || dir == Direction.SOUTH)
            return shape;

        return shape_turned;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity tileEntity = level.getBlockEntity(pos);

        if(tileEntity instanceof BroomStandTile broomStand) {

            return broomStand.interact(player, InteractionHand.MAIN_HAND, false) == 1 ? InteractionResult.SUCCESS : InteractionResult.PASS;

        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity tileEntity = level.getBlockEntity(pos);

        if(tileEntity instanceof BroomStandTile broomStand) {

            if (stack.is(ModItems.CROW_FLUTE.get()) && stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandMode() == 2) {
                stack.useOn(new UseOnContext(player, hand, hitResult));
                return ItemInteractionResult.SUCCESS;
            }

            return broomStand.interact(player, hand, true) == 1 ? ItemInteractionResult.SUCCESS : super.useItemOn(stack, state, level, pos, player, hand, hitResult);

        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity instanceof BroomStandTile te) {
                if (!te.itemHandler.getStackInSlot(0).isEmpty())
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 1f, pos.getZ() + 0.5f, te.itemHandler.getStackInSlot(0)));
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, HorizontalDirectionalBlock.FACING);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return !state.getValue(WATERLOGGED);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.altar_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public Class<BroomStandTile> getTileEntityClass() {
        return BroomStandTile.class;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType){
        return entityType == ModTileEntities.BROOM_STAND_TILE.get() ?
                (world2, pos, state2, entity) -> ((BroomStandTile)entity).tick() : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BroomStandTile(ModTileEntities.BROOM_STAND_TILE.get(), pos, state);
    }
}
