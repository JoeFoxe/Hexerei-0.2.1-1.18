package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.CofferSyncCrowButtonToServer;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class CofferTile extends RandomizableContainerBlockEntity implements WorldlyContainer, Clearable {

    public final ItemStackHandler itemStackHandler = createHandler();
//    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemStackHandler);

//    protected NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);
    public static final int DEFAULT_COLOR = 0x422F1E;

    public int degreesOpened;
    public int buttonToggled = 0;
    public static final int lidOpenAmount = 112;
    public int degreesOpenedPrev = 0;
    public int dyeColor = DEFAULT_COLOR;

    public Component customName;

    public ItemStack self = null;
    private static final int[] SLOTS = IntStream.range(0, 36).toArray();


    public CofferTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);
    }

    public CofferTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.COFFER_TILE.get(),blockPos, blockState);
    }


//    public CofferTile(BlockEntityType<?> tileEntityTypeIn) {
//        super(tileEntityTypeIn);
//
//        buttonToggled = 0;
//    }


    @Override
    public BlockEntityType<?> getType() {
        return super.getType();
    }

    public void readInventory(HolderLookup.Provider provider, CompoundTag compound) {
        itemStackHandler.deserializeNBT(provider, compound);
    }

    public void setDyeColor(int dyeColor){
        this.dyeColor = dyeColor;
    }

    public int getDyeColor(){
        DyeColor dye = HexereiUtil.getDyeColorNamed(this.getDisplayName().getString());
        if(dye != null)
            return HexereiUtil.getColorValue(dye);
        return this.dyeColor;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);
        for(int i = 0; i < this.itemStackHandler.getSlots(); i++)
            items.set(i, this.itemStackHandler.getStackInSlot(i));
        return items;
    }

    @Override
    public ItemStack removeItem(int p_59613_, int p_59614_) {
        this.unpackLootTable(null);
        ItemStack itemstack = p_59613_ >= 0 && p_59613_ < this.itemStackHandler.getSlots() && !this.itemStackHandler.getStackInSlot(p_59613_).isEmpty() && p_59614_ > 0 ? this.getItems().get(p_59613_).split(p_59614_) : ItemStack.EMPTY;
        if (!itemstack.isEmpty()) {
            this.sync();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_59630_) {
        this.unpackLootTable(null);
        if(p_59630_ >= 0 && p_59630_ < this.itemStackHandler.getSlots())
        {
            this.itemStackHandler.setStackInSlot(p_59630_, ItemStack.EMPTY);
            return this.itemStackHandler.getStackInSlot(p_59630_);

        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getItem(int p_59611_) {
        this.unpackLootTable(null);
        return this.itemStackHandler.getStackInSlot(p_59611_);
    }

    @Override
    public void setItem(int p_59616_, ItemStack p_59617_) {
        this.unpackLootTable(null);
        this.itemStackHandler.setStackInSlot(p_59616_, p_59617_);
        if (p_59617_.getCount() > this.getMaxStackSize()) {
            p_59617_.setCount(this.getMaxStackSize());
        }

        this.sync();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        for(int i = 0; i <  Math.min(itemsIn.size(), this.itemStackHandler.getSlots()); i++)
            this.itemStackHandler.setStackInSlot(i, itemsIn.get( i));
    }

    @Override
    public void setChanged() {
        super.setChanged();

        if(this.self != null){

            CompoundTag tag = this.self.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

            CompoundTag inv = this.itemStackHandler.serializeNBT(this.level.registryAccess());

//            boolean flag = false;
//            for(int i = 0; i < 36; i++)
//            {
//                if(!this.itemStackHandler.getStackInSlot(i).isEmpty())
//                {
//                    flag = true;
//                    break;
//                }
//            }
            tag.put("Inventory", inv);

            this.self.set(DataComponents.DYED_COLOR, new DyedItemColor(this.dyeColor, true));

            tag.putInt("ButtonToggled", this.buttonToggled);


            Component customName = getCustomName();

            if (customName != null)
                if(!customName.getString().isEmpty())
                    this.self.set(DataComponents.CUSTOM_NAME, customName);


            this.self.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    @Override
    public void startOpen(Player p_18955_) {
        super.startOpen(p_18955_);
    }

    @Override
    public void stopOpen(Player p_18954_) {
        super.stopOpen(p_18954_);
    }

    @Override
    public boolean canPlaceItem(int p_18952_, ItemStack stack) {
        String id = HexereiUtil.getRegistryName(stack.getItem()).toString();
        if(HexConfig.COFFER_BLACKLIST.get().contains(id))
            return false;
        return super.canPlaceItem(p_18952_, stack);
    }

    @Override
    public int countItem(Item p_18948_) {
        return super.countItem(p_18948_);
    }


    @Override
    protected Component getDefaultName() {
        return Component.translatable("container." + Hexerei.MOD_ID + ".coffer");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new CofferContainer(id, this.level, this.worldPosition, player, player.player);
    }

    @Override
    public void clearContent() {
        super.clearContent();

        for(int i = 0; i < this.itemStackHandler.getSlots(); i++)
            this.itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
    }


    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider reg) {
        if (!this.trySaveLootTable(compound))
            compound.put("inv", itemStackHandler.serializeNBT(reg));
        if (this.customName != null)
            compound.putString("CustomName", Component.Serializer.toJson(this.customName, reg));
        compound.putInt("ButtonToggled", this.buttonToggled);
        compound.putInt("DyeColor", this.dyeColor);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);;
        if (!tryLoadLootTable(tag)){
            itemStackHandler.deserializeNBT(registries, tag.getCompound("inv"));
        }
        if (tag.contains("CustomName", 8))
            this.customName = Component.Serializer.fromJson(tag.getString("CustomName"), registries);
        if(tag.contains("ButtonToggled"))
            this.buttonToggled = tag.getInt("ButtonToggled");
        if(tag.contains("DyeColor")) {
            this.dyeColor = tag.getInt("DyeColor");

            //this fixes the coffers having a black outline when loaded from the old village
            if(this.dyeColor == 0)
                this.dyeColor = 4337438;
        }
    }


    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {

        return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
    }

    public void sync() {
        setChanged();

        if(level != null){
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






    private ItemStackHandler createHandler() {
        return new ItemStackHandler(36) {
            @Override
            protected void onContentsChanged(int slot) {
                sync();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return !HexConfig.COFFER_BLACKLIST.get().contains(HexereiUtil.getRegistryName(stack.getItem()).toString());
            }

            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!isItemValid(slot, stack)) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    //TODO do capabilities
//    @Nonnull
//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
//        if (cap == ForgeCapabilities.ITEM_HANDLER) {
//            return handler.cast();
//        }
//        return super.getCapability(cap, side);
//    }
//
//    @NotNull
//    @Override
//    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
//        if (cap == ForgeCapabilities.ITEM_HANDLER) {
//            return handler.cast();
//        }
//        return super.getCapability(cap);
//    }

    public ItemStack getItemStackInSlot(int slot) {
        return this.itemStackHandler.getStackInSlot(slot);
    }

    public int getNumberOfItems() {

        int num = 0;
        for(int i = 0; i < this.itemStackHandler.getSlots(); i++)
        {
            if(this.itemStackHandler.getStackInSlot(i) != ItemStack.EMPTY)
                num++;
        }
        return num;

    }

    public boolean hasItem(Item item) {

        if(this.itemStackHandler != null){
            for (int i = 0; i < this.itemStackHandler.getSlots(); i++) {
                if (this.itemStackHandler.getStackInSlot(i).is(item))
                    return true;
            }
            return false;
        }
        return false;

    }

    public boolean hasNonMaxStackItemStack(ItemStack item) {

        if(this.itemStackHandler != null){
            for (int i = 0; i < this.itemStackHandler.getSlots(); i++) {
                if (this.itemStackHandler.getStackInSlot(i) == item && this.itemStackHandler.getStackInSlot(i).getCount() < this.itemStackHandler.getStackInSlot(i).getMaxStackSize())
                    return true;
            }
            return false;
        }
        return false;

    }
    public boolean isEmpty() {

        if(this.itemStackHandler != null){
            for (int i = 0; i < this.itemStackHandler.getSlots(); i++) {
                if (!this.itemStackHandler.getStackInSlot(i).isEmpty())
                    return false;
            }
            return true;
        }
        return true;

    }

    public static double getDistanceToEntity(Entity entity, BlockPos pos) {
        double deltaX = entity.getX() - pos.getX();
        double deltaY = entity.getY() - pos.getY();
        double deltaZ = entity.getZ() - pos.getZ();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }

    @Override
    public Component getDisplayName() {
        return customName != null ? customName
                : Component.literal("");
    }

    @Override
    public Component getCustomName() {
        return this.customName;
    }

    @Override
    public boolean hasCustomName() {
        return customName != null;
    }

    @Override
    public Component getName() {
        return customName;
    }

    public int getDegreesOpened() {
        return this.degreesOpened;
    }
    public void setDegreesOpened(int degrees) {
        this.degreesOpened =  degrees;
    }

    public void setButtonToggled(int buttonToggled) {
        this.buttonToggled = buttonToggled;

        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new CofferSyncCrowButtonToServer(this, buttonToggled));


    }


    public int getButtonToggled() {
        return this.buttonToggled;
    }
//    public void setButtonToggled(int degrees) {
//        this.buttonToggled =  degrees;
//    }

//    @Override
    public void tick() {
//        if(level.isClientSide)
//            return;

        this.degreesOpenedPrev = this.degreesOpened;
        boolean flag = false;
        Player playerEntity = this.level.getNearestPlayer(this.worldPosition.getX(),this.worldPosition.getY(),this.worldPosition.getZ(), 5D, false);
        if(playerEntity != null) {
            if (Math.floor(getDistanceToEntity(playerEntity, this.worldPosition)) < 4D) {
                if (!this.level.isClientSide)
                    unpackLootTable(playerEntity);
                int distanceFromSide = (lidOpenAmount / 2) - Math.abs((lidOpenAmount / 2) - this.degreesOpened);
                flag = true;

                if (this.degreesOpened + Math.floor(((double) distanceFromSide / (double) (lidOpenAmount / 2)) * 6) + 2 < 112)
                    this.degreesOpened += Math.floor(((double) distanceFromSide / (double) (lidOpenAmount / 2)) * 6) + 2;
                else
                    this.degreesOpened = 112;
            }
        }


        if(!flag)
        {

            int distanceFromSide = (lidOpenAmount/2)-Math.abs((lidOpenAmount/2)-this.degreesOpened);

            if(this.degreesOpened + Math.floor(((double)distanceFromSide/(double)(lidOpenAmount/2)) * 6) + 2 > 0) {
                this.degreesOpened -= Math.floor(((double) distanceFromSide / (double) (lidOpenAmount / 2)) * 6) + 2;
                if(this.degreesOpened < 0)
                    this.degreesOpened = 0;
            }
            else
                this.degreesOpened = 0;

        }
    }

    @Override
    public int getContainerSize() {
        return 36;
    }

    @Override
    public int getMaxStackSize() {
        return super.getMaxStackSize();
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @org.jetbrains.annotations.Nullable Direction pDirection) {
        return itemStackHandler.isItemValid(pIndex, pItemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return true;
    }
}
