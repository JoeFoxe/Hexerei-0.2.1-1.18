package net.joefoxe.hexerei.integration.jei;

import com.google.common.collect.Lists;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.recipes.CauldronEmptyingRecipe;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.fluid.FluidIngredient;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.fluid.PotionFluidHandler;
import net.joefoxe.hexerei.fluid.PotionMixingRecipes;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.joefoxe.hexerei.fluid.PotionMixingRecipes.ALL;

public class BottlingRecipeJEI {

    public static List<CauldronEmptyingRecipe> getRecipeList(RecipeManager rm) {


        List<CauldronEmptyingRecipe> recipeList = new ArrayList<>(rm.getAllRecipesFor(ModRecipeTypes.CAULDRON_EMPTYING_TYPE.get()));
//        recipeList.add(new CauldronEmptyingRecipe(new ResourceLocation(Hexerei.MOD_ID, "cauldron_emptying/" + val + "_to_bottle"), Items.GLASS_BOTTLE.getDefaultInstance(), new FluidStack(Fluids.WATER, 250), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
//        recipeList.add(new CauldronEmptyingRecipe(Items.GLASS_BOTTLE.getDefaultInstance(), new FluidStack(Fluids.LAVA, 250), ModItems.LAVA_BOTTLE.get().getDefaultInstance()));
//        recipeList.add(new CauldronEmptyingRecipe(Items.GLASS_BOTTLE.getDefaultInstance(), new FluidStack(ModFluids.TALLOW_FLUID.get(), 250), ModItems.TALLOW_BOTTLE.get().getDefaultInstance()));
//        recipeList.add(new CauldronEmptyingRecipe(Items.GLASS_BOTTLE.getDefaultInstance(), new FluidStack(ModFluids.BLOOD_FLUID.get(), 250), ModItems.BLOOD_BOTTLE.get().getDefaultInstance()));

        for (FluidMixingRecipe recipe : PotionMixingRecipes.ALL){

            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            recipeList.forEach((rec) -> {
                if (rec.getFluid().getMatchingFluidStacks().get(0).isFluidEqual(recipe.getLiquidOutput())){
                    atomicBoolean.set(true);
                }
            });
            if (!atomicBoolean.get()){
                ItemStack potionItem = PotionFluidHandler.fillBottle(recipe.getLiquidOutput());
                CompoundTag tag = recipe.getLiquidOutput().getOrCreateTag();
                Potion potion = PotionUtils.getPotion(tag);
                String val = potion != null ? potion.getName("") : "missing";

                recipeList.add(new CauldronEmptyingRecipe(new ResourceLocation(Hexerei.MOD_ID, "cauldron_emptying/" + val + "_to_bottle"), Ingredient.of(Items.GLASS_BOTTLE.getDefaultInstance()), FluidIngredient.fromFluidStack(recipe.getLiquidOutput()), potionItem));
            }
        }

        return recipeList;
    }
}
