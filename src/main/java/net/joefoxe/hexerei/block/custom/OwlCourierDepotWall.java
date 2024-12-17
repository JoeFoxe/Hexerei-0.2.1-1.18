package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.tileentity.BroomStandTile;
import net.joefoxe.hexerei.tileentity.OwlCourierDepotTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class OwlCourierDepotWall extends OwlCourierDepot implements ITileEntity<OwlCourierDepotTile>, EntityBlock, SimpleWaterloggedBlock {


    VoxelShape shape = Stream.of(
            Block.box(2, 2, 0, 14, 9, 3),
            Block.box(3, 5, 3, 13, 6, 13)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    VoxelShape shape_90 = Stream.of(
            Block.box(0, 2, 2, 3, 9, 14),
            Block.box(3, 5, 3, 13, 6, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    VoxelShape shape_180 = Stream.of(
            Block.box(2, 2, 13, 14, 9, 16),
            Block.box(3, 5, 3, 13, 6, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    VoxelShape shape_270 = Stream.of(
            Block.box(13, 2, 2, 16, 9, 14),
            Block.box(3, 5, 3, 13, 6, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public OwlCourierDepotWall(Properties pProperties){
        super(pProperties);
        registerDefaultState(super.defaultBlockState()
                .setValue(WATERLOGGED, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

        if (context.getClickedFace() != Direction.UP && context.getClickedFace() != Direction.DOWN)
            return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getClickedFace().getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);

        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if(!pState.hasProperty(HorizontalDirectionalBlock.FACING))
            return shape;

        Direction dir = pState.getValue(HorizontalDirectionalBlock.FACING);

        return switch (dir){
            case DOWN, UP, NORTH -> shape;
            case SOUTH -> shape_180;
            case WEST -> shape_90;
            case EAST -> shape_270;
        };
    }
}
