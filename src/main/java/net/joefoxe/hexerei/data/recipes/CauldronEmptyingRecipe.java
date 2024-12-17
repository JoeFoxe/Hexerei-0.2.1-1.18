package net.joefoxe.hexerei.data.recipes;

import com.google.gson.JsonObject;
import net.joefoxe.hexerei.fluid.FluidIngredient;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import static net.joefoxe.hexerei.data.recipes.MixingCauldronRecipe.Serializer.deserializeFluidStack;

public class CauldronEmptyingRecipe implements Recipe<CauldronEmptyingRecipe.Wrapper> {

    private final ResourceLocation id;
    private final Ingredient input;
    private final FluidIngredient fluid;
    private final ItemStack output;

    public CauldronEmptyingRecipe(ResourceLocation id, Ingredient input, FluidIngredient fluid, ItemStack output) {
        this.id = id;
        this.input = input;
        this.fluid = fluid;
        this.output = output;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(Wrapper pContainer, Level pLevel) {
        return input.test(pContainer.getInput()) && fluid.test(pContainer.getFluid());
    }

    @Override
    public ItemStack assemble(Wrapper pContainer, RegistryAccess pRegistryAccess) {
        return getResultItem(pRegistryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    public Ingredient getInput() {
        return input;
    }

    public FluidIngredient getFluid() {
        return fluid;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CAULDRON_EMPTYING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CAULDRON_EMPTYING_TYPE.get();
    }

    public static class Wrapper extends RecipeWrapper {

        private final FluidStack fluid;
        public Wrapper(ItemStack stack, FluidStack fluid) {
            super(new ItemStackHandler(1));
            this.fluid = fluid;
            setItem(0, stack);
        }

        public ItemStack getInput() {
            return this.getItem(0);
        }
        public FluidStack getFluid() {
            return fluid;
        }
    }

    public static class Serializer implements RecipeSerializer<CauldronEmptyingRecipe> {

        @Override
        public CauldronEmptyingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient input = Ingredient.fromJson(pSerializedRecipe.get("input"));
            FluidStack fluid = deserializeFluidStack(GsonHelper.getAsJsonObject(pSerializedRecipe, "fluid"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            return new CauldronEmptyingRecipe(pRecipeId, input, FluidIngredient.fromFluidStack(fluid), output);
        }

        @Override
        public @Nullable CauldronEmptyingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            FluidIngredient fluid = FluidIngredient.read(buf);
            ItemStack output = buf.readItem();
            return new CauldronEmptyingRecipe(pRecipeId, input, fluid, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CauldronEmptyingRecipe recipe) {
            recipe.input.toNetwork(buf);
            recipe.fluid.write(buf);
            buf.writeItem(recipe.output);
        }
    }
}
