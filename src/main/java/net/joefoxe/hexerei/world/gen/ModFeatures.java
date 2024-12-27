package net.joefoxe.hexerei.world.gen;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.world.structure.structures.HexereiAbstractTreeFeature;
import net.joefoxe.hexerei.world.structure.structures.HexereiMahoganyTreeFeature;
import net.joefoxe.hexerei.world.structure.structures.HexereiWitchHazelTreeFeature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES
            = DeferredRegister.create(BuiltInRegistries.FEATURE, Hexerei.MOD_ID);


    public static final DeferredHolder<Feature<?>, Feature<TreeConfiguration>> WILLOW_TREE = FEATURES.register("willow_tree",
            () -> new HexereiAbstractTreeFeature(TreeConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, Feature<TreeConfiguration>> WITCH_HAZEL_TREE = FEATURES.register("witch_hazel_tree",
            () -> new HexereiWitchHazelTreeFeature(TreeConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, Feature<TreeConfiguration>> MAHOGANY_TREE = FEATURES.register("mahogany_tree",
            () -> new HexereiMahoganyTreeFeature(TreeConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }

}
