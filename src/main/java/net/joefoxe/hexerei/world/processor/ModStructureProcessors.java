package net.joefoxe.hexerei.world.processor;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModStructureProcessors {
	public static final DeferredRegister<StructureProcessorType<?>> DEFERRED_REGISTRY_STRUCTURE_PROCESSOR = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, Hexerei.MOD_ID);

	public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<WitchHutLegProcessor>> WITCH_HUT_LEG_PROCESSOR = DEFERRED_REGISTRY_STRUCTURE_PROCESSOR.register("witch_hut_leg_processor", () -> (StructureProcessorType) () -> WitchHutLegProcessor.CODEC);
	public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<DarkCovenLegProcessor>> DARK_COVEN_LEG_PROCESSOR = DEFERRED_REGISTRY_STRUCTURE_PROCESSOR.register("dark_coven_leg_processor", () -> (StructureProcessorType) () -> DarkCovenLegProcessor.CODEC);
	public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<NatureCovenLegProcessor>> NATURE_COVEN_LEG_PROCESSOR = DEFERRED_REGISTRY_STRUCTURE_PROCESSOR.register("nature_coven_leg_processor", () -> (StructureProcessorType) () -> NatureCovenLegProcessor.CODEC);
	public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<MangroveTreeLegProcessor>> MANGROVE_TREE_LEG_PROCESSOR = DEFERRED_REGISTRY_STRUCTURE_PROCESSOR.register("mangrove_tree_leg_processor", () -> (StructureProcessorType) () -> MangroveTreeLegProcessor.CODEC);
}
