package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BroomStandTile extends RandomizableContainerBlockEntity implements Clearable, MenuProvider {

//    public final ItemStackHandler itemHandler = createHandler();
//    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
//    protected NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);

    public final ItemStackHandler itemHandler = createHandler();


    public BroomStandTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);
    }


    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() instanceof BroomItem;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!isItemValid(slot, stack)) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sync();
    }

    public void sync() {
        if (this.level != null) {
            if (!level.isClientSide) {
                CompoundTag tag = new CompoundTag();
                this.saveAdditional(tag, level.registryAccess());
                HexereiPacketHandler.sendToNearbyClient(level, worldPosition, new TESyncPacket(worldPosition, tag));
            }

            if (this.level != null)
                this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition),
                        Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inv", itemHandler.serializeNBT(registries));
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inv"));
    }

    public int interact(Player player, InteractionHand handIn, boolean withItem) {
        ItemStack stack = this.itemHandler.getStackInSlot(0);
        if (stack.isEmpty()) {
            if (withItem) {
                Random rand = new Random();
                if (stack.isEmpty()) {
                    if(this.itemHandler.isItemValid(0, player.getItemInHand(handIn))) {
                        this.itemHandler.setStackInSlot(0, player.getItemInHand(handIn));
                        level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
                        player.setItemInHand(handIn, ItemStack.EMPTY);
                        setChanged();
                        return 1;
                    }
                }

            }
        } else {

            if (player.getMainHandItem().isEmpty())
                player.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
            else
                player.getInventory().placeItemBackInInventory(stack.copy());

            level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
            this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);

            setChanged();

            return 1;
        }

        return 0;
    }

    @Override
    public void requestModelDataUpdate() {
        super.requestModelDataUpdate();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }


    @Override
    protected Component getDefaultName() {
        return null;
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return null;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);
        for (int i = 0; i < this.itemHandler.getSlots(); i++)
            items.set(i, this.itemHandler.getStackInSlot(i));
        return items;
    }

    @Override
    public ItemStack removeItem(int p_59613_, int p_59614_) {
        this.unpackLootTable(null);
        ItemStack itemstack = p_59613_ >= 0 && p_59613_ < this.itemHandler.getSlots() && !this.itemHandler.getStackInSlot(p_59613_).isEmpty() && p_59614_ > 0 ? this.getItems().get(p_59613_).split(p_59614_) : ItemStack.EMPTY;
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_59630_) {
        this.unpackLootTable(null);
        if (p_59630_ >= 0 && p_59630_ < this.itemHandler.getSlots()) {
            this.itemHandler.setStackInSlot(p_59630_, ItemStack.EMPTY);
            return this.itemHandler.getStackInSlot(p_59630_);

        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getItem(int p_59611_) {
        this.unpackLootTable(null);
        return this.itemHandler.getStackInSlot(p_59611_);
    }

    @Override
    public void setItem(int p_59616_, ItemStack p_59617_) {
        this.unpackLootTable(null);
        this.itemHandler.setStackInSlot(p_59616_, p_59617_);
        if (p_59617_.getCount() > this.getMaxStackSize()) {
            p_59617_.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        for (int i = 0; i < Math.min(itemsIn.size(), this.itemHandler.getSlots()); i++)
            this.itemHandler.setStackInSlot(i, itemsIn.get(i));
    }


    @Override
    public void clearContent() {
        super.clearContent();
//        this.items.clear();

        for (int i = 0; i < this.itemHandler.getSlots(); i++)
            this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
    }

    public BroomStandTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.BROOM_STAND_TILE.get(), blockPos, blockState);
    }


    public float getAngle(Vec3 pos) {
        float angle = (float) Math.toDegrees(Math.atan2(pos.z() - this.worldPosition.getZ() - 0.5f, pos.x() - this.worldPosition.getX() - 0.5f));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    public void tick() {
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

}
