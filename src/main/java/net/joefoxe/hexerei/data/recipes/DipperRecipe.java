package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.ArrayList;
import java.util.List;

public class DipperRecipe implements Recipe<CraftingInput> {

    private final Ingredient input;
    private final ItemStack output;
    private final FluidStack liquid;
    private final int fluidLevelsConsumed;
    private final int dippingTime;
    private final int dryingTime;
    private final int numberOfDips;
    private final boolean useInputItemAsOutput;


    @Override
    public boolean isSpecial() {
        return true;
    }
    public DipperRecipe(Ingredient input, ItemStack output, FluidStack liquid, int fluidLevelsConsumed, int dippingTime, int dryingTime, int numberOfDips, boolean useInputItemAsOutput) {
        this.input = input;
        this.output = output;
        this.liquid = liquid;
        this.fluidLevelsConsumed = fluidLevelsConsumed;
        this.dippingTime = dippingTime;
        this.dryingTime = dryingTime;
        this.numberOfDips = numberOfDips;
        this.useInputItemAsOutput = useInputItemAsOutput;

    }


    public List<FluidIngredient> getFluidIngredients(){
        return new ArrayList<>(List.of(FluidIngredient.of(this.liquid)));
    }
    public FluidIngredient getFluidIngredient(){
        return FluidIngredient.of(this.liquid);
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        return input.test(inv.getItem(0)) ||
                input.test(inv.getItem(1)) ||
                input.test(inv.getItem(2));


    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(input);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return getOutput();
    }

    public ItemStack getOutput(){
        return output.copy();
    }

    public FluidStack getLiquid() { return this.liquid; }

    public int getFluidLevelsConsumed() { return this.fluidLevelsConsumed; }

    public int getDippingTime() { return this.dippingTime; }

    public int getDryingTime() { return this.dryingTime; }

    public int getNumberOfDips() { return this.numberOfDips; }

    public boolean getUseInputItemAsOutput() { return this.useInputItemAsOutput; }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.CANDLE_DIPPER.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.DIPPER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<DipperRecipe> {
        private Type() { }
        public static final DipperRecipe.Type INSTANCE = new DipperRecipe.Type();
        public static final String ID = "dipper";
    }

    // for Serializing the recipe into/from a json
    public static class Serializer implements RecipeSerializer<DipperRecipe> {
        public static final DipperRecipe.Serializer INSTANCE = new DipperRecipe.Serializer();
        public static final ResourceLocation ID =
                ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID,"dipper");

        private static final MapCodec<DipperRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
                                ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
                                FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.liquid),
                                Codec.INT.fieldOf("fluidLevelsConsumed").forGetter(recipe -> recipe.fluidLevelsConsumed),
                                Codec.INT.fieldOf("dippingTime").forGetter(recipe -> recipe.dippingTime),
                                Codec.INT.fieldOf("dryingTime").forGetter(recipe -> recipe.dryingTime),
                                Codec.INT.fieldOf("numberOfDips").forGetter(recipe -> recipe.numberOfDips),
                                Codec.BOOL.fieldOf("useInputItemAsOutput").forGetter(recipe -> recipe.useInputItemAsOutput)
                        )
                        .apply(instance, DipperRecipe::new)
        );
//        public DipperRecipe(NonNullList<Ingredient> inputs, ItemStack output, FluidStack liquid, int fluidLevelsConsumed, int dippingTime, int dryingTime, int numberOfDips, boolean useInputItemAsOutput) {
        public static final StreamCodec<RegistryFriendlyByteBuf, DipperRecipe> STREAM_CODEC = StreamCodec.of(
                DipperRecipe.Serializer::toNetwork, DipperRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<DipperRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DipperRecipe> streamCodec() {
            return STREAM_CODEC;
        }


        static <B extends ByteBuf> StreamCodec.CodecOperation<B, Ingredient, NonNullList<Ingredient>> list() {
            return p_320272_ -> ByteBufCodecs.collection((s) -> NonNullList.withSize(s, Ingredient.EMPTY), p_320272_);
        }

        private static DipperRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
            FluidStack fluid = FluidStack.STREAM_CODEC.decode(buffer);
            int fluidLevelsConsumed = ByteBufCodecs.INT.decode(buffer);
            int dippingTime = ByteBufCodecs.INT.decode(buffer);
            int dryingTime = ByteBufCodecs.INT.decode(buffer);
            int numberOfDips = ByteBufCodecs.INT.decode(buffer);
            boolean useInputItemAsOutput = ByteBufCodecs.BOOL.decode(buffer);
            return new DipperRecipe(input, output, fluid, fluidLevelsConsumed, dippingTime, dryingTime, numberOfDips, useInputItemAsOutput);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, DipperRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.liquid);
            ByteBufCodecs.INT.encode(buffer, recipe.fluidLevelsConsumed);
            ByteBufCodecs.INT.encode(buffer, recipe.dippingTime);
            ByteBufCodecs.INT.encode(buffer, recipe.dryingTime);
            ByteBufCodecs.INT.encode(buffer, recipe.numberOfDips);
            ByteBufCodecs.BOOL.encode(buffer, recipe.useInputItemAsOutput);
        }
    }
}
