package net.joefoxe.hexerei.integration.jei;

import com.google.common.collect.Lists;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.fluid.PotionFluidHandler;
import net.joefoxe.hexerei.fluid.PotionMixingRecipes;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
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
    private static final Random random = new Random(0);

    private ItemStack INPUT = new ItemStack(Items.GLASS_BOTTLE);

    private FluidStack INPUT_FLUID = new FluidStack(Fluids.WATER, 250);
    private ItemStack OUTPUT = new ItemStack(ModItems.BLOOD_BOTTLE.get());

    public BottlingRecipeJEI(ItemStack input, FluidStack inputFluid, ItemStack output){
        this.INPUT = input;
        this.INPUT_FLUID = inputFluid;
        this.OUTPUT = output;
    }

    public ItemStack getInput() {
        return INPUT;
    }

    public FluidStack getInputFluid() {

        return INPUT_FLUID;
    }

    public ItemStack getOutput() {

        return OUTPUT;
    }

    public static List<BottlingRecipeJEI> getRecipeList() {

        List<BottlingRecipeJEI> recipeList = Lists.newArrayList();

        recipeList.add(new BottlingRecipeJEI(Items.GLASS_BOTTLE.getDefaultInstance(), new FluidStack(Fluids.WATER, 250), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
        recipeList.add(new BottlingRecipeJEI(Items.GLASS_BOTTLE.getDefaultInstance(), new FluidStack(Fluids.LAVA, 250), ModItems.LAVA_BOTTLE.get().getDefaultInstance()));
        recipeList.add(new BottlingRecipeJEI(Items.GLASS_BOTTLE.getDefaultInstance(), new FluidStack(ModFluids.TALLOW_FLUID.get(), 250), ModItems.TALLOW_BOTTLE.get().getDefaultInstance()));
        recipeList.add(new BottlingRecipeJEI(Items.GLASS_BOTTLE.getDefaultInstance(), new FluidStack(ModFluids.BLOOD_FLUID.get(), 250), ModItems.BLOOD_BOTTLE.get().getDefaultInstance()));

        for (FluidMixingRecipe recipe : PotionMixingRecipes.ALL){

            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            recipeList.forEach((rec) -> {
                if (rec.getInputFluid().isFluidEqual(recipe.getLiquidOutput())){
                    atomicBoolean.set(true);
                }
            });
            if (!atomicBoolean.get()){
                ItemStack potionItem = PotionFluidHandler.fillBottle(recipe.getLiquidOutput());
                recipeList.add(new BottlingRecipeJEI(Items.GLASS_BOTTLE.getDefaultInstance(), recipe.getLiquidOutput(), potionItem));
            }
        }

        return recipeList;
    }
}
