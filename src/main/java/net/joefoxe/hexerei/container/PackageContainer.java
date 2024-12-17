package net.joefoxe.hexerei.container;

import net.joefoxe.hexerei.item.custom.CourierPackageItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class PackageContainer extends ItemStackContainer {
    public static final int OFFSET = 28;

    CourierPackageItem.PackageInvWrapper wrapper;

    private final DataSlot sealed = new DataSlot() {
        private int value;

        public int get() {
            return this.value;
        }

        public void set(int value) {
            this.value = value;
            if (wrapper != null)
                wrapper.setSealed(value);
        }
    };
    public PackageContainer(int windowId, ItemStack itemStack, Inventory playerInventory, InteractionHand hand, int slotIndex) {
        super(ModContainers.PACKAGE_CONTAINER.get(), windowId, playerInventory, itemStack, hand, slotIndex);
        this.addDataSlot(this.sealed);
        if (wrapper != null)
            setSealed(wrapper.getSealed() ? 1 : 0);
    }

    @Override
    protected void addContainerSlots() {
        if (stack.getItem() instanceof CourierPackageItem) {

            this.wrapper = new CourierPackageItem.PackageInvWrapper(stack);

            for (int i = 0; i < this.wrapper.getSlots(); i++)
                this.addSlot(new SlotItemHandler(this.wrapper, i, 41 - 15 + 21 * i, 16 - OFFSET) {
                    @Override
                    public void setChanged() {
                        super.setChanged();
                        wrapper.setStackInSlot(getSlotIndex(), getItemHandler().getStackInSlot(getSlotIndex()));
                    }
                });
        }
    }

    public boolean isEmpty() {
        if (this.wrapper != null)
            return this.wrapper.isEmpty();

        return true;
    }

    @Override
    public void removed(Player pPlayer) {
        if (!pPlayer.level().isClientSide)
            wrapper.setSealed(this.sealed.get());

        super.removed(pPlayer);
    }

    public int getSealed() {
        return this.sealed.get();
    }

    public void setSealed(int value) {
        this.sealed.set(value);
    }

    @Override
    public boolean clickMenuButton(Player pPlayer, int pId) {
        this.sealed.set(pId);
        return true;
    }

    @Override
    protected void addPlayerSlots() {
        layoutPlayerInventorySlots(11, 59 - OFFSET);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return playerIn.getItemInHand(this.hand).equals(this.stack);
    }
}
