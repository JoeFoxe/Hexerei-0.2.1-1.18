package net.joefoxe.hexerei.data.recipes;

import com.google.gson.JsonObject;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;


public class AddToCandleRecipe extends CustomRecipe {

    NonNullList<Ingredient> inputs;
    ItemStack output;

    public AddToCandleRecipe(ResourceLocation pId, NonNullList<Ingredient> inputs, ItemStack output) {
        super(pId, CraftingBookCategory.MISC);

        this.inputs = inputs;
        this.output = output;
    }
    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        int i = 0;
        ItemStack itemstack = ItemStack.EMPTY;

        for(int j = 0; j < pInv.getContainerSize(); ++j) {
            ItemStack itemstack1 = pInv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ModItems.CANDLE.get())) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {

                    if(inputs.isEmpty() || inputs.get(1).getItems().length == 0)
                        return false;

                    CompoundTag tag = new CompoundTag();
                    CompoundTag tag2 = new CompoundTag();
                    if(itemstack1.hasTag())
                        tag = itemstack1.getOrCreateTag();
                    if(inputs.get(1).getItems()[0].hasTag())
                        tag2 = inputs.get(1).getItems()[0].getOrCreateTag();
                    boolean compare = NbtUtils.compareNbt(tag2, tag, true);

                    if ((itemstack1.is(this.inputs.get(1).getItems()[0].getItem()) && compare)) {
                        ++i;
                    }

                }
            }
        }

        return !itemstack.isEmpty() && i == 1;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingContainer pInv, RegistryAccess registryAccess) {
        int i = 0;
        ItemStack candle = ItemStack.EMPTY;

        for(int j = 0; j < pInv.getContainerSize(); ++j) {
            ItemStack itemstack1 = pInv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ModItems.CANDLE.get())) {
                    if (!candle.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    candle = itemstack1;
                } else {


                    CompoundTag tag = new CompoundTag();
                    CompoundTag tag2 = new CompoundTag();
                    if(itemstack1.hasTag())
                        tag = itemstack1.getOrCreateTag();
                    if(inputs.get(1).getItems()[0].hasTag())
                        tag2 = inputs.get(1).getItems()[0].getOrCreateTag();
                    boolean compare = NbtUtils.compareNbt(tag2, tag, true);

                    if (!itemstack1.is(this.inputs.get(1).getItems()[0].getItem()) && compare) {
                        return ItemStack.EMPTY;
                    }
                    ++i;
                }
            }
        }

        if (!candle.isEmpty() && i >= 1) {
            ItemStack itemstack2 = candle.copy();
            itemstack2.setCount(1);

            CandleData data = new CandleData();
            data.load(itemstack2.getOrCreateTag());
            data.load(output.getOrCreateTag());
            data.save(itemstack2.getOrCreateTag(), true);

            return itemstack2;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return getOutput();
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public NonNullList<Ingredient> getInputs() {
        return inputs;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputs;
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.ADD_TO_CANDLE_SERIALIZER.get();
    }
//
    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public static class Type implements RecipeType<AddToCandleRecipe> {
        private Type() { }
        public static final AddToCandleRecipe.Type INSTANCE = new AddToCandleRecipe.Type();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }


    // for Serializing the recipe into/from a json
    public static class Serializer implements RecipeSerializer<AddToCandleRecipe> {
        public static final AddToCandleRecipe.Serializer INSTANCE = new AddToCandleRecipe.Serializer();

        @Override
        public AddToCandleRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(2, Ingredient.EMPTY);
            ItemStack input = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "input"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            inputs.set(0, Ingredient.of(new ItemStack(ModItems.CANDLE.get())));
            inputs.set(1, Ingredient.of(input));

            return new AddToCandleRecipe(recipeId, inputs,
                    output);
        }

        @Nullable
        @Override
        public AddToCandleRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }

            ItemStack output = buffer.readItem();
            return new AddToCandleRecipe(recipeId, inputs, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AddToCandleRecipe recipe) {
            buffer.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients())
                ing.toNetwork(buffer);
            buffer.writeItem(recipe.getOutput());
        }

    }
}