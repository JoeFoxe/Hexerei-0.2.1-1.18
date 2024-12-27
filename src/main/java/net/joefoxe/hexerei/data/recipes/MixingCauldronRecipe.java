package net.joefoxe.hexerei.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MixingCauldronRecipe implements Recipe<CraftingInput> {

    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final FluidStack liquid;
    private final FluidStack liquidOutput;
    private final int fluidLevelsConsumed;
    protected static final List<Boolean> itemMatchesSlot = new ArrayList<>();

    private final FluidMixingRecipe.HeatCondition heatCondition;
    private final MoonPhases.MoonCondition moonCondition;


    @Override
    public boolean isSpecial() {
        return true;
    }

    public MixingCauldronRecipe(ItemStack output, NonNullList<Ingredient> recipeItems, FluidStack liquid, FluidStack liquidOutput, int fluidLevelsConsumed, FluidMixingRecipe.HeatCondition heatCondition, MoonPhases.MoonCondition moonCondition) {
        this.output = output;
        this.recipeItems = recipeItems;
        this.liquid = liquid;
        this.liquidOutput = liquidOutput;
        this.fluidLevelsConsumed = fluidLevelsConsumed;
        this.heatCondition = heatCondition;
        this.moonCondition = moonCondition;

    }

    public List<FluidIngredient> getFluidIngredients(){
        return new ArrayList<>(List.of(FluidIngredient.of(this.liquid)));
    }
    public FluidIngredient getFluidIngredient(){
        return FluidIngredient.of(this.liquid);
    }


    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {

        List<Boolean> itemMatchesSlot = Stream.generate(() -> false).limit(8).collect(Collectors.toList());

        // the flag is to break out early in case nothing matches for that slot
        boolean flag = false;

        // cycle through each recipe slot
        for(Ingredient recipeItem : recipeItems) {
            //cycle through each slot for each recipe slot
            for (int i = 0; i < 8; i++) {
                //if the recipe matches a slot
                if (recipeItem.test(inv.getItem(i))) {
                    // if the slot is not taken up
                    if (!itemMatchesSlot.get(i)) {
                        //mark the slot as taken up
                        itemMatchesSlot.set(i, true);
                        flag = true;
                        break;
                    }
                }
            }
            //this is where it breaks out early to stop the craft
            if(!flag)
                break;
            //reset the flag for the next iteration
            flag = false;
        }
        // checks if a slot is not taken up, if its not taken up then itll not craft
        for(int i = 0; i < 8; i++) {
            if (!itemMatchesSlot.get(i))
                return false;
        }
        //if it reaches here that means it has completed the shapeless craft and should craft it
        return true;


//        SHAPED CRAFTING - maybe bring this back as another config in the recipe to see if its shaped or shapeless
//        if(recipeItems.get(0).test(inv.getItem(0)) &&
//            recipeItems.get(1).test(inv.getItem(1)) &&
//            recipeItems.get(2).test(inv.getItem(2)) &&
//            recipeItems.get(3).test(inv.getItem(3)) &&
//            recipeItems.get(4).test(inv.getItem(4)) &&
//            recipeItems.get(5).test(inv.getItem(5)) &&
//            recipeItems.get(6).test(inv.getItem(6)) &&
//            recipeItems.get(7).test(inv.getItem(7)))
//        {
//            return true;
//        }
//        return false;

    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }


    @Override
    public ItemStack assemble(CraftingInput p_44001_, HolderLookup.Provider registryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {

        return getOutput();
    }

    public ItemStack getOutput(){
        return output.copy();
    }

    public FluidMixingRecipe.HeatCondition getHeatCondition() { return this.heatCondition; }
    public MoonPhases.MoonCondition getMoonCondition() { return this.moonCondition; }
    public FluidStack getLiquid() { return this.liquid; }

    public FluidStack getLiquidOutput() { return this.liquidOutput; }

    public int getFluidLevelsConsumed() { return this.fluidLevelsConsumed; }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.MIXING_CAULDRON.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.MIXING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MixingCauldronRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<MixingCauldronRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<MixingCauldronRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<MixingCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
                                NonNullList.codecOf(Ingredient.CODEC).fieldOf("input").forGetter(recipe -> recipe.recipeItems),
                                FluidStack.CODEC.fieldOf("liquidOutput").forGetter(recipe -> recipe.liquidOutput),
                                FluidStack.CODEC.fieldOf("liquid").forGetter(recipe -> recipe.liquid),
                                Codec.INT.fieldOf("fluidLevelsConsumed").forGetter(recipe -> recipe.fluidLevelsConsumed),
                                FluidMixingRecipe.HeatCondition.CODEC.optionalFieldOf("heatRequirement", FluidMixingRecipe.HeatCondition.NONE).forGetter(recipe -> recipe.heatCondition),
                                MoonPhases.MoonCondition.CODEC.optionalFieldOf("moonRequirement", MoonPhases.MoonCondition.NONE).forGetter(recipe -> recipe.moonCondition)
                        )
                        .apply(instance, MixingCauldronRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MixingCauldronRecipe> STREAM_CODEC = StreamCodec.of(
                MixingCauldronRecipe.Serializer::toNetwork, MixingCauldronRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<MixingCauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MixingCauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static MixingCauldronRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
            NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);
            inputs.replaceAll(ignored -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            FluidStack inputFluid = FluidStack.STREAM_CODEC.decode(buffer);
            FluidStack outputFluid = FluidStack.STREAM_CODEC.decode(buffer);
            int fluidLevelsConsumed = ByteBufCodecs.INT.decode(buffer);
            FluidMixingRecipe.HeatCondition heatCondition = NeoForgeStreamCodecs.enumCodec(FluidMixingRecipe.HeatCondition.class).decode(buffer);
            MoonPhases.MoonCondition moonCondition = NeoForgeStreamCodecs.enumCodec(MoonPhases.MoonCondition.class).decode(buffer);

            return new MixingCauldronRecipe(output, inputs, inputFluid, outputFluid, fluidLevelsConsumed, heatCondition, moonCondition);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, MixingCauldronRecipe recipe) {
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            buffer.writeInt(recipe.recipeItems.size());
            for (Ingredient ingredient : recipe.recipeItems)
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.liquid);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.liquidOutput);
            ByteBufCodecs.INT.encode(buffer, recipe.fluidLevelsConsumed);
            NeoForgeStreamCodecs.enumCodec(FluidMixingRecipe.HeatCondition.class).encode(buffer, recipe.heatCondition);
            NeoForgeStreamCodecs.enumCodec(MoonPhases.MoonCondition.class).encode(buffer, recipe.moonCondition);
        }
    }

}
