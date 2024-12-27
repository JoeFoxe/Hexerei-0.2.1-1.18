package net.joefoxe.hexerei.world.structure;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.world.structure.structures.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModStructures {
    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registries.STRUCTURE_TYPE, Hexerei.MOD_ID);

    /**
     * Registers the base structure itself and sets what its path is. In this case,
     * this base structure will have the resourcelocation of structure_tutorial:sky_structures.
     */
    public static final DeferredHolder<StructureType<?>, StructureType<DarkCovenStructure>> DARK_COVEN = DEFERRED_REGISTRY_STRUCTURE.register("dark_coven", () -> () -> DarkCovenStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<NatureCovenStructure>> NATURE_COVEN = DEFERRED_REGISTRY_STRUCTURE.register("nature_coven", () -> () -> NatureCovenStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<WitchHutStructure>> WITCH_HUT = DEFERRED_REGISTRY_STRUCTURE.register("witch_hut", () -> () -> WitchHutStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<BabaYagaHutStructure>> BABA_YAGA_HUT = DEFERRED_REGISTRY_STRUCTURE.register("baba_yaga_hut", () -> () -> BabaYagaHutStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<OwlPostOfficeStructure>> OWL_POST_OFFICE = DEFERRED_REGISTRY_STRUCTURE.register("owl_post_office", () -> () -> OwlPostOfficeStructure.CODEC);
}