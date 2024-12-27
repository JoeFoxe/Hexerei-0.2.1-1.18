package net.joefoxe.hexerei.world.processor;


import com.mojang.serialization.MapCodec;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Dynamically generates support legs.
 * Yellow stained glass is used to mark the corner positions where the legs will spawn for simplicity.
 */
@MethodsReturnNonnullByDefault
public class NatureCovenLegProcessor extends StructureProcessor {
    public static final MapCodec<NatureCovenLegProcessor> CODEC = MapCodec.unit(NatureCovenLegProcessor::new);

    @ParametersAreNonnullByDefault
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader worldReader, BlockPos jigsawPiecePos, BlockPos jigsawPieceBottomCenterPos, StructureTemplate.StructureBlockInfo blockInfoLocal, StructureTemplate.StructureBlockInfo blockInfoGlobal, StructurePlaceSettings structurePlacementData, @Nullable StructureTemplate template) {
        BlockPos worldPos = blockInfoGlobal.pos();

        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos().set(worldPos);

        ChunkPos currentChunkPos = new ChunkPos(blockInfoGlobal.pos());
        ChunkAccess currentChunk = worldReader.getChunk(currentChunkPos.x, currentChunkPos.z);

        if (blockInfoGlobal.state().getBlock() == Blocks.RED_STAINED_GLASS_PANE) {

            if(worldReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(currentPos))) {
                return getReturnBlock(worldPos, blockInfoGlobal.state());
            }

            // Always replace the observer itself with a ladder and grab the correct facing of the ladder from the ladder placed above
            BlockState newState = ModBlocks.WILLOW_VINES_PLANT.get().defaultBlockState();
            currentChunk.setBlockState(blockInfoGlobal.pos(), newState, false);

            blockInfoGlobal = new StructureTemplate.StructureBlockInfo(blockInfoGlobal.pos(), newState, blockInfoGlobal.nbt());

            // Generate vertical pillar down
            BlockPos.MutableBlockPos mutable = blockInfoGlobal.pos().below().mutable();
            BlockState currBlock = worldReader.getBlockState(mutable);
            while (mutable.getY() > 0 && (currBlock.canBeReplaced() || currBlock.isAir() || currBlock.is(BlockTags.LEAVES) || currBlock.is(Blocks.WATER) || currBlock.is(Blocks.LAVA))) {
                currentChunk.setBlockState(mutable, newState, false);
                mutable.move(Direction.DOWN);
                currBlock = worldReader.getBlockState(mutable);

                if(!(currBlock.canBeReplaced() || currBlock.isAir() || currBlock.is(BlockTags.LEAVES) || currBlock.is(Blocks.WATER) || currBlock.is(Blocks.LAVA))) {
                    currentChunk.setBlockState(mutable.above(), ModBlocks.WILLOW_VINES.get().defaultBlockState(), false);
                    break;
                }
            }
        }

        return blockInfoGlobal;
    }

    protected StructureProcessorType<?> getType() {
        return ModStructureProcessors.NATURE_COVEN_LEG_PROCESSOR.get();
    }
    private static StructureTemplate.StructureBlockInfo getReturnBlock(BlockPos worldPos, BlockState originalReplacementState) {
        return originalReplacementState == null || originalReplacementState.is(Blocks.STRUCTURE_VOID) ?
                null : new StructureTemplate.StructureBlockInfo(worldPos, originalReplacementState, null);
    }
}

