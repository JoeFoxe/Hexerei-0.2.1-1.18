package net.joefoxe.hexerei.particle;

import com.mojang.serialization.Codec;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Hexerei.MOD_ID);
//    public static final RegistryObject<ParticleType<CauldronParticleData>> CAULDRON = register(REGISTRY, "divine_altar_consume", CauldronParticleData.DESERIALIZER, CauldronParticleData.CODEC, true);


    public static final RegistryObject<ParticleType<CauldronParticleData>> CAULDRON = PARTICLES.register("cauldron_particle", () -> new ParticleType<>(true, CauldronParticleData.DESERIALIZER) {
        @Nonnull
        @Override
        public Codec<CauldronParticleData> codec() {

            return CauldronParticleData.codec(this);
        }
    });
    public static final RegistryObject<SimpleParticleType> BLOOD = PARTICLES.register("blood_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLOOD_BIT = PARTICLES.register("blood_bit_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BROOM = PARTICLES.register("broom_particle_1", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BROOM_2 = PARTICLES.register("broom_particle_2", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BROOM_3 = PARTICLES.register("broom_particle_3", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BROOM_4 = PARTICLES.register("broom_particle_4", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BROOM_5 = PARTICLES.register("broom_particle_5", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BROOM_6 = PARTICLES.register("broom_particle_6", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> FOG = PARTICLES.register("fog_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> EXTINGUISH = PARTICLES.register("extinguish_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MOON_BRUSH_1 = PARTICLES.register("moon_brush_1", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MOON_BRUSH_2 = PARTICLES.register("moon_brush_2", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MOON_BRUSH_3 = PARTICLES.register("moon_brush_3", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MOON_BRUSH_4 = PARTICLES.register("moon_brush_4", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> STAR_BRUSH = PARTICLES.register("star_brush", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> OWL_TELEPORT = PARTICLES.register("owl_teleport", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> OWL_TELEPORT_BARN = PARTICLES.register("owl_teleport_barn", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> OWL_TELEPORT_BARRED = PARTICLES.register("owl_teleport_barred", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> OWL_TELEPORT_SNOWY = PARTICLES.register("owl_teleport_snowy", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MAHOGANY_LEAVES = PARTICLES.register("mahogany_leaves", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> WITCH_HAZEL_LEAVES = PARTICLES.register("witch_hazel_leaves", () -> new SimpleParticleType(true));

//    public static final RegistryObject<SimpleParticleType> DOWSING_ROD_1 = PARTICLES.register("dowsing_rod_1", () -> new SimpleParticleType(true));
//    public static final RegistryObject<SimpleParticleType> DOWSING_ROD_2 = PARTICLES.register("dowsing_rod_2", () -> new SimpleParticleType(true));

}