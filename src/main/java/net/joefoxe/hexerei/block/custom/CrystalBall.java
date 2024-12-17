package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.tileentity.CrystalBallTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
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

public class CrystalBall extends BaseEntityBlock implements ITileEntity<CrystalBallTile>, EntityBlock, SimpleWaterloggedBlock {


    public static final IntegerProperty ANGLE = IntegerProperty.create("angle", 0, 180);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(ANGLE, 0).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    public static final VoxelShape SHAPE = Stream.of(
            Block.box(5.5, 0, 5.5, 10.5, 2, 10.5),
            Block.box(6.5, 2, 6.5, 9.5, 4, 9.5),
            Block.box(4, 4, 4, 12, 12, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();


    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_){
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        BlockEntity tileEntity = level.getBlockEntity(pos);

        if(player.getItemInHand(handIn).isEmpty()) {

            if (level.isClientSide) {
//                if (tileEntity instanceof CrystalBallTile crystalBallTile) {
//                    crystalBallTile.centerYawIncrement = Mth.clamp(crystalBallTile.centerYawIncrement + (crystalBallTile.centerYawIncrement > 0 ? 1 : -1) + (crystalBallTile.centerYawIncrement / 10), -100, 100);
//                    crystalBallTile.lastInteractedWith = level.getGameTime();
//                }
            } else {
                level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
                level.blockEvent(pos, state.getBlock(), 1, 0);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

//    @SuppressWarnings("deprecation")
//    @Override
//    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
//        ItemStack itemstack = player.getItemInHand(handIn);
//        if(!level.isClientSide()) {
//
//            BlockEntity tileEntity = level.getBlockEntity(pos);
//
//            if(tileEntity instanceof CofferTile) {
//                MenuProvider containerProvider = createContainerProvider(level, pos);
//
//                NetworkHooks.openGui(((ServerPlayer)player), containerProvider, tileEntity.getPos());
//
//            } else {
//                throw new IllegalStateException("Our Container provider is missing!");
//            }
//        }
//        return InteractionResult.SUCCESS;
//    }

    public CrystalBall(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.crystal_ball_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//            tooltip.add(Component.translatable("tooltip.hexerei.crystal_ball"));
        }
        super.appendHoverText(stack, world, tooltip, flagIn);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, ANGLE, WATERLOGGED);
    }

    public void setAngle(Level level, BlockPos pos, BlockState state, int angle) {
        level.setBlock(pos, state.setValue(ANGLE, Mth.clamp(angle, 0, 180)), 2);
    }

    public int getAngle(Level level, BlockPos pos) {
        return level.getBlockState(pos).getValue(ANGLE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, pos, facingPos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }


//    @Override
//    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
//        super.onBlockExploded(state, world, pos, explosion);
//
//        if (world instanceof ServerLevel) {
//            ItemStack cloneItemStack = getItem(world, pos, state);
//            if (world.getBlockState(pos) != state && !level.isClientSide()) {
//                world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, cloneItemStack));
//            }
//
//        }
//    }

//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
//
//        //world.addParticle(ParticleTypes.ENCHANT, pos.getX() + Math.round(rand.nextDouble()), pos.getY() + 1.2d, pos.getZ() + Math.round(rand.nextDouble()) , (rand.nextDouble() - 0.5d) / 50d, (rand.nextDouble() + 0.5d) * 0.035d ,(rand.nextDouble() - 0.5d) / 50d);
//
//        super.animateTick(state, world, pos, rand);
//    }
//
//    private MenuProvider createContainerProvider(Level level, BlockPos pos) {
//        return new MenuProvider() {
//            @Override
//            public Component getDisplayName() {
//                if(((CofferTile)level.getBlockEntity(pos)).customName != null)
//                    return Component.translatable(((CofferTile)level.getBlockEntity(pos)).customName.getString());
//                return Component.translatable("screen.hexerei.coffer");
//            }
//
//            @Nullable
//            @Override
//            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
//                return new CofferContainer(i, level, pos, playerInventory, playerEntity);
//            }
//        };
//    }
//

//    @Override
    public boolean hasBlockEntity(BlockState state) {
        return true;
    }

    @Override
    public Class<CrystalBallTile> getTileEntityClass() {
        return CrystalBallTile.class;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrystalBallTile(ModTileEntities.CRYSTAL_BALL_TILE.get(), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType){
        return entityType == ModTileEntities.CRYSTAL_BALL_TILE.get() ?
                (world2, pos, state2, entity) -> ((CrystalBallTile)entity).tick() : null;
    }
}
