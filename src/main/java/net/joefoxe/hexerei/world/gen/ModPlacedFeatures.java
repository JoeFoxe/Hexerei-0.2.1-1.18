package net.joefoxe.hexerei.world.gen;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> SELENITE_GEODE_PLACED_KEY = registerKey("selenite_geode_placed");
    public static final ResourceKey<PlacedFeature> COMMON_SWAMP_FLOWERS_PLACED_KEY = registerKey("common_swamp_flowers_placed");
    public static final ResourceKey<PlacedFeature> SWAMP_FLOWERS_PLACED_KEY = registerKey("swamp_flowers_placed");
    public static final ResourceKey<PlacedFeature> WILLOW_PLACED_KEY = registerKey("willow_placed");
    public static final ResourceKey<PlacedFeature> WITCH_HAZEL_PLACED_KEY = registerKey("witch_hazel_placed");
    public static final ResourceKey<PlacedFeature> MAHOGANY_PLACED_KEY = registerKey("mahogany_placed");
    public static final ResourceKey<PlacedFeature> TREES_WILLOW_SWAMP_PLACED_KEY = registerKey("trees_willow_swamp_placed");
    public static final ResourceKey<PlacedFeature> FLOWERING_LILYPAD_PLACED_KEY = registerKey("flowering_lilypad_placed");
    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(Hexerei.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeature = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, SELENITE_GEODE_PLACED_KEY, configuredFeature.getOrThrow(ModConfiguredFeatures.SELENITE_GEODE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(24), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(30)), BiomeFilter.biome()));

        register(context, WILLOW_PLACED_KEY, configuredFeature.getOrThrow(ModConfiguredFeatures.WILLOW_KEY), List.of(PlacementUtils.filteredByBlockSurvival(ModBlocks.WILLOW_SAPLING.get())));

        register(context, WITCH_HAZEL_PLACED_KEY, configuredFeature.getOrThrow(ModConfiguredFeatures.WITCH_HAZEL_KEY), List.of(PlacementUtils.filteredByBlockSurvival(ModBlocks.WITCH_HAZEL_SAPLING.get())));

        register(context, MAHOGANY_PLACED_KEY, configuredFeature.getOrThrow(ModConfiguredFeatures.MAHOGANY_KEY), List.of(PlacementUtils.filteredByBlockSurvival(ModBlocks.MAHOGANY_SAPLING.get())));

        register(context, TREES_WILLOW_SWAMP_PLACED_KEY, configuredFeature.getOrThrow(ModConfiguredFeatures.WILLOW_KEY), List.of(PlacementUtils.countExtra(2, 0.1F, 1),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(2),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome(),
                BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO))));

        register(context, SWAMP_FLOWERS_PLACED_KEY, configuredFeature.getOrThrow(ModConfiguredFeatures.SWAMP_FLOWERS_KEY), List.of(CountPlacement.of(3), RarityFilter.onAverageOnceEvery(1), InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP, BiomeFilter.biome(), PlacementUtils.HEIGHTMAP));

        register(context, FLOWERING_LILYPAD_PLACED_KEY, configuredFeature.getOrThrow(ModConfiguredFeatures.FLOWERING_LILYPAD_KEY), VegetationPlacements.worldSurfaceSquaredWithCount(4));

    }

}
