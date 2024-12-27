package net.joefoxe.hexerei.world.biomemods;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModBiomeModifiers {
//    public static final ResourceKey<BiomeModifier> ADD_SAPPHIRE_ORE = registerKey("add_sapphire_ore");
//    public static final ResourceKey<BiomeModifier> ADD_NETHER_SAPPHIRE_ORE = registerKey("add_nether_sapphire_ore");
//    public static final ResourceKey<BiomeModifier> ADD_END_SAPPHIRE_ORE = registerKey("add_end_sapphire_ore");
//
//
//    public static void bootstrap(BootstapContext<BiomeModifier> context) {
//        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
//        var biomes = context.lookup(Registries.BIOME);
//
//        context.register(ADD_SAPPHIRE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
//                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
//                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SAPPHIRE_ORE_PLACED_KEY)),
//                GenerationStep.Decoration.UNDERGROUND_ORES));
//
//        context.register(ADD_NETHER_SAPPHIRE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
//                biomes.getOrThrow(BiomeTags.IS_NETHER),
//                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NETHER_SAPPHIRE_ORE_PLACED_KEY)),
//                GenerationStep.Decoration.UNDERGROUND_ORES));
//
//        context.register(ADD_END_SAPPHIRE_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
//                biomes.getOrThrow(BiomeTags.IS_END),
//                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.END_SAPPHIRE_ORE_PLACED_KEY)),
//                GenerationStep.Decoration.UNDERGROUND_ORES));
//    }
//
//    private static ResourceKey<BiomeModifier> registerKey(String name) {
//        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(TutorialMod.MOD_ID, name));
//    }
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Hexerei.MOD_ID);


    public static DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<ModVegetalBiomeModifiers>> VEGETAL_MODIFIER = BIOME_MODIFIERS.register("vegetal", () ->
            RecordCodecBuilder.mapCodec(builder -> builder.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(ModVegetalBiomeModifiers::biomes),
                    PlacedFeature.CODEC.fieldOf("feature").forGetter(ModVegetalBiomeModifiers::feature)
            ).apply(builder, ModVegetalBiomeModifiers::new)));

//    public static DeferredHolder<Codec<ModOreBiomeModifier>> ORE_MODIFIER = BIOME_MODIFIERS.register("ores", () ->
//            RecordCodecBuilder.create(builder -> builder.group(
//                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(ModOreBiomeModifier::biomes),
//                    PlacedFeature.CODEC.fieldOf("feature").forGetter(ModOreBiomeModifier::feature)
//            ).apply(builder, ModOreBiomeModifier::new)));

    public static DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<ModEntityBiomeModifier>> ENTITY_MODIFIER = BIOME_MODIFIERS.register("entities", () ->
            RecordCodecBuilder.mapCodec(builder -> builder.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(ModEntityBiomeModifier::biomes),
                    MobSpawnSettings.SpawnerData.CODEC.fieldOf("entity").forGetter(ModEntityBiomeModifier::spawnerData)
            ).apply(builder, ModEntityBiomeModifier::new)));


    public static void register(IEventBus eventBus) {
        BIOME_MODIFIERS.register(eventBus);
    }
}
