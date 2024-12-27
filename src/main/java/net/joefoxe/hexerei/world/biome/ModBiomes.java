package net.joefoxe.hexerei.world.biome;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.world.gen.ModConfiguredFeatures;
import net.joefoxe.hexerei.world.gen.ModPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.CavePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.registries.NeoForgeRegistries;


public class ModBiomes {
	public static final ResourceKey<Biome> WILLOW_SWAMP = ResourceKey.create(Registries.BIOME, HexereiUtil.getResource("willow_swamp"));

	public static void bootstrap(BootstrapContext<Biome> context) {
//		context.register(WILLOW_SWAMP, makeWillowSwampBiome(context));
	}

	private static Biome makeWillowSwampBiome(BootstrapContext<Biome> context) {
		MobSpawnSettings.Builder mobSpawnSettingsBuilder = new MobSpawnSettings.Builder();
		HolderGetter<PlacedFeature> placedFeature = context.lookup(Registries.PLACED_FEATURE);

		BiomeDefaultFeatures.farmAnimals(mobSpawnSettingsBuilder);
		BiomeDefaultFeatures.commonSpawns(mobSpawnSettingsBuilder);
		BiomeDefaultFeatures.caveSpawns(mobSpawnSettingsBuilder);
		mobSpawnSettingsBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 1, 1, 1));
		mobSpawnSettingsBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 10, 2, 5));

		BiomeGenerationSettings.Builder genSettingsBuilder = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
		BiomeDefaultFeatures.addFossilDecoration(genSettingsBuilder);
		globalOverworldGeneration(genSettingsBuilder);
		BiomeDefaultFeatures.addDefaultOres(genSettingsBuilder);
		BiomeDefaultFeatures.addSwampClayDisk(genSettingsBuilder);
		BiomeDefaultFeatures.addWaterTrees(genSettingsBuilder);
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_SWAMP);
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL);
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH);
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_WATERLILY);
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_SWAMP);
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_SWAMP);
		BiomeDefaultFeatures.addDefaultMushrooms(genSettingsBuilder);
		BiomeDefaultFeatures.addSwampExtraVegetation(genSettingsBuilder);
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SWAMP);
		BiomeDefaultFeatures.addJungleVines(genSettingsBuilder);
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.LUSH_CAVES_CEILING_VEGETATION);
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.CAVE_VINES);
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.ROOTED_AZALEA_TREE);
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.SPORE_BLOSSOM);
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.CLASSIC_VINES);
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, placedFeature.getOrThrow(ModPlacedFeatures.FLOWERING_LILYPAD_PLACED_KEY));
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, placedFeature.getOrThrow(ModPlacedFeatures.SWAMP_FLOWERS_PLACED_KEY));
		genSettingsBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, placedFeature.getOrThrow(ModPlacedFeatures.TREES_WILLOW_SWAMP_PLACED_KEY));

		BiomeSpecialEffects.Builder specialEffectsBuilder = new BiomeSpecialEffects.Builder();
		specialEffectsBuilder.waterColor(6388580)
				.waterFogColor(2302743)
				.fogColor(12638463)
				.skyColor(getSkyColorWithTemperatureModifier(0.8F))
				.foliageColorOverride(6975545)
				.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP)
				.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS);

		return (new Biome.BiomeBuilder())
				.hasPrecipitation(true)
				.temperature(0.8F)
				.downfall(0.9F)
				.specialEffects(specialEffectsBuilder.build())
				.mobSpawnSettings(mobSpawnSettingsBuilder.build())
				.generationSettings(genSettingsBuilder.build())
				.build();
	}


	private static int getSkyColorWithTemperatureModifier(float temperature) {
		float lvt_1_1_ = temperature / 3.0F;
		lvt_1_1_ = Mth.clamp(lvt_1_1_, -1.0F, 1.0F);
		return Mth.hsvToRgb(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
	}

	private static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder) {
		BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
		BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
		BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
		BiomeDefaultFeatures.addDefaultSprings(builder);
		BiomeDefaultFeatures.addSurfaceFreezing(builder);
	}
}