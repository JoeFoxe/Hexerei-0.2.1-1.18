package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.tileentity.CourierPackageTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed.COLOR;
import static net.minecraft.world.level.block.ShulkerBoxBlock.CONTENTS;

public class CourierPackage extends BaseEntityBlock implements ITileEntity<CourierPackageTile>, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);

    public enum State implements StringRepresentable {
        OPENED("opened"),
        CLOSED("closed"),
        SEALED("sealed");

        private final String name;

        private State(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getString() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
    VoxelShape shape_turned = Stream.of(
            Block.box(3, 0, 2, 13, 7, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    VoxelShape shape = Stream.of(
            Block.box(2, 0, 3, 14, 7, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public CourierPackage(Properties pProperties){
        super(pProperties);
        registerDefaultState(super.defaultBlockState()
                .setValue(WATERLOGGED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH).setValue(STATE, State.SEALED));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        if (pLevel.getBlockEntity(pPos) instanceof CourierPackageTile courierPackageTile) {
            return courierPackageTile.interact(pPlayer) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
//        pLevel.setBlock(pPos, pState.setValue(STATE, !pState.getValue(STATE)), 11);
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

//        withTileEntityDo(worldIn, pos, te -> {
//            if (stack.hasTag() && stack.getTag().contains("BlockEntityTag") && stack.getTag().getCompound("BlockEntityTag").contains("Items") && !stack.getTag().getCompound("BlockEntityTag").getList("Items", Tag.TAG_COMPOUND).isEmpty()) {
//                te.readInventory(stack.getTag().getCompound("BlockEntityTag"));
//            }
//            te.sync();
//        });
        super.setPlacedBy(worldIn, pos, state, placer, stack);

//        if (stack.hasCustomHoverName()) {
//            BlockEntity tileentity = worldIn.getBlockEntity(pos);
//            if(tileentity != null)
//                ((CofferTile)tileentity).customName = stack.getHoverName();
//        }

    }
    /**
     * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
     * this block
     */
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof CourierPackageTile packageTile) {
            if (!pLevel.isClientSide && pPlayer.isCreative() && !packageTile.isEmpty()) {
                ItemStack itemstack = ModItems.COURIER_PACKAGE.get().getDefaultInstance();
                blockentity.saveToItem(itemstack);
                if (packageTile.hasCustomName()) {
                    itemstack.setHoverName(packageTile.getCustomName());
                }

                ItemEntity itementity = new ItemEntity(pLevel, (double)pPos.getX() + 0.5D, (double)pPos.getY() + 0.5D, (double)pPos.getZ() + 0.5D, itemstack);
                itementity.setDefaultPickUpDelay();
                pLevel.addFreshEntity(itementity);
            } else {
                packageTile.unpackLootTable(pPlayer);
            }
        }

        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        return super.getDrops(pState, pParams);
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
        State state = State.OPENED;
        ItemStack stack = context.getItemInHand();
        CompoundTag tag = BlockItem.getBlockEntityData(stack);
        if (tag != null && tag.contains("Items") && !tag.getList("Items", Tag.TAG_COMPOUND).isEmpty()) {
            if (tag.contains("Sealed") && tag.getBoolean("Sealed"))
                state = State.SEALED;
            else
                state = State.CLOSED;
        }

        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER).setValue(STATE, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }


    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }
    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return canSupportCenter(worldIn, pos.below(), Direction.UP);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, HorizontalDirectionalBlock.FACING, STATE);
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
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

//        if(Screen.hasShiftDown()) {
//            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//            tooltip.add(Component.translatable("tooltip.hexerei.altar_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//        } else {
//            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//
//        }
        super.appendHoverText(stack, world, tooltip, flagIn);
    }

    @Override
    public Class<CourierPackageTile> getTileEntityClass() {
        return CourierPackageTile.class;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CourierPackageTile(ModTileEntities.COURIER_PACKAGE_TILE.get(), pPos, pState);
    }
}
