package net.joefoxe.hexerei.block.custom.trees;


import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

public class HexereiTree {

    public static TreeGrower getGrower(String name, ResourceKey<ConfiguredFeature<?, ?>> configConfiguredFeature) {
        return new TreeGrower(name, Optional.empty(), Optional.of(configConfiguredFeature), Optional.empty());
    }

}
