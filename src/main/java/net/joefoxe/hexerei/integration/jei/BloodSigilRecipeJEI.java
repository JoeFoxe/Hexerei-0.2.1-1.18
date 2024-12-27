package net.joefoxe.hexerei.integration.jei;

import com.google.common.collect.Lists;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class BloodSigilRecipeJEI {
    private final ItemStack INPUT = new ItemStack(ModItems.BLOOD_SIGIL.get());

    private FluidStack OUTPUT_FLUID = new FluidStack(Fluids.WATER, 250);

    public BloodSigilRecipeJEI(FluidStack outputFluid){
        this.OUTPUT_FLUID = outputFluid;
    }

    public ItemStack getInput() {
        return INPUT;
    }

    public FluidStack getOutputFluid() {
        return OUTPUT_FLUID;
    }

    public static List<BloodSigilRecipeJEI> getRecipeList() {

        List<BloodSigilRecipeJEI> recipeList = Lists.newArrayList();

        recipeList.add(new BloodSigilRecipeJEI(new FluidStack(ModFluids.BLOOD_FLUID.get(), 250)));

        return recipeList;
    }
}
