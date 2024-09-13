package net.joefoxe.hexerei.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import static net.joefoxe.hexerei.data.recipes.MixingCauldronRecipe.Serializer.deserializeFluidStack;

public class CauldronFillingRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack output;
    private final FluidStack fluid;

    public CauldronFillingRecipe(ResourceLocation id, Ingredient input, ItemStack output, FluidStack fluid) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.fluid = fluid;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        return input.test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return getResultItem(pRegistryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    public FluidStack getResultingFluid() {
        return fluid.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
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

        @Override
        public CauldronFillingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient input = Ingredient.fromJson(pSerializedRecipe.get("input"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            FluidStack fluid = deserializeFluidStack(GsonHelper.getAsJsonObject(pSerializedRecipe, "fluid"));

            return new CauldronFillingRecipe(pRecipeId, input, result, fluid);
        }

        @Override
        public @Nullable CauldronFillingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            FluidStack fluid = FluidStack.readFromPacket(buf);
            return new CauldronFillingRecipe(pRecipeId, input, result, fluid);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CauldronFillingRecipe recipe) {
            recipe.input.toNetwork(buf);
            buf.writeItem(recipe.output);
            recipe.fluid.writeToPacket(buf);
        }
    }
}
