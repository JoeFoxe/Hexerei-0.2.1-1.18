package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.container.PackageContainer;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class CourierPackageItem extends BlockItem {

    public CourierPackageItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        return super.place(context);
    }


    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        // open message menu
        if(isSealed(pContext.getItemInHand()))
            return super.useOn(pContext);

        if (!pContext.isSecondaryUseActive()) {
            if (pContext.getPlayer() != null) {
                if (!pContext.getLevel().isClientSide) {
                    openMenu(pContext.getPlayer(), pContext.getHand(), pContext.getItemInHand());
                }
            }
        }

        return pContext.isSecondaryUseActive() ? super.useOn(pContext) : InteractionResult.CONSUME;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);

        if (!level.isClientSide) {
            openMenu(playerIn, handIn, itemstack);
        }
        return itemstack.getCount() == 1 ? (isSealed(itemstack) ? InteractionResultHolder.fail(itemstack) : InteractionResultHolder.consume(itemstack)) : InteractionResultHolder.fail(itemstack);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, world, tooltip, flagIn);

        CourierPackageItem.PackageInvWrapper wrapper = new CourierPackageItem.PackageInvWrapper(stack);

        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            if(wrapper.isEmpty()) {
                // say how to open the menu and seal
                tooltip.add(Component.translatable("tooltip.hexerei.courier_package_use").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.courier_package_menu").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.courier_package_send").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltip.add(Component.translatable("tooltip.hexerei.courier_package_must_be_sealed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else
            {
                if(wrapper.getSealed()) {
                    // say how to deliver or how to open
                    tooltip.add(Component.translatable("tooltip.hexerei.courier_package_send").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                    tooltip.add(Component.translatable("tooltip.hexerei.courier_package_open").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                } else {
                    // must be sealed
                    tooltip.add(Component.translatable("tooltip.hexerei.courier_package_must_be_sealed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                }
            }
        } else {
            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }
    }

    private boolean openMenu(Player player, InteractionHand hand, ItemStack stack) {
        if (!player.isSteppingCarefully() && stack.getCount() == 1) {
            if (!isSealed(stack)){
                MenuProvider containerProvider = createContainerProvider(stack, hand, stack.getTag());

                int slotIndex = hand == InteractionHand.OFF_HAND ? -1 : player.getInventory().selected;
                NetworkHooks.openScreen((ServerPlayer) player, containerProvider, b -> b.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1).writeByte(slotIndex));
                return true;
            }
        }
        return false;
    }

    private boolean isSealed(ItemStack stack) {
        CompoundTag tag = BlockItem.getBlockEntityData(stack);
        return tag != null && tag.contains("Sealed") && tag.getBoolean("Sealed");
    }

    private MenuProvider createContainerProvider(ItemStack itemStack, InteractionHand hand, CompoundTag list) {
        return new MenuProvider() {
            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new PackageContainer(i, itemStack, playerInventory, hand, hand == InteractionHand.OFF_HAND ? -1 : playerEntity.getInventory().selected);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("screen.hexerei.package");
            }

        };
    }


    @Override
    public @org.jetbrains.annotations.Nullable ICapabilityProvider initCapabilities(ItemStack stack, @org.jetbrains.annotations.Nullable CompoundTag nbt) {
        return new PackageInvWrapper(stack);
    }

    public static class PackageInvWrapper implements IItemHandlerModifiable, ICapabilityProvider
    {
        private final ItemStack stack;
        private final LazyOptional<IItemHandler> holder = LazyOptional.of(() -> this);

        private CompoundTag cachedTag;
        private NonNullList<ItemStack> itemStacksCache;
        private boolean sealed;

        public PackageInvWrapper(ItemStack stack) {
            this.stack = stack;
            this.sealed = getSealed();
        }

        @Override
        public int getSlots() {
            return 5;
        }

        public boolean isEmpty() {
            NonNullList<ItemStack> list = getItemList();
            for(ItemStack stack1 : list) {
                if (!stack1.isEmpty())
                    return false;
            }
            return true;
        }

        @Override
        @NotNull
        public ItemStack getStackInSlot(int slot) {
            if (!validateSlotIndex(slot))
                return ItemStack.EMPTY;
            return getItemList().get(slot);
        }

        @Override
        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty())
                return ItemStack.EMPTY;

            if (!isItemValid(slot, stack))
                return stack;

            validateSlotIndex(slot);

            NonNullList<ItemStack> itemStacks = getItemList();

            ItemStack existing = itemStacks.get(slot);

            int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

            if (!existing.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                    return stack;

                limit -= existing.getCount();
            }

            if (limit <= 0)
                return stack;

            boolean reachedLimit = stack.getCount() > limit;

            if (!simulate) {
                if (existing.isEmpty()) {
                    itemStacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                }
                else {
                    existing.grow(reachedLimit ? limit : stack.getCount());
                }
                setItemList(itemStacks);
            }

            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
        }

        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            NonNullList<ItemStack> itemStacks = getItemList();
            if (amount == 0)
                return ItemStack.EMPTY;

            validateSlotIndex(slot);

            ItemStack existing = itemStacks.get(slot);

            if (existing.isEmpty())
                return ItemStack.EMPTY;

            int toExtract = Math.min(amount, existing.getMaxStackSize());

            if (existing.getCount() <= toExtract) {
                if (!simulate) {
                    itemStacks.set(slot, ItemStack.EMPTY);
                    setItemList(itemStacks);
                    return existing;
                }
                else {
                    return existing.copy();
                }
            }
            else {
                if (!simulate) {
                    itemStacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                    setItemList(itemStacks);
                }

                return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
            }
        }

        private boolean validateSlotIndex(int slot) {
            if (slot < 0 || slot >= getSlots()) {
                System.out.println("invalid slot - " + slot);
                return false;
            }
            return true;
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (stack.getItem() instanceof CourierPackageItem) {
                PackageInvWrapper wrapper = new PackageInvWrapper(stack);
                if (!wrapper.isEmpty())
                    return false;
            }


            return stack.getItem().canFitInsideContainerItems();
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            validateSlotIndex(slot);
            if (!isItemValid(slot, stack)) throw new RuntimeException("Invalid stack " + stack + " for slot " + slot + ")");
            NonNullList<ItemStack> itemStacks = getItemList();
            itemStacks.set(slot, stack);
            setItemList(itemStacks);
        }

        private NonNullList<ItemStack> getItemList() {
            CompoundTag rootTag = BlockItem.getBlockEntityData(this.stack);
            if (cachedTag == null || !cachedTag.equals(rootTag))
                itemStacksCache = refreshItemList(rootTag);
            return itemStacksCache;
        }

        private NonNullList<ItemStack> refreshItemList(CompoundTag rootTag) {
            NonNullList<ItemStack> itemStacks = NonNullList.withSize(getSlots(), ItemStack.EMPTY);
            if (rootTag != null && rootTag.contains("Items", CompoundTag.TAG_LIST)) {
                ContainerHelper.loadAllItems(rootTag, itemStacks);
            }
            cachedTag = rootTag;
            return itemStacks;
        }


        private void setItemList(NonNullList<ItemStack> itemStacks) {

            boolean isEmpty = true;
            for (ItemStack itemStack : itemStacks) {
                if (!itemStack.isEmpty()) {
                    isEmpty = false;
                }
            }

            CompoundTag existing = BlockItem.getBlockEntityData(this.stack);
            CompoundTag rootTag = ContainerHelper.saveAllItems(existing == null ? new CompoundTag() : existing, itemStacks);

            if (!isEmpty) {
                BlockItem.setBlockEntityData(this.stack, ModTileEntities.COURIER_PACKAGE_TILE.get(), rootTag);
                cachedTag = rootTag;
            } else {
                this.stack.removeTagKey("BlockEntityTag");
                cachedTag = null;
            }

        }

        public void setSealed(int sealed) {

            this.sealed = sealed == 1;
            CompoundTag existing = BlockItem.getBlockEntityData(this.stack);
            if (existing != null) {
                if (isEmpty()) {
                    this.sealed = false;
                    existing.putBoolean("Sealed", false);
                    return;
                }
                existing.putBoolean("Sealed", this.sealed);

                cachedTag = existing;
            }
            else
                this.sealed = false;

        }

        public boolean getSealed() {
            CompoundTag existing = BlockItem.getBlockEntityData(this.stack);
            if (existing != null)
                return existing.getBoolean("Sealed");
            this.sealed = false;
            return false;
        }

        @Override
        @NotNull
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
            return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, this.holder);
        }
    }

}