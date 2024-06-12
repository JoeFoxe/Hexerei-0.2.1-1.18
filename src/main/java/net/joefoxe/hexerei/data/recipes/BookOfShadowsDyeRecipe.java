package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookOfShadowsDyeRecipe extends CustomRecipe {
    protected BookOfShadowsDyeRecipe(ResourceLocation registryName, CraftingBookCategory category) {
        super(registryName, category);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {

        Map<Tuple<Integer, Integer>, List<DyeColor>> posDyes = new HashMap<>();
        Tuple<Tuple<Integer, Integer>, ItemStack> posBook = null;

        for (int slot = 0; slot < inv.getContainerSize(); slot++) {
            ItemStack slotStack = inv.getItem(slot);
            int column = slot % inv.getWidth();
            int row = slot / inv.getHeight();
            if (slotStack.isEmpty()) {
                continue;
            }
            if (slotStack.is(ModItems.BOOK_OF_SHADOWS.get())) {
                if (posBook != null) {
                    return false;
                }
                posBook = new Tuple<>(new Tuple<>(column, row), slotStack);
            } else if (slotStack.is(Tags.Items.DYES)) {
                DyeColor dyeColor = DyeColor.getColor(slotStack);
                if (dyeColor == null) {
                    return false;
                }
                posDyes.computeIfAbsent(new Tuple<>(column, row), c -> new ArrayList<>()).add(dyeColor);
            } else {
                return false;
            }
        }

        if (posBook == null || posDyes.isEmpty())
            return false;

        boolean mainDye = false;
        boolean trimDye = false;

        for (Map.Entry<Tuple<Integer, Integer>, List<DyeColor>> entry : posDyes.entrySet()) {
            if (entry.getKey().getA() == posBook.getA().getA() + 1 && entry.getKey().getB().equals(posBook.getA().getB())) {
                if (!mainDye)
                    mainDye = true;
                else
                    return false;
            }
            else if (entry.getKey().getA() == posBook.getA().getA() - 1 && entry.getKey().getB().equals(posBook.getA().getB())) {
                if (!mainDye)
                    mainDye = true;
                else
                    return false;
            }
            else if (entry.getKey().getB() == posBook.getA().getB() + 1 && entry.getKey().getA().equals(posBook.getA().getA())) {
                if (!trimDye)
                    trimDye = true;
                else
                    return false;
            }
            else if (entry.getKey().getB() == posBook.getA().getB() - 1 && entry.getKey().getA().equals(posBook.getA().getA())) {
                if (!trimDye)
                    trimDye = true;
                else
                    return false;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        Map<Tuple<Integer, Integer>, List<DyeColor>> posDyes = new HashMap<>();
        Tuple<Tuple<Integer, Integer>, ItemStack> posBook = null;

        for (int slot = 0; slot < inv.getContainerSize(); slot++) {
            ItemStack slotStack = inv.getItem(slot);
            if (slotStack.isEmpty()) {
                continue;
            }
            int column = slot % inv.getWidth();
            int row = slot / inv.getHeight();
            if (slotStack.is(ModItems.BOOK_OF_SHADOWS.get())) {
                if (posBook != null) {
                    return ItemStack.EMPTY;
                }

                posBook = new Tuple<>(new Tuple<>(column, row), slotStack);
            } else if (slotStack.is(Tags.Items.DYES)) {
                DyeColor dyeColor = DyeColor.getColor(slotStack);
                if (dyeColor == null) {
                    return ItemStack.EMPTY;
                }
                posDyes.computeIfAbsent(new Tuple<>(column, row), c -> new ArrayList<>()).add(dyeColor);
            } else {
                return ItemStack.EMPTY;
            }
        }
        if (posBook == null) {
            return ItemStack.EMPTY;
        }

        ItemStack book = posBook.getB().copy();
        book.setCount(1);

        applyColors(posDyes, book, posBook.getA());

        return book;
    }

    private void applyColors(Map<Tuple<Integer, Integer>, List<DyeColor>> posDyes, ItemStack book, Tuple<Integer, Integer> posBook) {
        List<DyeColor> mainDyes = new ArrayList<>();
        List<DyeColor> trimDyes = new ArrayList<>();

        for (Map.Entry<Tuple<Integer, Integer>, List<DyeColor>> entry : posDyes.entrySet()) {
            if (entry.getKey().getA() == posBook.getA() + 1 && entry.getKey().getB().equals(posBook.getB())) {
                mainDyes.addAll(entry.getValue());
            }
            if (entry.getKey().getA() == posBook.getA() - 1 && entry.getKey().getB().equals(posBook.getB())) {
                mainDyes.addAll(entry.getValue());
            }
            if (entry.getKey().getB() == posBook.getB() + 1 && entry.getKey().getA().equals(posBook.getA())) {
                trimDyes.addAll(entry.getValue());
            }
            if (entry.getKey().getB() == posBook.getB() - 1 && entry.getKey().getA().equals(posBook.getA())) {
                trimDyes.addAll(entry.getValue());
            }
        }

        if (mainDyes.size() > 0)
            book.getOrCreateTag().putInt("dyeColor1", mainDyes.get(0).getId());
        if (trimDyes.size() > 0)
            book.getOrCreateTag().putInt("dyeColor2", trimDyes.get(0).getId());
    }


    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    public ItemStack getOutput() {
        return getResultItem(null);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BOOK_OF_SHADOWS_DYE_SERIALIZER.get();
    }
}