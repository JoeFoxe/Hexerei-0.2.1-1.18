package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.item.custom.KeychainItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;


public class KeychainUndoRecipe extends CustomRecipe {

    public KeychainUndoRecipe(CraftingBookCategory cBc) {
        super(cBc);

    }

    @Override
    public boolean isSpecial() {
        return true;
    }
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput p_44004_) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_44004_.size(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = p_44004_.getItem(i);
            if (item.hasCraftingRemainingItem()) {
                nonnulllist.set(i, item.getCraftingRemainingItem());
            }
            if(item.getItem() instanceof KeychainItem keychainItem)
                nonnulllist.set(i, new ItemStack(keychainItem));
        }
        return nonnulllist;
    }

    @Override
    public boolean matches(CraftingInput inventory, Level world) {
        int keychain = 0;
        int other = 0;

        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof KeychainItem) {
                    CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

                    if(tag.contains("Items")){
                        ++keychain;
                    }
                } else {
                    ++other;
                }

                if (other > 1 || keychain > 1) {
                    return false;
                }
            }
        }

        return keychain == 1 && other == 0;
    }

    @Override
    public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registryAccess) {
        ItemStack keychain = ItemStack.EMPTY;

        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof KeychainItem) {
                    keychain = stack.copy();
                    keychain.setCount(1);
                }
            }
        }
        if (keychain.getItem() instanceof KeychainItem) {
            CompoundTag tag = keychain.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            ListTag list = tag.getList("Items", 10);
            keychain = ItemStack.parseOptional(registryAccess, list.getCompound(0));
        }

        return keychain;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 1;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.KEYCHAIN_UNDO_SERIALIZER.get();
    }
}