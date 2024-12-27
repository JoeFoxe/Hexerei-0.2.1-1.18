package net.joefoxe.hexerei.data.datagen;

import com.google.gson.JsonObject;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipe;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class WoodcutterRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final Ingredient ingredient;
    private final int count;
    private final int ingredient_count;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    private final String type;

    public WoodcutterRecipeBuilder(ItemLike ingredient, ItemLike result, int count, int ingredient_count, String type) {
        this.ingredient = Ingredient.of(ingredient);
        this.result = result.asItem();
        this.count = count;
        this.ingredient_count = ingredient_count;
        this.type = type;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.advancement.addCriterion(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return result;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
//        this.advancement.parent(ResourceLocation.withDefaultNamespace("recipes/root"))
//                .addCriterion("give_recipe",
//                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(this.ingredient.getItems()[0].getItem()).build()))
//                .rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.AND);

        String path = "recipes/" + id.getPath();

        recipeOutput.accept(HexereiUtil.getResource("woodcutting" + "/" + this.type + "/" + BuiltInRegistries.ITEM.getKey(this.result).getPath() + "_from_" + BuiltInRegistries.ITEM.getKey(this.ingredient.getItems()[0].getItem()).getPath() + "_woodcutting"),
                new WoodcutterRecipe(this.type, this.ingredient, BuiltInRegistries.ITEM.getKey(this.result).toString(), this.count, this.ingredient_count), this.advancement.build(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path.trim())));
    }



//    public static class Result implements FinishedRecipe {
//        private final ResourceLocation id;
//        private final Item result;
//        private final Ingredient ingredient;
//        private final int count;
//        private final Advancement.Builder advancement;
//        private final ResourceLocation advancementId;
//
//        public final int ingredientCount;
//        public final String type;
//
//        public Result(ResourceLocation pId, Item pResult, int pCount, int ingredientCount, Ingredient ingredient, Advancement.Builder pAdvancement,
//                      ResourceLocation pAdvancementId, String type) {
//            this.ingredientCount = ingredientCount;
//            this.id = pId;
//            this.result = pResult;
//            this.count = pCount;
//            this.ingredient = ingredient;
//            this.advancement = pAdvancement;
//            this.advancementId = pAdvancementId;
//            this.type = type;
//        }
//
//        @Override
//        public void serializeRecipeData(JsonObject pJson) {
//            JsonObject jsonobject = new JsonObject();
//            jsonobject.addProperty("item", ForgeRegistries.ITEMS.getKey(ingredient.getItems()[0].getItem()).toString());
//
//            pJson.addProperty("result", ForgeRegistries.ITEMS.getKey(this.result).toString());
//            pJson.add("ingredient", jsonobject);
//            if (this.count > 1) {
//                pJson.addProperty("count", this.count);
//            }
//            if (this.ingredientCount > 1) {
//                pJson.addProperty("ingredient_count", this.ingredientCount);
//            }
//
//        }
//
//        @Override
//        public ResourceLocation getId() {
//            return HexereiUtil.getResource("woodcutting" + "/" + this.type + "/" + BuiltInRegistries.ITEM.getKey(this.result).getPath() + "_from_" + BuiltInRegistries.ITEM.getKey(this.ingredient.getItems()[0].getItem()).getPath() + "_woodcutting");
//        }
//
//        @Override
//        public RecipeSerializer<?> getType() {
//            return WoodcutterRecipe.Serializer.INSTANCE;
//        }
//
//        @Nullable
//        public JsonObject serializeAdvancement() {
//            return this.advancement.serializeToJson();
//        }
//
//        @Nullable
//        public ResourceLocation getAdvancementId() {
//            return this.advancementId;
//        }
//    }
}