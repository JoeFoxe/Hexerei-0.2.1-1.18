//package net.joefoxe.hexerei.data.datagen;
//
//import net.joefoxe.hexerei.Hexerei;
//import net.joefoxe.hexerei.world.biome.ModBiomes;
//import net.joefoxe.hexerei.world.gen.ModConfiguredFeatures;
//import net.joefoxe.hexerei.world.gen.ModPlacedFeatures;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.core.RegistrySetBuilder;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.data.PackOutput;
//import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
//
//import java.util.Set;
//import java.util.concurrent.CompletableFuture;
//
//public class WorldGenProvider extends DatapackBuiltinEntriesProvider {
//
//	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
//			.add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
//			.add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
//			.add(BuiltInRegistries.Keys.BIOMES, ModBiomes::bootstrap);
//
//	public WorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
//		super(output, registries, BUILDER, Set.of(Hexerei.MOD_ID));
//	}
//}
