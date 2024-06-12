package net.joefoxe.hexerei.data.recipes;

import com.google.gson.JsonObject;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nonnull;
import java.util.Map;


public class BookOfShadowsRecipe extends ShapedRecipe {

    public BookOfShadowsRecipe(ShapedRecipe compose) {
        super(compose.getId(), compose.getGroup(), CraftingBookCategory.MISC, compose.getWidth(), compose.getHeight(), compose.getIngredients(), compose.getResultItem(null));
    }
    @Override
    public boolean isSpecial() {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        DyeColor color1 = null;
        DyeColor color2 = null;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            Item item = stack.getItem();

            if (item instanceof DyeItem dye) {
                if (color1 == null)
                    color1 = dye.getDyeColor();
                else
                    color2 = dye.getDyeColor();
            }
        }
        return HexereiBookItem.withColors(color1 == null ? 0 : color1.getId(), color2 == null ? 0 : color2.getId());
    }

    public ItemStack getOutput() {
        return getResultItem(null);
    }

    public NonNullList<Ingredient> getInputs() {
        return getIngredients();
    }

    public static class Serializer implements RecipeSerializer<BookOfShadowsRecipe> {
        @Nonnull
        @Override
        public BookOfShadowsRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            return new BookOfShadowsRecipe(SHAPED_RECIPE.fromJson(recipeId, json));
        }

        @Nonnull
        @Override
        public BookOfShadowsRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
            return new BookOfShadowsRecipe(SHAPED_RECIPE.fromNetwork(recipeId, buffer));
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull BookOfShadowsRecipe recipe) {
            SHAPED_RECIPE.toNetwork(buffer, recipe);
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BOOK_OF_SHADOWS_SERIALIZER.get();
    }
}