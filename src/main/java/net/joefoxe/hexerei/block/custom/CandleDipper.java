package net.joefoxe.hexerei.block.custom;

import com.mojang.serialization.MapCodec;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.tileentity.CandleDipperTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class CandleDipper extends BaseEntityBlock implements ITileEntity<CandleDipperTile>, EntityBlock, SimpleWaterloggedBlock {
    public static final MapCodec<CandleDipper> CODEC = simpleCodec(CandleDipper::new);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
            FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

            if (this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos()) && context.getLevel().getBlockState(context.getClickedPos().below()).getBlock() instanceof MixingCauldron) {
                return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER).setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection());
        }
        return null;
    }

    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState p_49862_) {


        super.destroy(worldIn, pos, p_49862_);
    }

    // hitbox REMEMBER TO DO THIS
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(14, 1, 4, 16, 6, 12),
            Block.box(13, -1, 3.5, 17, 1, 6.5),
            Block.box(13, -1, 9.5, 17, 1, 12.5),
            Block.box(-1, -1, 9.5, 3, 1, 12.5),
            Block.box(0, 1, 4, 2, 6, 12),
            Block.box(-1, -1, 3.5, 3, 1, 6.5),
            Block.box(2, -1, 2, 14, 0, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SHAPE_TURNED = Stream.of(
            Block.box(4, 1, 0, 12, 6, 2),
            Block.box(3.5, -1, -1, 6.5, 1, 3),
            Block.box(9.5, -1, -1, 12.5, 1, 3),
            Block.box(9.5, -1, 13, 12.5, 1, 17),
            Block.box(4, 1, 14, 12, 6, 16),
            Block.box(3.5, -1, 13, 6.5, 1, 17),
            Block.box(2, -1, 2, 14, 0, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();


    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return state.getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH || state.getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH ? (SHAPE) : (SHAPE_TURNED);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof CandleDipperTile candleDipperTile) {
            return candleDipperTile.interactWithItem(player);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof CandleDipperTile candleDipperTile) {
            return candleDipperTile.interactWithoutItem(player);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    public CandleDipper(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, WATERLOGGED);
    }

    public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        if (!state.getValue(BlockStateProperties.WATERLOGGED) && fluidStateIn.getType() == Fluids.WATER) {

            worldIn.setBlock(pos, state.setValue(WATERLOGGED, Boolean.TRUE), 3);
            worldIn.scheduleTick(pos, fluidStateIn.getType(), fluidStateIn.getType().getTickDelay(worldIn));
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity != null) {
                CandleDipperTile te = (CandleDipperTile) level.getBlockEntity(pos);

                if(!te.getItems().get(0).isEmpty())
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, te.getItems().get(0)));
                if(!te.getItems().get(1).isEmpty())
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, te.getItems().get(1)));
                if(!te.getItems().get(2).isEmpty())
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, te.getItems().get(2)));
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }


    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {

        return !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return (worldIn.getBlockState(pos.below()).getBlock() instanceof MixingCauldron);
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
            tooltipComponents.add(Component.translatable("tooltip.hexerei.candle_dipper_shift_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.candle_dipper_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
    }

    @Override
    public Class<CandleDipperTile> getTileEntityClass() {
        return CandleDipperTile.class;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CandleDipperTile(ModTileEntities.CANDLE_DIPPER_TILE.get(), pos, state);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType){
        return entityType == ModTileEntities.CANDLE_DIPPER_TILE.get() ?
                (world2, pos, state2, entity) -> ((CandleDipperTile)entity).tick() : null;
    }
}
