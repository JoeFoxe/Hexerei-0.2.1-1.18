package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.data.owl.ClientOwlCourierDepotData;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotData;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotSavedData;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.OpenOwlCourierDepotNameEditorPacket;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OwlCourierDepotTile extends RandomizableContainerBlockEntity implements Clearable, MenuProvider {

//    public final ItemStackHandler itemHandler = createHandler();
//    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    protected NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);

//    public final ItemStackHandler itemHandler = createHandler();
//    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);


    public OwlCourierDepotTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);
    }


//    private ItemStackHandler createHandler() {
//        return new ItemStackHandler(6) {
//            @Override
//            protected void onContentsChanged(int slot) {
//                setChanged();
//            }
//
//            @Nonnull
//            @Override
//            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
//                if (!isItemValid(slot, stack)) {
//                    return stack;
//                }
//
//                return super.insertItem(slot, stack, simulate);
//            }
//        };
//    }


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider reg) {
        return this.save(new CompoundTag(), reg);
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {

        return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
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

    public CompoundTag save(CompoundTag tag, HolderLookup.Provider reg) {
        super.saveAdditional(tag, reg);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider reg) {
        super.saveAdditional(pTag, reg);
        ContainerHelper.saveAllItems(pTag, this.items, reg);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    public ItemInteractionResult interact(Player player, InteractionHand handIn) {

        GlobalPos globalPos = GlobalPos.of(this.getLevel().dimension(), this.getBlockPos());
        if (player instanceof ServerPlayer serverPlayer) {
            if(OwlCourierDepotSavedData.get().getDepots().containsKey(globalPos)) {
                OwlCourierDepotData depot = OwlCourierDepotSavedData.get().getDepots().get(globalPos);
                if (!depot.items.get(0).isEmpty()) {
                    ItemStack stack = depot.takeFirstSlotAndSlide();
                    if (player.getItemInHand(handIn).isEmpty())
                        player.setItemInHand(handIn, stack);
                    else
                        player.getInventory().placeItemBackInInventory(stack);
                    OwlCourierDepotSavedData.get().syncInvToClient(globalPos);
                    OwlCourierDepotSavedData.get().setDirty();
                    return ItemInteractionResult.SUCCESS;
                }
            } else {

                HexereiPacketHandler.sendToPlayerClient(new OpenOwlCourierDepotNameEditorPacket(this.getBlockPos()), serverPlayer);
                return ItemInteractionResult.SUCCESS;
            }
        } else {
            if(ClientOwlCourierDepotData.getDepots().containsKey(globalPos)) {
                OwlCourierDepotData depot = ClientOwlCourierDepotData.getDepots().get(globalPos);
                if (!depot.items.get(0).isEmpty()) {
                    return ItemInteractionResult.SUCCESS;
                }
            } else {
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
        return items;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        this.unpackLootTable(null);
        ItemStack itemstack = index >= 0 && index < this.items.size() && !this.items.get(index).isEmpty() && count > 0 ? this.getItems().get(index).split(count) : ItemStack.EMPTY;
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.items, index);
    }

    @Override
    public ItemStack getItem(int index) {
        this.unpackLootTable(null);
        return this.items.get(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.unpackLootTable(null);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.items.set(index, stack);

        this.setChanged();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.items = itemsIn;

        this.setChanged();
    }


    @Override
    public void clearContent() {
        super.clearContent();

        this.setChanged();
    }

    public OwlCourierDepotTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.OWL_COURIER_DEPOT_TILE.get(), blockPos, blockState);
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
