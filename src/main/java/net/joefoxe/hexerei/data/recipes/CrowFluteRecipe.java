package net.joefoxe.hexerei.data.recipes;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.joefoxe.hexerei.item.custom.CrowFluteItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import javax.annotation.Nonnull;


public class CrowFluteRecipe extends ShapedRecipe {

    NonNullList<Ingredient> inputs;
    ItemStack output;

    public CrowFluteRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification) {
        super(group, category, pattern, result, showNotification);


        this.inputs = pattern.ingredients();
        this.output = result;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
    @Nonnull
    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        int first = -1;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getItem(i);
            Item item = stack.getItem();

            int colorId;
            if (item instanceof DyeItem dye) {
                colorId = dye.getDyeColor().getId();
            } else {
                continue;
            }
            if (first == -1) {
                first = colorId;
            } else {
                return CrowFluteItem.withColors(first, colorId);
            }
        }
        return CrowFluteItem.withColors(first != -1 ? first : 0, 0);
    }





    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return getOutput();
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public NonNullList<Ingredient> getInputs() {
        return inputs;
    }





    public static class Serializer implements RecipeSerializer<CrowFluteRecipe> {

        public static final MapCodec<CrowFluteRecipe> CODEC = RecordCodecBuilder.mapCodec(
                p_340778_ -> p_340778_.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
                                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification)
                        )
                        .apply(p_340778_, CrowFluteRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, CrowFluteRecipe> STREAM_CODEC = StreamCodec.of(
                CrowFluteRecipe.Serializer::toNetwork, CrowFluteRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<CrowFluteRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrowFluteRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static CrowFluteRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String s = buffer.readUtf();
            CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            boolean flag = buffer.readBoolean();
            return new CrowFluteRecipe(s, craftingbookcategory, shapedrecipepattern, itemstack, flag);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, CrowFluteRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeBoolean(recipe.showNotification());
        }

    }




    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CROW_FLUTE_DYE_SERIALIZER.get();
    }
}