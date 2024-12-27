package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotSavedData;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.tileentity.OwlCourierDepotTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
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

public class OwlCourierDepot extends Block implements ITileEntity<OwlCourierDepotTile>, EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    VoxelShape shape_turned = Stream.of(
            Block.box(3, 0, 3, 13, 2, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    VoxelShape shape = Stream.of(
            Block.box(3, 0, 3, 13, 2, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public OwlCourierDepot(Properties pProperties){
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        BlockEntity tileEntity = level.getBlockEntity(pos);

        if(tileEntity instanceof OwlCourierDepotTile odc) {

            if (stack.is(ModItems.CROW_FLUTE.get()) && stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandMode() == 2) {
                stack.useOn(new UseOnContext(player, hand, hitResult));
                return ItemInteractionResult.SUCCESS;
            }

            return odc.interact(player, hand);

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

            // remove from list of depot positions
            if (level instanceof ServerLevel serverLevel) {

                if (OwlCourierDepotSavedData.get().getDepots().containsKey(GlobalPos.of(level.dimension(), pos))) {
                    NonNullList<ItemStack> list = OwlCourierDepotSavedData.get().getDepots().get(GlobalPos.of(level.dimension(), pos)).items;

                    for (ItemStack stack : list)
                        if (!stack.isEmpty())
                            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 1f, pos.getZ() + 0.5f, stack.copy()));
                }

                OwlCourierDepotSavedData.get().clearOwlCourierDepot(GlobalPos.of(level.dimension(), pos));

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
    public Class<OwlCourierDepotTile> getTileEntityClass() {
        return OwlCourierDepotTile.class;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType){
        return entityType == ModTileEntities.OWL_COURIER_DEPOT_TILE.get() ?
                (world2, pos, state2, entity) -> ((OwlCourierDepotTile)entity).tick() : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OwlCourierDepotTile(ModTileEntities.OWL_COURIER_DEPOT_TILE.get(), pos, state);
    }
}
