package net.joefoxe.hexerei.block;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.connected.*;
import net.joefoxe.hexerei.block.connected.behavior.*;
import net.joefoxe.hexerei.block.custom.*;
import net.joefoxe.hexerei.block.custom.trees.MahoganyTree;
import net.joefoxe.hexerei.block.custom.trees.WillowTree;
import net.joefoxe.hexerei.block.custom.trees.WitchHazelTree;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ModBlocks {


	public static final DeferredRegister<Block> BLOCKS
					= DeferredRegister.create(ForgeRegistries.BLOCKS, Hexerei.MOD_ID);

	public static final Registrate REGISTRATE = Hexerei.registrate();



	public static final BlockEntry<Block> WILLOW_CONNECTED = REGISTRATE.block("willow_connected", Block::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WILLOW_CONNECTED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WILLOW_CONNECTED)))
			.register();
	public static final BlockEntry<WaxedLayeredBlock> WAXED_WILLOW_CONNECTED = REGISTRATE.block("waxed_willow_connected", WaxedLayeredBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_WILLOW_CONNECTED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_WILLOW_CONNECTED)))
			.register();

	public static final BlockEntry<Block> POLISHED_WILLOW_CONNECTED = REGISTRATE.block("polished_willow_connected", Block::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.POLISHED_WILLOW_CONNECTED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_WILLOW_CONNECTED)))
			.register();

	public static final BlockEntry<WaxedLayeredBlock> WAXED_POLISHED_WILLOW_CONNECTED = REGISTRATE.block("waxed_polished_willow_connected", WaxedLayeredBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_POLISHED_WILLOW_CONNECTED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_WILLOW_CONNECTED)))
			.register();

	public static final BlockEntry<ConnectedPillarBlock> POLISHED_WILLOW_PILLAR = REGISTRATE.block("polished_willow_pillar", ConnectedPillarBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new RotatedPillarCTBehaviour(AllSpriteShifts.POLISHED_WILLOW_PILLAR_SIDE, AllSpriteShifts.POLISHED_WILLOW_PILLAR_TOP)))
			.register();

	public static final BlockEntry<WaxedConnectedRotatedPillarBlock> WAXED_POLISHED_WILLOW_PILLAR = REGISTRATE.block("waxed_polished_willow_pillar", WaxedConnectedRotatedPillarBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new RotatedPillarCTBehaviour(AllSpriteShifts.WAXED_POLISHED_WILLOW_PILLAR_SIDE, AllSpriteShifts.WAXED_POLISHED_WILLOW_PILLAR_TOP)))
			.register();

	public static final BlockEntry<LayeredBlock> POLISHED_WILLOW_LAYERED = REGISTRATE.block("polished_willow_layered", LayeredBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new HorizontalCTBehaviour(AllSpriteShifts.POLISHED_WILLOW_LAYERED, AllSpriteShifts.POLISHED_SMOOTH_WILLOW)))
			.register();

	public static final BlockEntry<WaxedLayeredBlock> WAXED_POLISHED_WILLOW_LAYERED = REGISTRATE.block("waxed_polished_willow_layered", WaxedLayeredBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new HorizontalCTBehaviour(AllSpriteShifts.WAXED_POLISHED_WILLOW_LAYERED, AllSpriteShifts.WAXED_POLISHED_SMOOTH_WILLOW)))
			.register();



	public static final BlockEntry<Block> WITCH_HAZEL_CONNECTED = REGISTRATE.block("witch_hazel_connected", Block::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WITCH_HAZEL_CONNECTED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WITCH_HAZEL_CONNECTED)))
			.register();
	public static final BlockEntry<WaxedLayeredBlock> WAXED_WITCH_HAZEL_CONNECTED = REGISTRATE.block("waxed_witch_hazel_connected", WaxedLayeredBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_WITCH_HAZEL_CONNECTED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_WITCH_HAZEL_CONNECTED)))
			.register();

	public static final BlockEntry<Block> POLISHED_WITCH_HAZEL_CONNECTED = REGISTRATE.block("polished_witch_hazel_connected", Block::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.POLISHED_WITCH_HAZEL_CONNECTED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_WITCH_HAZEL_CONNECTED)))
			.register();

	public static final BlockEntry<WaxedLayeredBlock> WAXED_POLISHED_WITCH_HAZEL_CONNECTED = REGISTRATE.block("waxed_polished_witch_hazel_connected", WaxedLayeredBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_POLISHED_WITCH_HAZEL_CONNECTED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_WITCH_HAZEL_CONNECTED)))
			.register();

	public static final BlockEntry<ConnectedPillarBlock> POLISHED_WITCH_HAZEL_PILLAR = REGISTRATE.block("polished_witch_hazel_pillar", ConnectedPillarBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new RotatedPillarCTBehaviour(AllSpriteShifts.POLISHED_WITCH_HAZEL_PILLAR_SIDE, AllSpriteShifts.POLISHED_WITCH_HAZEL_PILLAR_TOP)))
			.register();

	public static final BlockEntry<WaxedConnectedRotatedPillarBlock> WAXED_POLISHED_WITCH_HAZEL_PILLAR = REGISTRATE.block("waxed_polished_witch_hazel_pillar", WaxedConnectedRotatedPillarBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new RotatedPillarCTBehaviour(AllSpriteShifts.WAXED_POLISHED_WITCH_HAZEL_PILLAR_SIDE, AllSpriteShifts.WAXED_POLISHED_WITCH_HAZEL_PILLAR_TOP)))
			.register();

	public static final BlockEntry<LayeredBlock> POLISHED_WITCH_HAZEL_LAYERED = REGISTRATE.block("polished_witch_hazel_layered", LayeredBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new HorizontalCTBehaviour(AllSpriteShifts.POLISHED_WITCH_HAZEL_LAYERED, AllSpriteShifts.POLISHED_SMOOTH_WITCH_HAZEL)))
			.register();

	public static final BlockEntry<WaxedLayeredBlock> WAXED_POLISHED_WITCH_HAZEL_LAYERED = REGISTRATE.block("waxed_polished_witch_hazel_layered", WaxedLayeredBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_GREEN))
			.onRegister(connectedTextures(() -> new HorizontalCTBehaviour(AllSpriteShifts.WAXED_POLISHED_WITCH_HAZEL_LAYERED, AllSpriteShifts.WAXED_POLISHED_SMOOTH_WITCH_HAZEL)))
			.register();


	public static final BlockEntry<Block> MAHOGANY_CONNECTED = REGISTRATE.block("mahogany_connected", Block::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.MAHOGANY_CONNECTED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.MAHOGANY_CONNECTED)))
			.register();


	public static final BlockEntry<Block> POLISHED_MAHOGANY_CONNECTED = REGISTRATE.block("polished_mahogany_connected", Block::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.POLISHED_MAHOGANY_CONNECTED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_MAHOGANY_CONNECTED)))
			.register();
	public static final BlockEntry<WaxedLayeredBlock> WAXED_MAHOGANY_CONNECTED = REGISTRATE.block("waxed_mahogany_connected", (properties) -> new WaxedLayeredBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_MAHOGANY_CONNECTED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_MAHOGANY_CONNECTED)))
			.register();
	public static final BlockEntry<WaxedLayeredBlock> WAXED_POLISHED_MAHOGANY_CONNECTED = REGISTRATE.block("waxed_polished_mahogany_connected", (properties) -> new WaxedLayeredBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_POLISHED_MAHOGANY_CONNECTED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_MAHOGANY_CONNECTED)))
			.register();

	public static final BlockEntry<ConnectedPillarBlock> POLISHED_MAHOGANY_PILLAR = REGISTRATE.block("polished_mahogany_pillar", ConnectedPillarBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new RotatedPillarCTBehaviour(AllSpriteShifts.POLISHED_MAHOGANY_PILLAR_SIDE, AllSpriteShifts.POLISHED_MAHOGANY_PILLAR_TOP)))
			.register();

	public static final BlockEntry<WaxedConnectedRotatedPillarBlock> WAXED_POLISHED_MAHOGANY_PILLAR = REGISTRATE.block("waxed_polished_mahogany_pillar", WaxedConnectedRotatedPillarBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new RotatedPillarCTBehaviour(AllSpriteShifts.WAXED_POLISHED_MAHOGANY_PILLAR_SIDE, AllSpriteShifts.WAXED_POLISHED_MAHOGANY_PILLAR_TOP)))
			.register();

	public static final BlockEntry<LayeredBlock> POLISHED_MAHOGANY_LAYERED = REGISTRATE.block("polished_mahogany_layered", LayeredBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new HorizontalCTBehaviour(AllSpriteShifts.POLISHED_MAHOGANY_LAYERED, AllSpriteShifts.POLISHED_SMOOTH_MAHOGANY)))
			.register();


	public static final BlockEntry<WaxedLayeredBlock> WAXED_POLISHED_MAHOGANY_LAYERED = REGISTRATE.block("waxed_polished_mahogany_layered", WaxedLayeredBlock::new)
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new HorizontalCTBehaviour(AllSpriteShifts.WAXED_POLISHED_MAHOGANY_LAYERED, AllSpriteShifts.WAXED_POLISHED_SMOOTH_MAHOGANY)))
			.register();


	public static final BlockEntry<WaxedGlassPaneBlock> STONE_WINDOW_PANE = REGISTRATE.block("stone_window_pane", (properties) -> new WaxedGlassPaneBlock(BlockBehaviour.Properties.copy(Blocks.GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.GLASS_PANE).mapColor(MapColor.STONE))
			.onRegister(connectedTextures(() -> new GlassPaneTransparentCTBehaviour(AllSpriteShifts.STONE_WINDOW_PANE_CONNECTED, AllSpriteShifts.STONE_WINDOW_PANE_CONNECTED_GLASS)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.STONE_WINDOW_PANE_CONNECTED)))
			.register();
	public static final BlockEntry<WaxedGlassPaneBlock> WAXED_STONE_WINDOW_PANE = REGISTRATE.block("waxed_stone_window_pane", (properties) -> new WaxedGlassPaneBlock(BlockBehaviour.Properties.copy(Blocks.GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.GLASS_PANE).mapColor(MapColor.STONE))
			.onRegister(connectedTextures(() -> new GlassPaneTransparentCTBehaviour(AllSpriteShifts.WAXED_STONE_WINDOW_PANE_CONNECTED, AllSpriteShifts.WAXED_STONE_WINDOW_PANE_CONNECTED_GLASS)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_STONE_WINDOW_PANE_CONNECTED)))
			.register();

	public static final BlockEntry<GlassBlock> STONE_WINDOW = REGISTRATE.block("stone_window", (properties) -> new GlassBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockTopBottomShiftCTBehaviour(AllSpriteShifts.STONE_WINDOW_CONNECTED, AllSpriteShifts.STONE_WINDOW_CONNECTED_GLASS, AllSpriteShifts.STONE_WINDOW_CONNECTED_TOP, AllSpriteShifts.STONE_WINDOW_CONNECTED_TOP_GLASS)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.STONE_WINDOW_CONNECTED)))
			.register();

	public static final BlockEntry<GlassBlock> WAXED_STONE_WINDOW = REGISTRATE.block("waxed_stone_window", (properties) -> new GlassBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockTopBottomShiftCTBehaviour(AllSpriteShifts.WAXED_STONE_WINDOW_CONNECTED, AllSpriteShifts.WAXED_STONE_WINDOW_CONNECTED_GLASS, AllSpriteShifts.WAXED_STONE_WINDOW_CONNECTED_TOP, AllSpriteShifts.WAXED_STONE_WINDOW_CONNECTED_TOP_GLASS)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_STONE_WINDOW_CONNECTED)))
			.register();


	public static final BlockEntry<WaxedGlassPaneBlock> MAHOGANY_WINDOW_PANE = REGISTRATE.block("mahogany_window_pane", (properties) -> new WaxedGlassPaneBlock(BlockBehaviour.Properties.copy(Blocks.GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new GlassPaneCTBehaviour(AllSpriteShifts.POLISHED_SMOOTH_MAHOGANY_GLASS_PANE)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_SMOOTH_MAHOGANY_GLASS_PANE)))
			.register();


	public static final BlockEntry<WaxedGlassPaneBlock> WAXED_MAHOGANY_WINDOW_PANE = REGISTRATE.block("waxed_mahogany_window_pane", (properties) -> new WaxedGlassPaneBlock(BlockBehaviour.Properties.copy(Blocks.GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new GlassPaneCTBehaviour(AllSpriteShifts.WAXED_POLISHED_SMOOTH_MAHOGANY_GLASS_PANE)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_SMOOTH_MAHOGANY_GLASS_PANE)))
			.register();


	public static final BlockEntry<WaxedGlassPaneBlock> WILLOW_WINDOW_PANE = REGISTRATE.block("willow_window_pane", (properties) -> new WaxedGlassPaneBlock(BlockBehaviour.Properties.copy(Blocks.GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new GlassPaneCTBehaviour(AllSpriteShifts.POLISHED_SMOOTH_WILLOW_GLASS_PANE)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_SMOOTH_WILLOW_GLASS_PANE)))
			.register();


	public static final BlockEntry<WaxedGlassPaneBlock> WAXED_WILLOW_WINDOW_PANE = REGISTRATE.block("waxed_willow_window_pane", (properties) -> new WaxedGlassPaneBlock(BlockBehaviour.Properties.copy(Blocks.GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new GlassPaneCTBehaviour(AllSpriteShifts.WAXED_POLISHED_SMOOTH_WILLOW_GLASS_PANE)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_SMOOTH_WILLOW_GLASS_PANE)))
			.register();


	public static final BlockEntry<WaxedGlassPaneBlock> WITCH_HAZEL_WINDOW_PANE = REGISTRATE.block("witch_hazel_window_pane", (properties) -> new WaxedGlassPaneBlock(BlockBehaviour.Properties.copy(Blocks.GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new GlassPaneCTBehaviour(AllSpriteShifts.POLISHED_SMOOTH_WITCH_HAZEL_GLASS_PANE)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_SMOOTH_WITCH_HAZEL_GLASS_PANE)))
			.register();


	public static final BlockEntry<WaxedGlassPaneBlock> WAXED_WITCH_HAZEL_WINDOW_PANE = REGISTRATE.block("waxed_witch_hazel_window_pane", (properties) -> new WaxedGlassPaneBlock(BlockBehaviour.Properties.copy(Blocks.GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new GlassPaneCTBehaviour(AllSpriteShifts.WAXED_POLISHED_SMOOTH_WITCH_HAZEL_GLASS_PANE)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_SMOOTH_WITCH_HAZEL_GLASS_PANE)))
			.register();

	public static final BlockEntry<GlassBlock> MAHOGANY_WINDOW = REGISTRATE.block("mahogany_window", (properties) -> new GlassBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.POLISHED_SMOOTH_MAHOGANY_GLASS)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_SMOOTH_MAHOGANY_GLASS)))
			.register();

	public static final BlockEntry<WaxedGlassBlock> WAXED_MAHOGANY_WINDOW = REGISTRATE.block("waxed_mahogany_window", (properties) -> new WaxedGlassBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_POLISHED_SMOOTH_MAHOGANY_GLASS)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_SMOOTH_MAHOGANY_GLASS)))
			.register();

	public static final BlockEntry<GlassBlock> WILLOW_WINDOW = REGISTRATE.block("willow_window", (properties) -> new GlassBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.POLISHED_SMOOTH_WILLOW_GLASS)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_SMOOTH_WILLOW_GLASS)))
			.register();

	public static final BlockEntry<WaxedGlassBlock> WAXED_WILLOW_WINDOW = REGISTRATE.block("waxed_willow_window", (properties) -> new WaxedGlassBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_POLISHED_SMOOTH_WILLOW_GLASS)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_SMOOTH_WILLOW_GLASS)))
			.register();

	public static final BlockEntry<GlassBlock> WITCH_HAZEL_WINDOW = REGISTRATE.block("witch_hazel_window", (properties) -> new GlassBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.POLISHED_SMOOTH_WITCH_HAZEL_GLASS)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_SMOOTH_WITCH_HAZEL_GLASS)))
			.register();

	public static final BlockEntry<WaxedGlassBlock> WAXED_WITCH_HAZEL_WINDOW = REGISTRATE.block("waxed_witch_hazel_window", (properties) -> new WaxedGlassBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_POLISHED_SMOOTH_WITCH_HAZEL_GLASS)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_POLISHED_SMOOTH_WITCH_HAZEL_GLASS)))
			.register();


	public static final BlockEntry<FabricBlock> INFUSED_FABRIC_BLOCK_ORNATE = REGISTRATE.block("infused_fabric_block_ornate", (properties) -> new FabricBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE)))
			.register();

	public static final BlockEntry<FabricBlock> WAXED_INFUSED_FABRIC_BLOCK_ORNATE = REGISTRATE.block("waxed_infused_fabric_block_ornate", (properties) -> new FabricBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new FullBlockCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE)))
			.register();

	public static final BlockEntry<ConnectingCarpetDyed> INFUSED_FABRIC_CARPET_ORNATE = REGISTRATE.block("infused_fabric_carpet_ornate",
			(properties) -> new ConnectingCarpetDyed(BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_BLACK), DyeColor.WHITE))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new CarpetCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE, AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE)))
			.register();

	public static final BlockEntry<ConnectingCarpetDyed> WAXED_INFUSED_FABRIC_CARPET_ORNATE = REGISTRATE.block("waxed_infused_fabric_carpet_ornate",
			(properties) -> new ConnectingCarpetDyed(BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_BLACK), DyeColor.WHITE))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new CarpetCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE)))
			.loot((lootTables, block) -> lootTables.dropOther(block, block.asItem()))
			.register();

	public static final BlockEntry<ConnectingCarpetDyed> INFUSED_FABRIC_CARPET = REGISTRATE.block("infused_fabric_carpet",
			(properties) -> new ConnectingCarpetDyed(BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_BLACK), DyeColor.WHITE))
			.onRegister(connectedTextures(() -> new CarpetCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED, AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED)))
			.register();

	public static final BlockEntry<ConnectingCarpetDyed> WAXED_INFUSED_FABRIC_CARPET = REGISTRATE.block("waxed_infused_fabric_carpet",
			(properties) -> new ConnectingCarpetDyed(BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_BLACK), DyeColor.WHITE))
			.onRegister(connectedTextures(() -> new CarpetCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_DYED, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_DYED)))
			.register();

	public static final BlockEntry<ConnectingCarpetStairs> INFUSED_FABRIC_CARPET_ORNATE_STAIRS = REGISTRATE.block("infused_fabric_carpet_ornate_stairs", (properties -> new ConnectingCarpetStairs(BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET), ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get())))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_BLACK))
			.onRegister(connectedTextures(() -> new CarpetStairsCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE, AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE)))
			.loot((lootTables, block) -> lootTables.dropOther(block, block.parentBlock.asItem()))
			.register();

	public static final BlockEntry<ConnectingCarpetStairs> WAXED_INFUSED_FABRIC_CARPET_ORNATE_STAIRS = REGISTRATE.block("waxed_infused_fabric_carpet_ornate_stairs", (properties -> new ConnectingCarpetStairs(BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET), ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE.get())))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_BLACK))
			.onRegister(connectedTextures(() -> new CarpetStairsCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE)))
			.loot((lootTables, block) -> lootTables.dropOther(block, block.parentBlock.asItem()))
			.register();

	public static final BlockEntry<ConnectingCarpetStairs> INFUSED_FABRIC_CARPET_STAIRS = REGISTRATE.block("infused_fabric_carpet_stairs", (properties -> new ConnectingCarpetStairs(BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET), ModBlocks.INFUSED_FABRIC_CARPET.get())))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new CarpetStairsCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED, AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED)))
			.loot((lootTables, block) -> lootTables.dropOther(block, block.parentBlock.asItem()))
			.register();

	public static final BlockEntry<ConnectingCarpetStairs> WAXED_INFUSED_FABRIC_CARPET_STAIRS = REGISTRATE.block("waxed_infused_fabric_carpet_stairs", (properties -> new ConnectingCarpetStairs(BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET), ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get())))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new CarpetStairsCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_DYED, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_DYED)))
			.loot((lootTables, block) -> lootTables.dropOther(block, block.parentBlock.asItem()))
			.register();

	public static final BlockEntry<ConnectingCarpetSlab> INFUSED_FABRIC_CARPET_ORNATE_SLAB = REGISTRATE.block("infused_fabric_carpet_ornate_slab", (p) -> new ConnectingCarpetSlab(p, ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get()))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new CarpetCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE, AllSpriteShifts.INFUSED_FABRIC_CARPET_ORNATE)))
			.loot((lootTables, block) -> lootTables.dropOther(block, block.parentBlock.asItem()))
			.register();

	public static final BlockEntry<ConnectingCarpetSlab> WAXED_INFUSED_FABRIC_CARPET_ORNATE_SLAB = REGISTRATE.block("waxed_infused_fabric_carpet_ornate_slab", (p) -> new ConnectingCarpetSlab(p, ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE.get()))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new CarpetCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_ORNATE)))
			.loot((lootTables, block) -> lootTables.dropOther(block, block.parentBlock.asItem()))
			.register();

	public static final BlockEntry<ConnectingCarpetSlab> INFUSED_FABRIC_CARPET_SLAB = REGISTRATE.block("infused_fabric_carpet_slab", (p -> new ConnectingCarpetSlab(p, ModBlocks.INFUSED_FABRIC_CARPET.get())))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new CarpetCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED, AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED)))
			.loot((lootTables, block) -> lootTables.dropOther(block, block.parentBlock.asItem()))
			.register();

	public static final BlockEntry<ConnectingCarpetSlab> WAXED_INFUSED_FABRIC_CARPET_SLAB = REGISTRATE.block("waxed_infused_fabric_carpet_slab", (p -> new ConnectingCarpetSlab(p, ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get())))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.CYAN_CARPET).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new CarpetCTBehaviour(AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_DYED, AllSpriteShifts.WAXED_INFUSED_FABRIC_CARPET_DYED)))
			.loot((lootTables, block) -> lootTables.dropOther(block, block.parentBlock.asItem()))
			.register();



	public static final BlockEntry<FabricBlock> INFUSED_FABRIC_BLOCK = REGISTRATE.block("infused_fabric_block", (properties) -> new FabricBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new DyedFullBlockCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED)))
			.register();


	public static final BlockEntry<FabricBlock> WAXED_INFUSED_FABRIC_BLOCK = REGISTRATE.block("waxed_infused_fabric_block", (properties) -> new FabricBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new DyedFullBlockCTBehaviour(AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.INFUSED_FABRIC_CARPET_DYED)))
			.register();



	@OnlyIn(Dist.CLIENT)
	private static void registerCTBehviour(Block entry, Supplier<ConnectedTextureBehaviour> behaviorSupplier) {
		ConnectedTextureBehaviour behavior = behaviorSupplier.get();
		ClientProxy.MODEL_SWAPPER.getCustomBlockModels()
				.register(HexereiUtil.getKeyOrThrow(entry), model -> new CTModel(model, behavior));
	}


	public static <T extends Block> NonNullConsumer<? super T> blockConnectivity(
			BiConsumer<T, BlockConnectivity> consumer) {
		return entry -> onClient(() -> () -> registerBlockConnectivity(entry, consumer));
	}

	@OnlyIn(Dist.CLIENT)
	private static <T extends Block> void registerBlockConnectivity(T entry,
																	 BiConsumer<T, BlockConnectivity> consumer) {
		consumer.accept(entry, ClientProxy.BLOCK_CONNECTIVITY);
	}


	protected static void onClient(Supplier<Runnable> toRun) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, toRun);
	}
	public static <T extends Block> NonNullConsumer<? super T> connectedTextures(
			Supplier<ConnectedTextureBehaviour> behavior) {
		return entry -> onClient(() -> () -> registerCTBehviour(entry, behavior));
	}


	public static final RegistryObject<MixingCauldron> MIXING_CAULDRON = registerBlockNoItem("mixing_cauldron",
					() -> new MixingCauldron(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).randomTicks().explosionResistance(4f).requiresCorrectToolForDrops().strength(3).lightLevel(state -> 12)));

	public static final RegistryObject<CandleDipper> CANDLE_DIPPER = registerBlock("candle_dipper",
					() -> new CandleDipper(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).noCollission().noOcclusion().strength(3).requiresCorrectToolForDrops().explosionResistance(8f)));

	public static final RegistryObject<Coffer> COFFER = registerBlockNoItem("coffer",
					() -> new Coffer(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).requiresCorrectToolForDrops().explosionResistance(8f)));

	//TODO get back to this crystal eventually
	public static final RegistryObject<CuttingCrystal> CUTTING_CRYSTAL = registerBlockNoItem("cutting_crystal",
			() -> new CuttingCrystal(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).requiresCorrectToolForDrops().explosionResistance(8f)));

	public static final RegistryObject<OwlCourierDepotWall> WILLOW_COURIER_DEPOT_WALL = registerBlockNoItem("willow_courier_depot_wall",
			() -> new OwlCourierDepotWall(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<OwlCourierDepot> WILLOW_COURIER_DEPOT = registerBlockNoItem("willow_courier_depot",
			() -> new OwlCourierDepot(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<OwlCourierDepotWall> MAHOGANY_COURIER_DEPOT_WALL = registerBlockNoItem("mahogany_courier_depot_wall",
			() -> new OwlCourierDepotWall(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<OwlCourierDepot> MAHOGANY_COURIER_DEPOT = registerBlockNoItem("mahogany_courier_depot",
			() -> new OwlCourierDepot(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<OwlCourierDepotWall> WITCH_HAZEL_COURIER_DEPOT_WALL = registerBlockNoItem("witch_hazel_courier_depot_wall",
			() -> new OwlCourierDepotWall(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<OwlCourierDepot> WITCH_HAZEL_COURIER_DEPOT = registerBlockNoItem("witch_hazel_courier_depot",
			() -> new OwlCourierDepot(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<CourierLetter> COURIER_LETTER = registerBlockNoItem("courier_letter",
			() -> new CourierLetter(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET).noCollission().strength(0.1f).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<CourierPackage> COURIER_PACKAGE = registerBlockNoItem("courier_package",
			() -> new CourierPackage(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(0.4f).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<BroomStandWall> MAHOGANY_BROOM_STAND_WALL = registerBlockNoItem("mahogany_broom_stand_wall",
			() -> new BroomStandWall(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<BroomStand> MAHOGANY_BROOM_STAND = registerBlockNoItem("mahogany_broom_stand",
			() -> new BroomStand(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<BroomStandWall> WILLOW_BROOM_STAND_WALL = registerBlockNoItem("willow_broom_stand_wall",
			() -> new BroomStandWall(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<BroomStand> WILLOW_BROOM_STAND = registerBlockNoItem("willow_broom_stand",
			() -> new BroomStand(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<BroomStandWall> WITCH_HAZEL_BROOM_STAND_WALL = registerBlockNoItem("witch_hazel_broom_stand_wall",
			() -> new BroomStandWall(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<BroomStand> WITCH_HAZEL_BROOM_STAND = registerBlockNoItem("witch_hazel_broom_stand",
			() -> new BroomStand(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).noOcclusion().explosionResistance(2f)));

	public static final RegistryObject<Altar> BOOK_OF_SHADOWS_ALTAR = registerBlock("book_of_shadows_altar",
			() -> new Altar(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).explosionResistance(2f)));

	public static final RegistryObject<ConnectingTable> WILLOW_ALTAR = registerBlock("willow_altar",
			() -> new Altar(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).explosionResistance(2f)));

	public static final RegistryObject<ConnectingTable> WITCH_HAZEL_ALTAR = registerBlock("witch_hazel_altar",
			() -> new Altar(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).explosionResistance(2f)));

	public static final RegistryObject<Woodcutter> MAHOGANY_WOODCUTTER = registerBlock("mahogany_woodcutter",
			() -> new Woodcutter(BlockBehaviour.Properties.copy(Blocks.STONECUTTER).explosionResistance(4f).requiresCorrectToolForDrops().strength(3)));

	public static final RegistryObject<Woodcutter> WILLOW_WOODCUTTER = registerBlock("willow_woodcutter",
			() -> new Woodcutter(BlockBehaviour.Properties.copy(Blocks.STONECUTTER).explosionResistance(4f).requiresCorrectToolForDrops().strength(3)));

	public static final RegistryObject<Woodcutter> WITCH_HAZEL_WOODCUTTER = registerBlock("witch_hazel_woodcutter",
			() -> new Woodcutter(BlockBehaviour.Properties.copy(Blocks.STONECUTTER).explosionResistance(4f).requiresCorrectToolForDrops().strength(3)));

	public static final RegistryObject<ModChest> WILLOW_CHEST = registerBlockNoItem("willow_chest",
			() -> new ModChest(BlockBehaviour.Properties.copy(Blocks.CHEST).explosionResistance(4f).strength(3), ModTileEntities.CHEST_TILE::get, ModChest.WoodType.WILLOW));

	public static final RegistryObject<ModChest> WITCH_HAZEL_CHEST = registerBlockNoItem("witch_hazel_chest",
			() -> new ModChest(BlockBehaviour.Properties.copy(Blocks.CHEST).explosionResistance(4f).strength(3), ModTileEntities.CHEST_TILE::get, ModChest.WoodType.WITCH_HAZEL));

	public static final RegistryObject<ModChest> MAHOGANY_CHEST = registerBlockNoItem("mahogany_chest",
			() -> new ModChest(BlockBehaviour.Properties.copy(Blocks.CHEST).explosionResistance(4f).strength(3), ModTileEntities.CHEST_TILE::get, ModChest.WoodType.MAHOGANY));

	public static final RegistryObject<ModSign> MAHOGANY_SIGN = registerBlockNoItem("mahogany_sign",
			() -> new ModSign(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).explosionResistance(4f).strength(3).noCollission().noOcclusion(), ModWoodType.MAHOGANY));

	public static final RegistryObject<ModSign> WILLOW_SIGN = registerBlockNoItem("willow_sign",
			() -> new ModSign(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).explosionResistance(4f).strength(3).noCollission().noOcclusion(), ModWoodType.WILLOW));

	public static final RegistryObject<ModSign> WITCH_HAZEL_SIGN = registerBlockNoItem("witch_hazel_sign",
			() -> new ModSign(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).explosionResistance(4f).strength(3).noCollission().noOcclusion(), ModWoodType.WITCH_HAZEL));

	public static final RegistryObject<ModSign> POLISHED_MAHOGANY_SIGN = registerBlockNoItem("polished_mahogany_sign",
			() -> new ModSign(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).explosionResistance(4f).strength(3).noCollission().noOcclusion(), ModWoodType.POLISHED_MAHOGANY));

	public static final RegistryObject<ModSign> POLISHED_WILLOW_SIGN = registerBlockNoItem("polished_willow_sign",
			() -> new ModSign(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).explosionResistance(4f).strength(3).noCollission().noOcclusion(), ModWoodType.POLISHED_WILLOW));

	public static final RegistryObject<ModSign> POLISHED_WITCH_HAZEL_SIGN = registerBlockNoItem("polished_witch_hazel_sign",
			() -> new ModSign(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).explosionResistance(4f).strength(3).noCollission().noOcclusion(), ModWoodType.POLISHED_WITCH_HAZEL));

	public static final RegistryObject<ModWallSign> MAHOGANY_WALL_SIGN = registerBlockNoItem("mahogany_wall_sign",
			() -> new ModWallSign(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).explosionResistance(4f).strength(3).noCollission().noOcclusion(), ModTileEntities.SIGN_TILE::get, ModWoodType.MAHOGANY));

	public static final RegistryObject<ModWallSign> WILLOW_WALL_SIGN = registerBlockNoItem("willow_wall_sign",
			() -> new ModWallSign(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).explosionResistance(4f).strength(3).noCollission().noOcclusion(), ModTileEntities.SIGN_TILE::get, ModWoodType.WILLOW));

	public static final RegistryObject<ModWallSign> WITCH_HAZEL_WALL_SIGN = registerBlockNoItem("witch_hazel_wall_sign",
			() -> new ModWallSign(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).explosionResistance(4f).strength(3).noCollission().noOcclusion(), ModTileEntities.SIGN_TILE::get, ModWoodType.WITCH_HAZEL));

	public static final RegistryObject<ModWallSign> POLISHED_MAHOGANY_WALL_SIGN = registerBlockNoItem("polished_mahogany_wall_sign",
			() -> new ModWallSign(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).explosionResistance(4f).strength(3).noCollission().noOcclusion(), ModTileEntities.SIGN_TILE::get, ModWoodType.POLISHED_MAHOGANY));

	public static final RegistryObject<ModWallSign> POLISHED_WILLOW_WALL_SIGN = registerBlockNoItem("polished_willow_wall_sign",
			() -> new ModWallSign(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).explosionResistance(4f).strength(3).noCollission().noOcclusion(), ModTileEntities.SIGN_TILE::get, ModWoodType.POLISHED_WILLOW));

	public static final RegistryObject<ModWallSign> POLISHED_WITCH_HAZEL_WALL_SIGN = registerBlockNoItem("polished_witch_hazel_wall_sign",
			() -> new ModWallSign(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).explosionResistance(4f).strength(3).noCollission().noOcclusion(), ModTileEntities.SIGN_TILE::get, ModWoodType.POLISHED_WITCH_HAZEL));

	public static final RegistryObject<ModHangingSign> MAHOGANY_HANGING_SIGN = registerBlockNoItem("mahogany_hanging_sign",
			() -> new ModHangingSign(BlockBehaviour.Properties.copy(Blocks.OAK_HANGING_SIGN), ModWoodType.MAHOGANY));
	public static final RegistryObject<ModHangingSign> WILLOW_HANGING_SIGN = registerBlockNoItem("willow_hanging_sign",
			() -> new ModHangingSign(BlockBehaviour.Properties.copy(Blocks.OAK_HANGING_SIGN), ModWoodType.WILLOW));
	public static final RegistryObject<ModHangingSign> WITCH_HAZEL_HANGING_SIGN = registerBlockNoItem("witch_hazel_hanging_sign",
			() -> new ModHangingSign(BlockBehaviour.Properties.copy(Blocks.OAK_HANGING_SIGN), ModWoodType.WITCH_HAZEL));

	public static final RegistryObject<ModHangingWallSign> MAHOGANY_WALL_HANGING_SIGN = registerBlockNoItem("mahogany_wall_hanging_sign",
			() -> new ModHangingWallSign(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN), ModWoodType.MAHOGANY));
	public static final RegistryObject<ModHangingWallSign> WILLOW_WALL_HANGING_SIGN = registerBlockNoItem("willow_wall_hanging_sign",
			() -> new ModHangingWallSign(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN), ModWoodType.WILLOW));
	public static final RegistryObject<ModHangingWallSign> WITCH_HAZEL_WALL_HANGING_SIGN = registerBlockNoItem("witch_hazel_wall_hanging_sign",
			() -> new ModHangingWallSign(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN), ModWoodType.WITCH_HAZEL));

//    public static final RegistryObject<Block> SAGE_BUNDLE_PLATE = registerBlock("sage_bundle_plate",
//            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2).explosionResistance(2f)));

	public static final RegistryObject<SageBlock> SAGE = BLOCKS.register("sage_crop",
					() -> new SageBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT)));

	public static final RegistryObject<PestleAndMortar> PESTLE_AND_MORTAR = registerBlock("pestle_and_mortar",
					() -> new PestleAndMortar(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2).explosionResistance(2f)));

	public static final RegistryObject<SageBurningPlate> SAGE_BURNING_PLATE = registerBlock("sage_burning_plate",
					() -> new SageBurningPlate(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1).explosionResistance(2f)));

	public static final RegistryObject<CrystalBall> CRYSTAL_BALL = registerBlock("crystal_ball",
					() -> new CrystalBall(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(2f).lightLevel(state -> 9)));

	public static final RegistryObject<HerbJar> HERB_JAR = registerBlockNoItem("herb_jar",
					() -> new HerbJar(BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).strength(1).explosionResistance(0.5f).strength(1f, 1f).sound(SoundType.GLASS)));

	public static final RegistryObject<HerbDryingRackFull> HERB_DRYING_RACK_FULL = registerBlock("herb_drying_rack_full",
					() -> new HerbDryingRackFull(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1).explosionResistance(2f)));

	public static final RegistryObject<HerbDryingRack> HERB_DRYING_RACK = registerBlock("herb_drying_rack",
					() -> new HerbDryingRack(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1).explosionResistance(2f)));

	public static final RegistryObject<WallDryingRack> MAHOGANY_DRYING_RACK = registerBlock("mahogany_drying_rack",
			() -> new WallDryingRack(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1).explosionResistance(2f)));

	public static final RegistryObject<WallDryingRack> WILLOW_DRYING_RACK = registerBlock("willow_drying_rack",
			() -> new WallDryingRack(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1).explosionResistance(2f)));

	public static final RegistryObject<WallDryingRack> WITCH_HAZEL_DRYING_RACK = registerBlock("witch_hazel_drying_rack",
			() -> new WallDryingRack(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1).explosionResistance(2f)));


	public static final RegistryObject<Candelabra> CANDELABRA = registerBlock("candelabra",
					() -> new Candelabra(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1).explosionResistance(2f).lightLevel(state -> state.getValue(Candelabra.LIT) ? 15 : 0)));

	public static final RegistryObject<Candle> CANDLE = registerBlockNoItem("candle",
					() -> new Candle(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().noOcclusion().strength(1).explosionResistance(0.5f).lightLevel(state -> Math.min(state.getValue(Candle.CANDLES_LIT) * 12, 15))));

// MAHOGANY
	public static final RegistryObject<MahoganyLog> MAHOGANY_LOG = registerBlock("mahogany_log",
					() -> new MahoganyLog(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));

	public static final RegistryObject<MahoganyWood> MAHOGANY_WOOD = registerBlock("mahogany_wood",
					() -> new MahoganyWood(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));

	public static final RegistryObject<RotatedPillarBlock> STRIPPED_MAHOGANY_LOG = registerBlock("stripped_mahogany_log",
					() -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));

	public static final RegistryObject<RotatedPillarBlock> STRIPPED_MAHOGANY_WOOD = registerBlock("stripped_mahogany_wood",
					() -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));

	public static final RegistryObject<Block> MAHOGANY_PLANKS = registerBlock("mahogany_planks",
			() -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<StairBlock> MAHOGANY_STAIRS = registerBlock("mahogany_stairs",
					() -> new StairBlock(() -> MAHOGANY_PLANKS.get().defaultBlockState(),
									BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<FenceBlock> MAHOGANY_FENCE = registerBlock("mahogany_fence",
					() -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<FenceGateBlock> MAHOGANY_FENCE_GATE = registerBlock("mahogany_fence_gate",
					() -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodType.MAHOGANY));

	public static final RegistryObject<SlabBlock> MAHOGANY_SLAB = registerBlock("mahogany_slab",
					() -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<ButtonBlock> MAHOGANY_BUTTON = registerBlock("mahogany_button",
					() -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), ModBlockSetType.MAHOGANY, 30, false));

	public static final RegistryObject<PressurePlateBlock> MAHOGANY_PRESSURE_PLATE = registerBlock("mahogany_pressure_plate",
					() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noCollission(), ModBlockSetType.MAHOGANY));

	public static final RegistryObject<DoorBlock> MAHOGANY_DOOR = registerBlock("mahogany_door",
					() -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.MAHOGANY));

	public static final RegistryObject<TrapDoorBlock> MAHOGANY_TRAPDOOR = registerBlock("mahogany_trapdoor",
					() -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.MAHOGANY));

	public static final RegistryObject<LeavesBlock> MAHOGANY_LEAVES = registerBlock("mahogany_leaves",
					() -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).randomTicks().sound(SoundType.AZALEA_LEAVES).noOcclusion().isSuffocating(Properties::never).isViewBlocking(Properties::never)){
						public void animateTick(BlockState p_272714_, Level p_272837_, BlockPos p_273218_, RandomSource p_273360_) {
							super.animateTick(p_272714_, p_272837_, p_273218_, p_273360_);
							if (p_273360_.nextInt(10) == 0) {
								BlockPos blockpos = p_273218_.below();
								BlockState blockstate = p_272837_.getBlockState(blockpos);
								if (!isFaceFull(blockstate.getCollisionShape(p_272837_, blockpos), Direction.UP)) {
									ParticleUtils.spawnParticleBelow(p_272837_, p_273218_, p_273360_, ModParticleTypes.MAHOGANY_LEAVES.get());
								}
							}
						}
					});

	public static final RegistryObject<SaplingBlock> MAHOGANY_SAPLING = registerBlock("mahogany_sapling",
					() -> new SaplingBlock(new MahoganyTree(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));

	public static final RegistryObject<Block> POLISHED_MAHOGANY_PLANKS = registerBlock("polished_mahogany_planks",
			() -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<StairBlock> POLISHED_MAHOGANY_STAIRS = registerBlock("polished_mahogany_stairs",
			() -> new StairBlock(() -> POLISHED_MAHOGANY_PLANKS.get().defaultBlockState(),
					BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<FenceBlock> POLISHED_MAHOGANY_FENCE = registerBlock("polished_mahogany_fence",
			() -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<FenceGateBlock> POLISHED_MAHOGANY_FENCE_GATE = registerBlock("polished_mahogany_fence_gate",
			() -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodType.MAHOGANY));

	public static final RegistryObject<SlabBlock> POLISHED_MAHOGANY_SLAB = registerBlock("polished_mahogany_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<ButtonBlock> POLISHED_MAHOGANY_BUTTON = registerBlock("polished_mahogany_button",
			() -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), ModBlockSetType.POLISHED_MAHOGANY, 30, false));

	public static final RegistryObject<PressurePlateBlock> POLISHED_MAHOGANY_PRESSURE_PLATE = registerBlock("polished_mahogany_pressure_plate",
			() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noCollission(), ModBlockSetType.POLISHED_MAHOGANY));

	public static final RegistryObject<DoorBlock> POLISHED_MAHOGANY_DOOR = registerBlock("polished_mahogany_door",
			() -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.POLISHED_MAHOGANY));

	public static final BlockEntry<TrapDoorBlock> POLISHED_MAHOGANY_TRAPDOOR = REGISTRATE.block("polished_mahogany_trapdoor", (properties) -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.POLISHED_MAHOGANY))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new TrapdoorCTBehaviour(AllSpriteShifts.POLISHED_MAHOGANY_TRAPDOOR)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_MAHOGANY_TRAPDOOR)))
			.register();


//	WILLOW

	public static final RegistryObject<WillowLog> WILLOW_LOG = registerBlock("willow_log",
					() -> new WillowLog(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));

	public static final RegistryObject<WillowWood> WILLOW_WOOD = registerBlock("willow_wood",
					() -> new WillowWood(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));

	public static final RegistryObject<RotatedPillarBlock> STRIPPED_WILLOW_LOG = registerBlock("stripped_willow_log",
					() -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));

	public static final RegistryObject<RotatedPillarBlock> STRIPPED_WILLOW_WOOD = registerBlock("stripped_willow_wood",
					() -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));

	public static final RegistryObject<Block> WILLOW_PLANKS = registerBlock("willow_planks",
					() -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<StairBlock> WILLOW_STAIRS = registerBlock("willow_stairs",
					() -> new StairBlock(() -> WILLOW_PLANKS.get().defaultBlockState(),
									BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<Block> WILLOW_VINES = registerBlock("willow_vines",
					() -> new WillowVinesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().instabreak().sound(SoundType.WEEPING_VINES)));

	public static final RegistryObject<Block> WILLOW_VINES_PLANT = registerBlockNoItem("willow_vines_plant",
					() -> new WillowVinesPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().instabreak().sound(SoundType.WEEPING_VINES)));

	public static final RegistryObject<FenceBlock> WILLOW_FENCE = registerBlock("willow_fence",
					() -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<FenceGateBlock> WILLOW_FENCE_GATE = registerBlock("willow_fence_gate",
					() -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodType.WILLOW));

	public static final RegistryObject<SlabBlock> WILLOW_SLAB = registerBlock("willow_slab",
					() -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<ButtonBlock> WILLOW_BUTTON = registerBlock("willow_button",
					() -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), ModBlockSetType.WILLOW, 30, false));

	public static final RegistryObject<PressurePlateBlock> WILLOW_PRESSURE_PLATE = registerBlock("willow_pressure_plate",
					() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noCollission(), ModBlockSetType.WILLOW));

	public static final RegistryObject<DoorBlock> WILLOW_DOOR = registerBlock("willow_door",
					() -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.WILLOW));

	public static final RegistryObject<TrapDoorBlock> WILLOW_TRAPDOOR = registerBlock("willow_trapdoor",
					() -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.WILLOW));

	public static final RegistryObject<LeavesBlock> WILLOW_LEAVES = registerBlock("willow_leaves",
					() -> new LeavesBlock(BlockBehaviour.Properties.of().ignitedByLava().pushReaction(PushReaction.DESTROY).strength(0.2F).randomTicks().sound(SoundType.AZALEA_LEAVES).noOcclusion().isSuffocating(Properties::never).isViewBlocking(Properties::never)));

	public static final RegistryObject<SaplingBlock> WILLOW_SAPLING = registerBlock("willow_sapling",
					() -> new SaplingBlock(new WillowTree(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));


	public static final RegistryObject<Block> POLISHED_WILLOW_PLANKS = registerBlock("polished_willow_planks",
			() -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<StairBlock> POLISHED_WILLOW_STAIRS = registerBlock("polished_willow_stairs",
			() -> new StairBlock(() -> POLISHED_WILLOW_PLANKS.get().defaultBlockState(),
					BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<FenceBlock> POLISHED_WILLOW_FENCE = registerBlock("polished_willow_fence",
			() -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<FenceGateBlock> POLISHED_WILLOW_FENCE_GATE = registerBlock("polished_willow_fence_gate",
			() -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodType.POLISHED_WILLOW));

	public static final RegistryObject<SlabBlock> POLISHED_WILLOW_SLAB = registerBlock("polished_willow_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<ButtonBlock> POLISHED_WILLOW_BUTTON = registerBlock("polished_willow_button",
			() -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), ModBlockSetType.POLISHED_WILLOW, 30, false));

	public static final RegistryObject<PressurePlateBlock> POLISHED_WILLOW_PRESSURE_PLATE = registerBlock("polished_willow_pressure_plate",
			() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noCollission(), ModBlockSetType.POLISHED_WILLOW));

	public static final RegistryObject<DoorBlock> POLISHED_WILLOW_DOOR = registerBlock("polished_willow_door",
			() -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.POLISHED_WILLOW));

//	public static final RegistryObject<TrapDoorBlock> POLISHED_WILLOW_TRAPDOOR = registerBlock("polished_willow_trapdoor",
//			() -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.POLISHED_WILLOW));
	public static final BlockEntry<TrapDoorBlock> POLISHED_WILLOW_TRAPDOOR = REGISTRATE.block("polished_willow_trapdoor", (properties) -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.POLISHED_WILLOW))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new TrapdoorCTBehaviour(AllSpriteShifts.POLISHED_WILLOW_TRAPDOOR)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_WILLOW_TRAPDOOR)))
			.register();

// WITCH HAZEL


	public static final RegistryObject<WitchHazelLog> WITCH_HAZEL_LOG = registerBlock("witch_hazel_log",
			() -> new WitchHazelLog(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));

	public static final RegistryObject<WitchHazelWood> WITCH_HAZEL_WOOD = registerBlock("witch_hazel_wood",
			() -> new WitchHazelWood(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));

	public static final RegistryObject<RotatedPillarBlock> STRIPPED_WITCH_HAZEL_LOG = registerBlock("stripped_witch_hazel_log",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));

	public static final RegistryObject<RotatedPillarBlock> STRIPPED_WITCH_HAZEL_WOOD = registerBlock("stripped_witch_hazel_wood",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));

	public static final RegistryObject<Block> WITCH_HAZEL_PLANKS = registerBlock("witch_hazel_planks",
			() -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<StairBlock> WITCH_HAZEL_STAIRS = registerBlock("witch_hazel_stairs",
			() -> new StairBlock(() -> WITCH_HAZEL_PLANKS.get().defaultBlockState(),
					BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
//
//	public static final RegistryObject<Block> WITCH_HAZEL_VINES = registerBlock("witch_hazel_vines",
//			() -> new WillowVinesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().instabreak().sound(SoundType.WEEPING_VINES)));
//
//	public static final RegistryObject<Block> WITCH_HAZEL_VINES_PLANT = registerBlockNoItem("witch_hazel_vines_plant",
//			() -> new WillowVinesPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().instabreak().sound(SoundType.WEEPING_VINES)));

	public static final RegistryObject<FenceBlock> WITCH_HAZEL_FENCE = registerBlock("witch_hazel_fence",
			() -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<FenceGateBlock> WITCH_HAZEL_FENCE_GATE = registerBlock("witch_hazel_fence_gate",
			() -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodType.WITCH_HAZEL));

	public static final RegistryObject<SlabBlock> WITCH_HAZEL_SLAB = registerBlock("witch_hazel_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<ButtonBlock> WITCH_HAZEL_BUTTON = registerBlock("witch_hazel_button",
			() -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), ModBlockSetType.WITCH_HAZEL, 30, false));

	public static final RegistryObject<PressurePlateBlock> WITCH_HAZEL_PRESSURE_PLATE = registerBlock("witch_hazel_pressure_plate",
			() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noCollission(), ModBlockSetType.WITCH_HAZEL));

	public static final RegistryObject<DoorBlock> WITCH_HAZEL_DOOR = registerBlock("witch_hazel_door",
			() -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.WITCH_HAZEL));

	public static final RegistryObject<TrapDoorBlock> WITCH_HAZEL_TRAPDOOR = registerBlock("witch_hazel_trapdoor",
			() -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.WITCH_HAZEL));

	public static final RegistryObject<LeavesBlock> WITCH_HAZEL_LEAVES = registerBlock("witch_hazel_leaves",
			() -> new LeavesBlock(BlockBehaviour.Properties.of().ignitedByLava().pushReaction(PushReaction.DESTROY).strength(0.2F).randomTicks().sound(SoundType.AZALEA_LEAVES).noOcclusion().isSuffocating(Properties::never).isViewBlocking(Properties::never)){
				public void animateTick(BlockState p_272714_, Level p_272837_, BlockPos p_273218_, RandomSource p_273360_) {
					super.animateTick(p_272714_, p_272837_, p_273218_, p_273360_);
					if (p_273360_.nextInt(10) == 0) {
						BlockPos blockpos = p_273218_.below();
						BlockState blockstate = p_272837_.getBlockState(blockpos);
						if (!isFaceFull(blockstate.getCollisionShape(p_272837_, blockpos), Direction.UP)) {
							ParticleUtils.spawnParticleBelow(p_272837_, p_273218_, p_273360_, ModParticleTypes.WITCH_HAZEL_LEAVES.get());
						}
					}
				}
			});

	public static final RegistryObject<SaplingBlock> WITCH_HAZEL_SAPLING = registerBlock("witch_hazel_sapling",
			() -> new SaplingBlock(new WitchHazelTree(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));


	public static final RegistryObject<Block> POLISHED_WITCH_HAZEL_PLANKS = registerBlock("polished_witch_hazel_planks",
			() -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<StairBlock> POLISHED_WITCH_HAZEL_STAIRS = registerBlock("polished_witch_hazel_stairs",
			() -> new StairBlock(() -> POLISHED_WITCH_HAZEL_PLANKS.get().defaultBlockState(),
					BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<FenceBlock> POLISHED_WITCH_HAZEL_FENCE = registerBlock("polished_witch_hazel_fence",
			() -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<FenceGateBlock> POLISHED_WITCH_HAZEL_FENCE_GATE = registerBlock("polished_witch_hazel_fence_gate",
			() -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodType.POLISHED_WITCH_HAZEL));

	public static final RegistryObject<SlabBlock> POLISHED_WITCH_HAZEL_SLAB = registerBlock("polished_witch_hazel_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

	public static final RegistryObject<ButtonBlock> POLISHED_WITCH_HAZEL_BUTTON = registerBlock("polished_witch_hazel_button",
			() -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), ModBlockSetType.POLISHED_WITCH_HAZEL, 30, false));

	public static final RegistryObject<PressurePlateBlock> POLISHED_WITCH_HAZEL_PRESSURE_PLATE = registerBlock("polished_witch_hazel_pressure_plate",
			() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noCollission(), ModBlockSetType.POLISHED_WITCH_HAZEL));

	public static final RegistryObject<DoorBlock> POLISHED_WITCH_HAZEL_DOOR = registerBlock("polished_witch_hazel_door",
			() -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.POLISHED_WITCH_HAZEL));

//	public static final RegistryObject<TrapDoorBlock> POLISHED_WITCH_HAZEL_TRAPDOOR = registerBlock("polished_witch_hazel_trapdoor",
//			() -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.POLISHED_WITCH_HAZEL));
	public static final BlockEntry<TrapDoorBlock> POLISHED_WITCH_HAZEL_TRAPDOOR = REGISTRATE.block("polished_witch_hazel_trapdoor", (properties) -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), ModBlockSetType.POLISHED_WITCH_HAZEL))
			.properties((p) -> BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS_PANE).mapColor(MapColor.TERRACOTTA_RED))
			.onRegister(connectedTextures(() -> new TrapdoorCTBehaviour(AllSpriteShifts.POLISHED_WITCH_HAZEL_TRAPDOOR)))
			.onRegister(blockConnectivity((block, cc) -> cc.makeBlock(block, AllSpriteShifts.POLISHED_WITCH_HAZEL_TRAPDOOR)))
			.register();

	public static final RegistryObject<FloweringLilyPadBlock> LILY_PAD_BLOCK = registerBlockNoItem("flowering_lily_pad",
					() -> new FloweringLilyPadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).instabreak().sound(SoundType.LILY_PAD).noOcclusion()));

	public static final RegistryObject<PickablePlant> MANDRAKE_PLANT = registerBlock("mandrake_plant",
					() -> new PickablePlant(MobEffects.REGENERATION, 3, BlockBehaviour.Properties.copy(Blocks.DANDELION), ModItems.MANDRAKE_FLOWERS, 2, ModItems.MANDRAKE_ROOT, 1) {


						@Override
						public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

							tooltip.add(Component.translatable("tooltip.hexerei.found_in_swamp").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
							super.appendHoverText(stack, world, tooltip, flagIn);
						}
					});

	public static final RegistryObject<PickablePlant> BELLADONNA_PLANT = registerBlock("belladonna_plant",
					() -> new PickablePlant(MobEffects.POISON, 3, BlockBehaviour.Properties.copy(Blocks.DANDELION), ModItems.BELLADONNA_FLOWERS, 2, ModItems.BELLADONNA_BERRIES, 6) {


						@Override
						public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

							tooltip.add(Component.translatable("tooltip.hexerei.found_in_swamp").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
							super.appendHoverText(stack, world, tooltip, flagIn);
						}
					});

//            () -> new PickableFlower(MobEffects.POISON, 3, BlockBehaviour.Properties.copy(Blocks.DANDELION), new ItemStack(ModItems.BELLADONNA_FLOWERS.get(), 4),  new ItemStack(ModItems.BELLADONNA_BERRIES.get(), 5), 5));

	public static final RegistryObject<PickableDoublePlant> MUGWORT_BUSH = registerBlock("mugwort_bush",
					() -> new PickableDoublePlant(MobEffects.GLOWING, 10, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().instabreak().sound(SoundType.AZALEA), ModItems.MUGWORT_LEAVES, 4, ModItems.MUGWORT_FLOWERS, 3) {


						@Override
						public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

							tooltip.add(Component.translatable("tooltip.hexerei.found_in_swamp").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
							super.appendHoverText(stack, world, tooltip, flagIn);
						}
					});

	public static final RegistryObject<PickableDoublePlant> YELLOW_DOCK_BUSH = registerBlock("yellow_dock_bush",
					() -> new PickableDoublePlant(MobEffects.GLOWING, 10, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().instabreak().sound(SoundType.AZALEA), ModItems.YELLOW_DOCK_LEAVES, 4, ModItems.YELLOW_DOCK_FLOWERS, 3) {


						@Override
						public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

							tooltip.add(Component.translatable("tooltip.hexerei.found_in_swamp").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
							super.appendHoverText(stack, world, tooltip, flagIn);
						}
					});

	public static final RegistryObject<Block> POTTED_MANDRAKE_PLANT = registerBlockNoItem("potted_mandrake_plant",
					() -> new FlowerPotBlock(ModBlocks.MANDRAKE_PLANT.get(), BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));

	public static final RegistryObject<Block> POTTED_BELLADONNA_PLANT = registerBlockNoItem("potted_belladonna_plant",
					() -> new FlowerPotBlock(ModBlocks.BELLADONNA_PLANT.get(), BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));

	public static final RegistryObject<Block> POTTED_YELLOW_DOCK_BUSH = registerBlockNoItem("potted_yellow_dock_bush",
					() -> new FlowerPotBlock(ModBlocks.YELLOW_DOCK_BUSH.get(), BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));

	public static final RegistryObject<Block> POTTED_MUGWORT_BUSH = registerBlockNoItem("potted_mugwort_bush",
					() -> new FlowerPotBlock(ModBlocks.MUGWORT_BUSH.get(), BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));


	public static final RegistryObject<AmethystBlock> SELENITE_BLOCK = registerBlock("selenite_block",
					() -> new AmethystBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(0.5F).sound(SoundType.AMETHYST).noOcclusion()) {
						public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
							return ((pSide == Direction.UP || pSide == Direction.DOWN) && pAdjacentBlockState.is(this)) || super.skipRendering(pState, pAdjacentBlockState, pSide);
						}
					});

	public static final RegistryObject<AmethystBlock> BUDDING_SELENITE = registerBlock("budding_selenite",
					() -> new BuddingSelenite(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).randomTicks().strength(1.0F).sound(SoundType.AMETHYST).noOcclusion()) {
						public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
							return ((pSide == Direction.UP || pSide == Direction.DOWN) && pAdjacentBlockState.is(this)) || super.skipRendering(pState, pAdjacentBlockState, pSide);
						}
					});

	public static final RegistryObject<AmethystBlock> SELENITE_CLUSTER = registerBlock("selenite_cluster",
			() -> new AmethystClusterBlock(7, 3, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).noOcclusion().strength(0.5F).randomTicks().sound(SoundType.AMETHYST_CLUSTER).noOcclusion().lightLevel((p_152632_) -> 5)));

	public static final RegistryObject<AmethystBlock> LARGE_SELENITE_BUD = registerBlock("large_selenite_bud",
			() -> new AmethystClusterBlock(5, 3, BlockBehaviour.Properties.copy(Blocks.AMETHYST_CLUSTER).sound(SoundType.MEDIUM_AMETHYST_BUD).noOcclusion().lightLevel((p_152629_) -> 4)));

	public static final RegistryObject<AmethystBlock> MEDIUM_SELENITE_BUD = registerBlock("medium_selenite_bud",
			() -> new AmethystClusterBlock(4, 3, BlockBehaviour.Properties.copy(Blocks.AMETHYST_CLUSTER).sound(SoundType.LARGE_AMETHYST_BUD).noOcclusion().lightLevel((p_152617_) -> 2)));

	public static final RegistryObject<AmethystBlock> SMALL_SELENITE_BUD = registerBlock("small_selenite_bud",
			() -> new AmethystClusterBlock(3, 4, BlockBehaviour.Properties.copy(Blocks.AMETHYST_CLUSTER).sound(SoundType.SMALL_AMETHYST_BUD).noOcclusion().lightLevel((p_187409_) -> 1)));

	// SIGILS
	public static final RegistryObject<Block> BLOOD_SIGIL = registerBlockNoItem("blood_sigil",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2).requiresCorrectToolForDrops().explosionResistance(5f)));

	// BLOCK PARTIALS

	public static final RegistryObject<Block> MIXING_CAULDRON_DYE = registerBlockNoItem("mixing_cauldron_dye",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2).requiresCorrectToolForDrops().explosionResistance(5f)));

	public static final RegistryObject<Block> CANDLE_DIPPER_WICK_BASE = registerBlockNoItem("candle_dipper_wick_base",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0).noCollission().explosionResistance(1f)));

	public static final RegistryObject<Block> CANDLE_DIPPER_WICK = registerBlockNoItem("candle_dipper_wick",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).strength(0).noCollission().explosionResistance(1f)));

	public static final RegistryObject<Block> CANDLE_DIPPER_CANDLE_1 = registerBlockNoItem("candle_dipper_candle_1",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).strength(0).noCollission().explosionResistance(1f)));

	public static final RegistryObject<Block> CANDLE_DIPPER_CANDLE_2 = registerBlockNoItem("candle_dipper_candle_2",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).strength(0).noCollission().explosionResistance(1f)));

	public static final RegistryObject<Block> CANDLE_DIPPER_CANDLE_3 = registerBlockNoItem("candle_dipper_candle_3",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).strength(0).noCollission().explosionResistance(1f)));
	public static final RegistryObject<Block> CRYSTAL_BALL_ORB = registerBlockNoItem("crystal_ball_orb",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> CRYSTAL_BALL_LARGE_RING = registerBlockNoItem("crystal_ball_large_ring",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> CRYSTAL_BALL_SMALL_RING = registerBlockNoItem("crystal_ball_small_ring",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> CRYSTAL_BALL_STAND = registerBlockNoItem("crystal_ball_stand",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> BOOK_OF_SHADOWS_COVER = registerBlockNoItem("book_of_shadows_cover",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> BOOK_OF_SHADOWS_COVER_CORNERS = registerBlockNoItem("book_of_shadows_cover_corners",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> BOOK_OF_SHADOWS_BACK = registerBlockNoItem("book_of_shadows_back",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> BOOK_OF_SHADOWS_BACK_CORNERS = registerBlockNoItem("book_of_shadows_back_corners",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> BOOK_OF_SHADOWS_BINDING = registerBlockNoItem("book_of_shadows_binding",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> BOOK_OF_SHADOWS_PAGE = registerBlockNoItem("book_of_shadows_page_blank",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0).explosionResistance(2f)));

	public static final RegistryObject<Block> COFFER_CHEST = registerBlockNoItem("coffer_chest",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3).requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> COFFER_LID = registerBlockNoItem("coffer_lid",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3).requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> COFFER_CONTAINER = registerBlockNoItem("coffer_container",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> COFFER_HINGE = registerBlockNoItem("coffer_hinge",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_JAR_GENERIC = registerBlockNoItem("herb_jar_generic",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_JAR_BELLADONNA = registerBlockNoItem("herb_jar_belladonna",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_JAR_MANDRAKE_PLANT = registerBlockNoItem("herb_jar_mandrake_plant",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_JAR_MANDRAKE_ROOT = registerBlockNoItem("herb_jar_mandrake_root",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_JAR_MUGWORT = registerBlockNoItem("herb_jar_mugwort",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_JAR_YELLOW_DOCK = registerBlockNoItem("herb_jar_yellow_dock",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_DRYING_RACK_BROWN_MUSHROOM_1 = registerBlockNoItem("herb_drying_rack_brown_mushroom_1",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_DRYING_RACK_BROWN_MUSHROOM_2 = registerBlockNoItem("herb_drying_rack_brown_mushroom_2",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_DRYING_RACK_RED_MUSHROOM_1 = registerBlockNoItem("herb_drying_rack_red_mushroom_1",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> HERB_DRYING_RACK_RED_MUSHROOM_2 = registerBlockNoItem("herb_drying_rack_red_mushroom_2",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> PESTLE_AND_MORTAR_PESTLE = registerBlockNoItem("pestle_and_mortar_pestle",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_5 = registerBlockNoItem("dried_sage_bundle_plate_5",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_4 = registerBlockNoItem("dried_sage_bundle_plate_4",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_3 = registerBlockNoItem("dried_sage_bundle_plate_3",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_2 = registerBlockNoItem("dried_sage_bundle_plate_2",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_1 = registerBlockNoItem("dried_sage_bundle_plate_1",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_5_LIT = registerBlockNoItem("dried_sage_bundle_plate_5_lit",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_4_LIT = registerBlockNoItem("dried_sage_bundle_plate_4_lit",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_3_LIT = registerBlockNoItem("dried_sage_bundle_plate_3_lit",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_2_LIT = registerBlockNoItem("dried_sage_bundle_plate_2_lit",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));

	public static final RegistryObject<Block> DRIED_SAGE_BUNDLE_PLATE_1_LIT = registerBlockNoItem("dried_sage_bundle_plate_1_lit",
					() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2).explosionResistance(8f)));


	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		registerBlockItem(name, toReturn);
		return toReturn;
	}

	private static <T extends Block> RegistryObject<T> registerBlockNoItem(String name, Supplier<T> block) {
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		return toReturn;
	}

	private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
		ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
						new BlockItem.Properties()));
	}

	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);
	}

	public static class Properties {

		public static boolean always(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
			return true;
		}

		public static boolean hasPostProcess(BlockState state, BlockGetter reader, BlockPos pos) {
			return true;
		}

		public static boolean never(BlockState state, BlockGetter reader, BlockPos pos) {
			return false;
		}
	}


//    private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup) {
//        return register(name, sup, block -> item(block));
//    }
//
//    private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup, Function<RegistryObject<T>, Supplier<? extends Item>> itemCreator) {
//        RegistryObject<T> ret = registerNoItem(name, sup);
//        ITEMS.register(name, itemCreator.apply(ret));
//        return ret;
//    }
//
//    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<? extends T> sup) {
//        return BLOCKS.register(name, sup);
//    }
//
//    private static Supplier<BlockItem> item(final RegistryObject<? extends Block> block) {
//        return () -> new BlockItem(block.get(), new Item.Properties().tab(ModItemGroup.HEXEREI_GROUP));
//    }


}
