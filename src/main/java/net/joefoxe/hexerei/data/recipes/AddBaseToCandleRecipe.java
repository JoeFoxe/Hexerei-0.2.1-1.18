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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;


public class AddBaseToCandleRecipe extends CustomRecipe {

    public AddBaseToCandleRecipe(ResourceLocation pId) {
        super(pId, CraftingBookCategory.MISC);

    }
    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        ItemStack itemstack = ItemStack.EMPTY;
        BlockItem block = null;

        for(int j = 0; j < pInv.getContainerSize(); ++j) {
            ItemStack itemstack1 = pInv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ModItems.CANDLE.get())) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else if (itemstack1.getItem() instanceof BlockItem blockItem) {
                    if (block != null) {
                        return false;
                    }

                    block = blockItem;
                }
            }
        }

        return !itemstack.isEmpty() && block != null;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingContainer pInv, RegistryAccess registryAccess) {
        int i = 0;
        ItemStack candle = ItemStack.EMPTY;
        BlockItem block = null;

        for(int j = 0; j < pInv.getContainerSize(); ++j) {
            ItemStack itemstack1 = pInv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ModItems.CANDLE.get())) {
                    if (!candle.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    candle = itemstack1;
                } else if (itemstack1.getItem() instanceof BlockItem blockItem) {
                    try {
                        if (block != null || !blockItem.getBlock().defaultBlockState().getShape(null, null).equals(Shapes.block())) {
                            return ItemStack.EMPTY;
                        }

                        block = blockItem;
                    } catch (Exception exception) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if (!candle.isEmpty() && block != null) {
            ItemStack itemstack2 = candle.copy();
            itemstack2.setCount(1);

            CandleData data = new CandleData();
            data.load(itemstack2.getOrCreateTag());
            ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(block.getBlock());

            if (loc != null) {
                CompoundTag tag = new CompoundTag();
                tag.putBoolean("layerFromBlockLocation", true);
                tag.putString("layer", loc.toString());
                data.base.load(tag);
            }
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
        return ModItems.CANDLE.get().getDefaultInstance();
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.ADD_BASE_TO_CANDLE_SERIALIZER.get();
    }
//
    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public static class Type implements RecipeType<AddBaseToCandleRecipe> {
        private Type() { }
        public static final AddBaseToCandleRecipe.Type INSTANCE = new AddBaseToCandleRecipe.Type();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }


    // for Serializing the recipe into/from a json
    public static class Serializer implements RecipeSerializer<AddBaseToCandleRecipe> {
        public static final AddBaseToCandleRecipe.Serializer INSTANCE = new AddBaseToCandleRecipe.Serializer();

        @Override
        public AddBaseToCandleRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return new AddBaseToCandleRecipe(recipeId);
        }

        @Nullable
        @Override
        public AddBaseToCandleRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new AddBaseToCandleRecipe(recipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AddBaseToCandleRecipe recipe) {
        }

    }
}