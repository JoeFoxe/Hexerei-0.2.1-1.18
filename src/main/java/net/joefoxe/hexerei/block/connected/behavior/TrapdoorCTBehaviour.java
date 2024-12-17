package net.joefoxe.hexerei.block.connected.behavior;


import net.joefoxe.hexerei.block.connected.BlockConnectivity;
import net.joefoxe.hexerei.block.connected.CTSpriteShiftEntry;
import net.joefoxe.hexerei.block.connected.ConnectedTextureBehaviour;
import net.joefoxe.hexerei.util.ClientProxy;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import org.jetbrains.annotations.Nullable;

public class TrapdoorCTBehaviour extends ConnectedTextureBehaviour.Base {

    private CTSpriteShiftEntry shift;


    public TrapdoorCTBehaviour(CTSpriteShiftEntry shift) {
        this.shift = shift;
    }
    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos,
                              Direction face) {

        if (state.hasProperty(HorizontalDirectionalBlock.FACING) && state.hasProperty(TrapDoorBlock.OPEN) && (!state.getValue(TrapDoorBlock.OPEN))) {
            Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
            BlockPos offset = otherPos.subtract(pos);

            float angleDegrees = facing.toYRot() - 180f;
            double angleRadians = Math.toRadians(angleDegrees);

            otherPos = pos.offset(rotateOffset(offset, angleRadians));
            other = reader.getBlockState(otherPos);
        }
        Direction facing = Direction.NORTH;
        if (state.hasProperty(HorizontalDirectionalBlock.FACING)) {
            facing = state.getValue(HorizontalDirectionalBlock.FACING);
            BlockPos offset = otherPos.subtract(pos);
            if (state.hasProperty(TrapDoorBlock.OPEN)) {
                if (state.getValue(TrapDoorBlock.OPEN)) {
                    if (state.hasProperty(TrapDoorBlock.HALF) && state.getValue(TrapDoorBlock.HALF) == Half.TOP) {

                        float angleDegrees = facing.toYRot() - 180f;
                        double angleRadians = Math.toRadians(angleDegrees);

                        offset = rotateXAxisOffset(rotateOffset(offset, angleRadians), angleRadians);
                        if (facing == Direction.SOUTH && (face == Direction.NORTH || face == Direction.SOUTH))
                            offset = new BlockPos(-offset.getX(), offset.getY(), offset.getZ());
                        if (facing == Direction.EAST && (face == Direction.UP || face == Direction.DOWN)) {
                            offset = rotateOffset(offset, Math.toRadians(90f));
                            offset = new BlockPos(offset.getX(), offset.getY(), -offset.getZ());
                        }
                        if (facing == Direction.NORTH) {
                            offset = new BlockPos(-offset.getX(), offset.getY(), offset.getZ());
                        }
                        if (facing == Direction.WEST && (face == Direction.UP || face == Direction.DOWN)) {
                            offset = rotateOffset(offset, Math.toRadians(270f));
                            offset = new BlockPos(offset.getX(), offset.getY(), -offset.getZ());
                        }

                    } else {
                        if (facing == Direction.SOUTH && (face == Direction.UP || face == Direction.DOWN))
                            offset = new BlockPos(-offset.getX(), offset.getY(), offset.getZ());
                        if (facing == Direction.EAST && (face == Direction.UP || face == Direction.DOWN)) {
                            offset = rotateOffset(offset, Math.toRadians(90f));
                        }
                        if (facing == Direction.WEST && (face == Direction.UP || face == Direction.DOWN)) {
                            offset = rotateOffset(offset, Math.toRadians(270f));
                        }

                    }
                } else {

                    if (facing == Direction.SOUTH) {
                        if (face == Direction.NORTH || face == Direction.SOUTH)
                            offset = new BlockPos(-offset.getX(), offset.getY(), offset.getZ());
                        if (face == Direction.WEST || face == Direction.EAST)
                            offset = new BlockPos(offset.getX(), offset.getY(), -offset.getZ());
                    }
                    if (facing == Direction.EAST) {
                        if (face == Direction.NORTH || face == Direction.SOUTH) {
                            offset = rotateOffset(offset, Math.toRadians(90f));
                            offset = new BlockPos(-offset.getX(), offset.getY(), offset.getZ());
                        }
                        if (face == Direction.WEST || face == Direction.EAST) {
                            offset = rotateOffset(offset, Math.toRadians(90f));
                            offset = new BlockPos(offset.getX(), offset.getY(), -offset.getZ());
                        }
                    }
                    if (facing == Direction.WEST) {
                        if (face == Direction.NORTH || face == Direction.SOUTH) {
                            offset = rotateOffset(offset, Math.toRadians(-90f));
                            offset = new BlockPos(-offset.getX(), offset.getY(), offset.getZ());
                        }
                        if (face == Direction.WEST || face == Direction.EAST) {
                            offset = rotateOffset(offset, Math.toRadians(-90f));
                            offset = new BlockPos(offset.getX(), offset.getY(), -offset.getZ());
                        }
                    }
                }
                otherPos = pos.offset(offset);
                other = reader.getBlockState(otherPos);
            }
        }

        if (isBeingBlocked(state, reader, pos, otherPos, face))
            return false;
        BlockConnectivity cc = ClientProxy.BLOCK_CONNECTIVITY;
        BlockConnectivity.Entry entry = cc.get(state);
        BlockConnectivity.Entry otherEntry = cc.get(other);
        if(!(state.hasProperty(TrapDoorBlock.OPEN) && state.hasProperty(TrapDoorBlock.HALF) && state.hasProperty(HorizontalDirectionalBlock.FACING) &&
             other.hasProperty(TrapDoorBlock.OPEN) && other.hasProperty(TrapDoorBlock.HALF) && other.hasProperty(HorizontalDirectionalBlock.FACING)
        )) {
            return false;
        } else {
            if (!(state.getValue(TrapDoorBlock.OPEN) == other.getValue(TrapDoorBlock.OPEN) &&
                    state.getValue(TrapDoorBlock.HALF) == other.getValue(TrapDoorBlock.HALF) &&
                    state.getValue(HorizontalDirectionalBlock.FACING) == other.getValue(HorizontalDirectionalBlock.FACING))) {
                return false;
            }
            if (state.getValue(TrapDoorBlock.OPEN)) {
                if ((facing == Direction.SOUTH || facing == Direction.NORTH) && pos.getZ() != otherPos.getZ())
                    return false;
                if ((facing == Direction.EAST || facing == Direction.WEST) && pos.getX() != otherPos.getX())
                    return false;
            }
        }
        if (entry == null || otherEntry == null)
            return false;
        if (!entry.isSideValid(state, face) || !otherEntry.isSideValid(other, face))
            return false;
        if (entry.getCTSpriteShiftEntry() != otherEntry.getCTSpriteShiftEntry())
            return false;
        return true;
    }

    private BlockPos rotateOffset(BlockPos offset, double angleRadians) {
        int x = offset.getX();
        int z = offset.getZ();

        // Apply the rotation matrix
        int newX = (int) Math.round(x * Math.cos(angleRadians) - z * Math.sin(angleRadians));
        int newZ = (int) Math.round(x * Math.sin(angleRadians) + z * Math.cos(angleRadians));

        return new BlockPos(newX, offset.getY(), newZ);
    }

    private BlockPos rotateXAxisOffset(BlockPos offset, double angleRadians) {
        int x = offset.getX();
        int y = offset.getY();
        int z = offset.getZ();

        // Apply the 180-degree rotation around the local x-axis
        // This involves flipping the y and z coordinates
        int newY = -y;
        int newZ = -z;

        // Transform back to the global coordinate system
        int globalX = (int) Math.round(x * Math.cos(angleRadians) - newZ * Math.sin(angleRadians));
        int globalZ = (int) Math.round(x * Math.sin(angleRadians) + newZ * Math.cos(angleRadians));

        return new BlockPos(globalX, newY, globalZ);
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        return shift;
    }

}