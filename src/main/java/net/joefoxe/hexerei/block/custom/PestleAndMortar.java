package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.tileentity.PestleAndMortarTile;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class PestleAndMortar extends Block implements ITileEntity<PestleAndMortarTile>, EntityBlock, SimpleWaterloggedBlock {


    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.MODEL;
    }

//    @SuppressWarnings("deprecation")
//    @Override
//    public RenderShape getRenderShape(BlockState iBlockState) {
//        return RenderShape.MODEL;
//    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

//    @Override
//    public Object getRenderPropertiesInternal() {
//        return new BroomRenderProperties();
//    }


    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }


    // hitbox REMEMBER TO DO THIS
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(4, 1, 4, 5, 7, 12),
            Block.box(5, 1, 4, 11, 7, 5),
            Block.box(5, 1, 11, 11, 7, 12),
            Block.box(5, 0, 5, 11, 2, 11),
            Block.box(11, 1, 4, 12, 7, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();


    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {


        if (stack.is(ModItems.CROW_FLUTE.get()) && stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandMode() == 2) {
            stack.useOn(new UseOnContext(player, hand, hitResult));
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof PestleAndMortarTile) {
            ((PestleAndMortarTile)tileEntity).interactPestleAndMortar(player, hitResult);
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }


    public PestleAndMortar(Properties properties) {
        super(properties.noOcclusion());
        this.withPropertiesOf(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, WATERLOGGED);
    }



    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            tooltipComponents.add(Component.translatable("tooltip.hexerei.pestle_and_mortar_shift_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.pestle_and_mortar_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.pestle_and_mortar_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.pestle_and_mortar_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.pestle_and_mortar_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity != null) {
                PestleAndMortarTile te = (PestleAndMortarTile) level.getBlockEntity(pos);

                if(!te.getItems().get(0).isEmpty())
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.25f, pos.getZ() + 0.5f, te.getItems().get(0)));
                if(!te.getItems().get(1).isEmpty())
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.25f, pos.getZ() + 0.5f, te.getItems().get(1)));
                if(!te.getItems().get(2).isEmpty())
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.25f, pos.getZ() + 0.5f, te.getItems().get(2)));
                if(!te.getItems().get(3).isEmpty())
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.25f, pos.getZ() + 0.5f, te.getItems().get(3)));
                if(!te.getItems().get(4).isEmpty())
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.25f, pos.getZ() + 0.5f, te.getItems().get(4)));
                if(!te.getItems().get(5).isEmpty())
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.25f, pos.getZ() + 0.5f, te.getItems().get(5)));
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public Class<PestleAndMortarTile> getTileEntityClass() {
        return PestleAndMortarTile.class;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PestleAndMortarTile(ModTileEntities.PESTLE_AND_MORTAR_TILE.get(), pos, state);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType){
        return entityType == ModTileEntities.PESTLE_AND_MORTAR_TILE.get() ?
                (world2, pos, state2, entity) -> ((PestleAndMortarTile)entity).tick() : null;
    }
}
