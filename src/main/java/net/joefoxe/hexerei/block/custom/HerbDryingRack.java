package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.tileentity.DryingRackTile;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class HerbDryingRack extends Block implements ITileEntity<DryingRackTile>, EntityBlock, SimpleWaterloggedBlock {


    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    // hitbox REMEMBER TO DO THIS
    public static final VoxelShape SHAPE = Optional.of(Block.box(0.5, 5.5, 7.5, 15.5, 16, 8.5)).get();

    public static final VoxelShape SHAPE_TURNED = Optional.of(Block.box(7.5, 5.5, 0.5, 8.5, 16, 15.5)).get();


    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity tileEntity = level.getBlockEntity(pos);

        if (tileEntity instanceof DryingRackTile) {
            ((DryingRackTile)tileEntity).interactDryingRack(player, hitResult);
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

//    @Override
//    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
//        BlockEntity tileEntity = level.getBlockEntity(pos);
//
//        if (tileEntity instanceof DryingRackTile) {
//            ((DryingRackTile)tileEntity).interactWithoutItem(player, hitResult);
//            return InteractionResult.SUCCESS;
//        }
//        return super.useWithoutItem(state, level, pos, player, hitResult);
//    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_){
        return p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH || p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH ? SHAPE : SHAPE_TURNED;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_drying_rack_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//            tooltip.add(Component.translatable("tooltip.hexerei.herb_drying_rack"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public HerbDryingRack(Properties properties) {

        super(properties.noCollission());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, WATERLOGGED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof DryingRackTile te && te.getLevel() != null) {
                if (!te.getItems().get(0).isEmpty())
                    te.getLevel().addFreshEntity(new ItemEntity(te.getLevel(), pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, te.getItems().get(0)));
                if (!te.getItems().get(1).isEmpty())
                    te.getLevel().addFreshEntity(new ItemEntity(te.getLevel(), pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, te.getItems().get(1)));
                if (!te.getItems().get(2).isEmpty())
                    te.getLevel().addFreshEntity(new ItemEntity(te.getLevel(), pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, te.getItems().get(2)));
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }


        return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }


    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return canSupportCenter(worldIn, pos.above(), Direction.DOWN);
    }

    @Override
    public Class<DryingRackTile> getTileEntityClass() {
        return DryingRackTile.class;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DryingRackTile(ModTileEntities.DRYING_RACK_TILE.get(), pos, state);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType){
        return entityType == ModTileEntities.DRYING_RACK_TILE.get() ?
                (world2, pos, state2, entity) -> ((DryingRackTile)entity).tick() : null;
    }

}
