package net.joefoxe.hexerei.world.processor;


import com.mojang.serialization.Codec;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
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
 * Dynamically generates support legs below small dungeons.
 * Yellow stained glass is used to mark the corner positions where the legs will spawn for simplicity.
 */
@MethodsReturnNonnullByDefault
public class DarkCovenLegProcessor extends StructureProcessor {
    public static final DarkCovenLegProcessor INSTANCE = new DarkCovenLegProcessor();
    public static final Codec<DarkCovenLegProcessor> CODEC = Codec.unit(() -> INSTANCE);

    @ParametersAreNonnullByDefault
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader worldReader, BlockPos jigsawPiecePos, BlockPos jigsawPieceBottomCenterPos, StructureTemplate.StructureBlockInfo blockInfoLocal, StructureTemplate.StructureBlockInfo blockInfoGlobal, StructurePlaceSettings structurePlacementData, @Nullable StructureTemplate template) {
//        if (blockInfoGlobal.state().getBlock() == Blocks.YELLOW_STAINED_GLASS_PANE) {
//            BlockPos worldPos = blockInfoGlobal.pos();
//
//            BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos().set(worldPos);
//
//            if(worldReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(currentPos))) {
//                return getReturnBlock(worldPos, blockInfoGlobal.state());
//            }
//
//            ChunkPos currentChunkPos = new ChunkPos(blockInfoGlobal.pos());
//            ChunkAccess currentChunk = worldReader.getChunk(currentChunkPos.x, currentChunkPos.z);
//            RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
//
//            // Always replace the glass itself with mossy cobble
//            currentChunk.setBlockState(blockInfoGlobal.pos(), Blocks.DARK_OAK_LOG.defaultBlockState(), false);
//            blockInfoGlobal = new StructureTemplate.StructureBlockInfo(blockInfoGlobal.pos(), Blocks.DARK_OAK_LOG.defaultBlockState(), blockInfoGlobal.nbt());
//
//            // Generate vertical pillar down
//            BlockPos.MutableBlockPos mutable = blockInfoGlobal.pos().below().mutable();
//            BlockState currBlock = worldReader.getBlockState(mutable);
//            while (mutable.getY() > 0 && (currBlock.canBeReplaced() || currBlock.isAir() || currBlock.is(BlockTags.LEAVES) || currBlock.is(Blocks.WATER) || currBlock.is(Blocks.LAVA))) {
//                currentChunk.setBlockState(mutable, Blocks.DARK_OAK_LOG.defaultBlockState(), false);
//                mutable.move(Direction.DOWN);
//                currBlock = worldReader.getBlockState(mutable);
//            }
//        }

        return blockInfoGlobal;
    }

    protected StructureProcessorType<?> getType() {
        return Hexerei.DARK_COVEN_LEG_PROCESSOR;
    }
    private static StructureTemplate.StructureBlockInfo getReturnBlock(BlockPos worldPos, BlockState originalReplacementState) {
        return originalReplacementState == null || originalReplacementState.is(Blocks.STRUCTURE_VOID) ?
                null : new StructureTemplate.StructureBlockInfo(worldPos, originalReplacementState, null);
    }
}

