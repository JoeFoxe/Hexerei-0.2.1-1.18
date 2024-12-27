package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
    public static DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Hexerei.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Hexerei.MOD_ID);


//    public static final DeferredHolder<RecipeType<?>, MixingCauldronRecipe.Serializer> MIXING_SERIALIZER = RECIPE_SERIALIZERS.register("mixingcauldron", MixingCauldronRecipe.Serializer::new);
//    public static RecipeType<MixingCauldronRecipe> MIXING_CAULDRON_RECIPE = new MixingCauldronRecipe.MixingCauldronRecipeType();


//    public static final DeferredHolder<RecipeType<?>, DipperRecipe.Serializer> DIPPER_SERIALIZER = RECIPE_SERIALIZERS.register("dipper", DipperRecipe.Serializer::new);
//    public static RecipeType<DipperRecipe> DIPPER_RECIPE = new DipperRecipe.DipperRecipeType();


//    public static final DeferredHolder<RecipeType<?>, DryingRackRecipe.Serializer> DRYING_RACK_SERIALIZER = RECIPE_SERIALIZERS.register("drying_rack", DryingRackRecipe.Serializer::new);
//    public static RecipeType<DryingRackRecipe> DRYING_RACK_RECIPE = new DryingRackRecipe.DryingRackRecipeType();


//    public static final DeferredHolder<RecipeType<?>, PestleAndMortarRecipe.Serializer> PESTLE_AND_MORTAR_SERIALIZER = RECIPE_SERIALIZERS.register("pestle_and_mortar", PestleAndMortarRecipe.Serializer::new);
//    public static RecipeType<PestleAndMortarRecipe> PESTLE_AND_MORTAR_RECIPE = new PestleAndMortarRecipe.PestleAndMortarRecipeType();


    public static final DeferredHolder<RecipeType<?>, RecipeType<MixingCauldronRecipe>> MIXING_CAULDRON_TYPE = RECIPE_TYPES.register("mixingcauldron", () -> MixingCauldronRecipe.Type.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MixingCauldronRecipe>> MIXING_SERIALIZER =
            RECIPE_SERIALIZERS.register("mixingcauldron", () -> MixingCauldronRecipe.Serializer.INSTANCE);


    public static final DeferredHolder<RecipeType<?>, RecipeType<FluidMixingRecipe>> FLUID_MIXING_TYPE = RECIPE_TYPES.register("fluid_mixing", () -> FluidMixingRecipe.Type.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FluidMixingRecipe>> FLUID_MIXING_SERIALIZER =
            RECIPE_SERIALIZERS.register("fluid_mixing", () -> FluidMixingRecipe.Serializer.INSTANCE);


    public static final DeferredHolder<RecipeType<?>, RecipeType<DipperRecipe>> DIPPER_TYPE = RECIPE_TYPES.register("dipper", () -> DipperRecipe.Type.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DipperRecipe>> DIPPER_SERIALIZER =
            RECIPE_SERIALIZERS.register("dipper", () -> DipperRecipe.Serializer.INSTANCE);



    public static final DeferredHolder<RecipeType<?>, RecipeType<DryingRackRecipe>> DRYING_RACK_TYPE = RECIPE_TYPES.register("drying_rack", () -> DryingRackRecipe.Type.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DryingRackRecipe>> DRYING_RACK_SERIALIZER =
            RECIPE_SERIALIZERS.register("drying_rack", () -> DryingRackRecipe.Serializer.INSTANCE);



    public static final DeferredHolder<RecipeType<?>, RecipeType<PestleAndMortarRecipe>> PESTLE_AND_MORTAR_TYPE = RECIPE_TYPES.register("pestle_and_mortar", () -> PestleAndMortarRecipe.Type.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PestleAndMortarRecipe>> PESTLE_AND_MORTAR_SERIALIZER =
            RECIPE_SERIALIZERS.register("pestle_and_mortar", () -> PestleAndMortarRecipe.Serializer.INSTANCE);


    public static final DeferredHolder<RecipeType<?>, RecipeType<AddToCandleRecipe>> ADD_TO_CANDLE_TYPE = RECIPE_TYPES.register("add_to_candle", () -> AddToCandleRecipe.Type.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AddToCandleRecipe>> ADD_TO_CANDLE_SERIALIZER =
            RECIPE_SERIALIZERS.register("add_to_candle", () -> AddToCandleRecipe.Serializer.INSTANCE);


    public static final DeferredHolder<RecipeType<?>, RecipeType<CutCandleRecipe>> CUT_CANDLE_TYPE = RECIPE_TYPES.register("cut_candle", ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CutCandleRecipe>> CUT_CANDLE_SERIALIZER =
            RECIPE_SERIALIZERS.register("cut_candle", () -> new SimpleCraftingRecipeSerializer<>(CutCandleRecipe::new));





    public static final DeferredHolder<RecipeType<?>, RecipeType<FillWaxingKitRecipe>> FILL_WAXING_KIT_TYPE = RECIPE_TYPES.register("fill_waxing_kit", ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FillWaxingKitRecipe>> FILL_WAXING_KIT_SERIALIZER =
            RECIPE_SERIALIZERS.register("fill_waxing_kit", () -> new SimpleCraftingRecipeSerializer<>(FillWaxingKitRecipe::new));


    public static final DeferredHolder<RecipeType<?>, RecipeType<CrowFluteRecipe>> CROW_FLUTE_DYE_TYPE = RECIPE_TYPES.register("crow_flute_dye", ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrowFluteRecipe>> CROW_FLUTE_DYE_SERIALIZER = RECIPE_SERIALIZERS.register("crow_flute_dye", CrowFluteRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BookOfShadowsRecipe>> BOOK_OF_SHADOWS_TYPE = RECIPE_TYPES.register("book_of_shadows", ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BookOfShadowsRecipe>> BOOK_OF_SHADOWS_SERIALIZER = RECIPE_SERIALIZERS.register("book_of_shadows", BookOfShadowsRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BookOfShadowsDyeRecipe>> BOOK_OF_SHADOWS_DYE_TYPE = RECIPE_TYPES.register("book_of_shadows_dye", ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<?>> BOOK_OF_SHADOWS_DYE_SERIALIZER = RECIPE_SERIALIZERS.register("book_of_shadows_dye", () -> new SimpleCraftingRecipeSerializer<>(BookOfShadowsDyeRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<KeychainRecipe>> KEYCHAIN_APPLY_TYPE = RECIPE_TYPES.register("keychain_apply", ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<KeychainRecipe>> KEYCHAIN_APPLY_SERIALIZER = RECIPE_SERIALIZERS.register("keychain_apply", () -> new SimpleCraftingRecipeSerializer<>(KeychainRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<KeychainUndoRecipe>> KEYCHAIN_UNDO_TYPE = RECIPE_TYPES.register("keychain_undo", ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<KeychainUndoRecipe>> KEYCHAIN_UNDO_SERIALIZER = RECIPE_SERIALIZERS.register("keychain_undo", () -> new SimpleCraftingRecipeSerializer<>(KeychainUndoRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<WhistleBindRecipe>> WHISTLE_BIND_TYPE = RECIPE_TYPES.register("whistle_bind", ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WhistleBindRecipe>> WHISTLE_BIND_SERIALIZER = RECIPE_SERIALIZERS.register("whistle_bind", () -> new SimpleCraftingRecipeSerializer<>(WhistleBindRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<CrowAmuletRecipe>> CROW_AMULET_APPLY_TYPE = RECIPE_TYPES.register("crow_amulet_apply", ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrowAmuletRecipe>> CROW_AMULET_APPLY_SERIALIZER = RECIPE_SERIALIZERS.register("crow_amulet_apply", () -> new SimpleCraftingRecipeSerializer<>(CrowAmuletRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<CrowAmuletUndoRecipe>> CROW_AMULET_UNDO_TYPE = RECIPE_TYPES.register("crow_amulet_undo", ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrowAmuletUndoRecipe>> CROW_AMULET_UNDO_SERIALIZER = RECIPE_SERIALIZERS.register("crow_amulet_undo", () -> new SimpleCraftingRecipeSerializer<>(CrowAmuletUndoRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<WoodcutterRecipe>> WOODCUTTING_TYPE = RECIPE_TYPES.register("woodcutting", ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WoodcutterRecipe>> WOODCUTTING_SERIALIZER = RECIPE_SERIALIZERS.register("woodcutting", () -> WoodcutterRecipe.Serializer.INSTANCE);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CauldronFillingRecipe>> CAULDRON_FILLING_TYPE = RECIPE_TYPES.register("cauldron_filling", ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CauldronFillingRecipe>> CAULDRON_FILLING_SERIALIZER = RECIPE_SERIALIZERS.register("cauldron_filling", CauldronFillingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CauldronEmptyingRecipe>> CAULDRON_EMPTYING_TYPE = RECIPE_TYPES.register("cauldron_emptying", ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CauldronEmptyingRecipe>> CAULDRON_EMPTYING_SERIALIZER = RECIPE_SERIALIZERS.register("cauldron_emptying", CauldronEmptyingRecipe.Serializer::new);



    public static final DeferredHolder<RecipeType<?>, RecipeType<AddBaseToCandleRecipe>> ADD_BASE_TO_CANDLE_TYPE = RECIPE_TYPES.register("add_base_to_candle", () -> AddBaseToCandleRecipe.Type.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AddBaseToCandleRecipe>> ADD_BASE_TO_CANDLE_SERIALIZER = RECIPE_SERIALIZERS.register("add_base_to_candle", () -> new SimpleCraftingRecipeSerializer<>(AddBaseToCandleRecipe::new));
    private static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        @Override
        public String toString() {
            return BuiltInRegistries.RECIPE_TYPE.getKey(this).toString();
        }
    }
    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);

    }



}
