package net.joefoxe.hexerei.world.biomemods;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public record ModEntityBiomeModifier(HolderSet<Biome> biomes, MobSpawnSettings.SpawnerData spawnerData) implements BiomeModifier {
    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && biomes.contains(biome)) {
            builder.getMobSpawnSettings().addSpawn(spawnerData.type.getCategory(),
                    new MobSpawnSettings.SpawnerData(spawnerData.type, spawnerData.getWeight(),
                            spawnerData.minCount, spawnerData.maxCount));
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return ModBiomeModifiers.ENTITY_MODIFIER.get();
    }
}