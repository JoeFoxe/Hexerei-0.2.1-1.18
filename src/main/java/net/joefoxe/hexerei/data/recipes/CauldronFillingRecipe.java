package net.joefoxe.hexerei.data.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;


public class CauldronFillingRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient input;
    private final ItemStack output;
    private final FluidStack fluid;

    public CauldronFillingRecipe(Ingredient input, ItemStack output, FluidStack fluid) {
        this.input = input;
        this.output = output;
        this.fluid = fluid;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level pLevel) {
        return test(input, recipeInput.getItem(0));
    }

    public boolean test(Ingredient ingredient, @javax.annotation.Nullable ItemStack pStack) {
        if (pStack == null) {
            return false;
        } else if (ingredient.isEmpty()) {
            return pStack.isEmpty();
        } else {
            for(ItemStack itemStack : ingredient.getItems()) {
                if (itemStack.is(pStack.getItem())) {
                    PotionContents itemStackPotion = itemStack.get(DataComponents.POTION_CONTENTS);
                    PotionContents pStackPotion = pStack.get(DataComponents.POTION_CONTENTS);
                    return itemStackPotion == null || pStackPotion == null || (pStackPotion.potion().isPresent() && itemStackPotion.is(pStackPotion.potion().get()));
                }
            }

            return false;
        }
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return getResultItem(registries);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }

    public FluidStack getResultingFluid() {
        return fluid.copy();
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CAULDRON_FILLING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CAULDRON_FILLING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CauldronFillingRecipe> {

        private static final MapCodec<CauldronFillingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
                                ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
                                FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluid)
                        )
                        .apply(instance, CauldronFillingRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, CauldronFillingRecipe> STREAM_CODEC = StreamCodec.of(
                CauldronFillingRecipe.Serializer::toNetwork, CauldronFillingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<CauldronFillingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CauldronFillingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static CauldronFillingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
            FluidStack fluid = FluidStack.STREAM_CODEC.decode(buffer);
            return new CauldronFillingRecipe(input, output, fluid);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, CauldronFillingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.fluid);
        }

    }
}
