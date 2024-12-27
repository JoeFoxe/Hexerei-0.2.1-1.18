package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.data.recipes.PestleAndMortarRecipe;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class PestleAndMortarTile extends RandomizableContainerBlockEntity implements WorldlyContainer, Clearable, MenuProvider {

    public final ItemStackHandler itemHandler = createHandler();
//    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    protected NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
//    LazyOptional<? extends IItemHandler>[] handlers =
//            SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

    public int craftDelay;
    public static final int craftDelayMax = 100;
    public boolean crafted = false;
    public boolean crafting = false;
    public boolean grindSoundPlayed = false;
    public int grindingTimeMax = 200;
    public int grindingTime = 200;
    public ItemStack output = ItemStack.EMPTY;

    public PestleAndMortarTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            @Override
            protected void onContentsChanged(int slot) {
                sync();
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot != 5)
                    return 1;

                return 64;
            }
        };
    }


    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public void setItems(NonNullList<ItemStack> itemsIn) {
        this.items = itemsIn;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sync();
    }

    public void sync() {

        if (level != null) {
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


    //TODO do capabilities
//    @Override
//    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
////        if (facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
////            return handler.cast();
////        }
//
//        if (facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
//            return switch (facing) {
//                case UP -> handlers[0].cast();
//                case DOWN -> handlers[1].cast();
//                default -> handlers[2].cast();
//            };
//        }
//
//        return super.getCapability(capability, facing);
//    }
//
//    @Nonnull
//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
//
//        return super.getCapability(cap);
//    }

    public Item getItemInSlot(int slot) {
        return this.items.get(slot).getItem();
    }

    public ItemStack getItemStackInSlot(int slot) {
        return this.items.get(slot);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }


    public PestleAndMortarTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.PESTLE_AND_MORTAR_TILE.get(), blockPos, blockState);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= 0 && index < this.items.size()) {
            ItemStack itemStack = stack.copy();
            this.items.set(index, itemStack);
            if (index != 5)
                this.grindingTime = this.grindingTimeMax;
        }

        setChanged();
    }

    @Override
    public ItemStack removeItem(int index, int p_59614_) {
        this.unpackLootTable(null);
        ItemStack itemstack = ContainerHelper.removeItem(this.getItems(), index, p_59614_);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        setChanged();

        return itemstack;
    }



    private static CraftingContainer makeContainer(int width, int height, NonNullList<ItemStack> items) {
        return new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {

            public @NotNull ItemStack quickMoveStack(@NotNull Player p_218264_, int p_218265_) {
                return ItemStack.EMPTY;
            }


            public boolean stillValid(@NotNull Player p_29888_) {
                return false;
            }
        }, width, height, items);
    }

    public void craft() {
        CraftingContainer inv = makeContainer(5, 1, this.items);

        Optional<RecipeHolder<PestleAndMortarRecipe>> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.PESTLE_AND_MORTAR_TYPE.get(), inv.asCraftInput(), level);

        BlockEntity blockEntity = level.getBlockEntity(this.worldPosition);
        AtomicBoolean matches = new AtomicBoolean(false);
        if (blockEntity instanceof PestleAndMortarTile pestleAndMortarTile) {
            recipe.ifPresent(iRecipe -> {
                this.output = iRecipe.value().getResultItem(level.registryAccess());

                matches.set(true);
                if (pestleAndMortarTile.getItemInSlot(5) == Items.AIR && !this.crafting) {
                    this.crafting = true;
                    this.grindingTimeMax = iRecipe.value().getGrindingTime();
                    this.grindingTime = this.grindingTimeMax;
                    setChanged();


                }

            });
        }
        if (!matches.get()) {
            if (this.crafting) {
                this.crafting = false;
                setChanged();
            }
        }
//

    }


    private void craftTheItem(ItemStack output) {
        this.items.set(0, ItemStack.EMPTY);
        this.items.set(1, ItemStack.EMPTY);
        this.items.set(2, ItemStack.EMPTY);
        this.items.set(3, ItemStack.EMPTY);
        this.items.set(4, ItemStack.EMPTY);
        this.items.set(5, output);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        itemHandler.deserializeNBT(registries, tag.getCompound("inv"));
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }


//        if (tag.contains("CustomName", 8))
//            this.customName = Component.Serializer.fromJson(tag.getString("CustomName"));

        if (tag.contains("grindingTime", Tag.TAG_INT))
            grindingTime = tag.getInt("grindingTime");
        if (tag.contains("grindingTimeMax", Tag.TAG_INT))
            grindingTimeMax = tag.getInt("grindingTimeMax");
        if (tag.contains("crafting", Tag.TAG_INT))
            crafting = tag.getInt("crafting") == 1;
        if (tag.contains("crafted", Tag.TAG_INT))
            crafted = tag.getInt("crafted") == 1;
        super.loadAdditional(tag, registries);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container." + Hexerei.MOD_ID + ".pestle_and_mortar");
    }

    @Override
    protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
        return null;
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        ContainerHelper.saveAllItems(compound, this.items, registries);
        compound.put("inv", itemHandler.serializeNBT(registries));

        compound.putInt("grindingTime", grindingTime);

        compound.putInt("grindingTimeMax", grindingTimeMax);

        compound.putInt("crafted", crafted ? 1 : 0);

        compound.putInt("crafting", crafting ? 1 : 0);
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {

        return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        this.saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    public static double getDistanceToEntity(Entity entity, BlockPos pos) {
        double deltaX = entity.position().x() - pos.getX() - 0.5f;
        double deltaY = entity.position().y() - pos.getY() - 0.5f;
        double deltaZ = entity.position().z() - pos.getZ() - 0.5f;

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }

    public static double getDistance(float x1, float y1, float x2, float y2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }


    public float getAngle(Vec3 pos) {
        float angle = (float) Math.toDegrees(Math.atan2(pos.z() - this.getBlockPos().getZ() - 0.5f, pos.x() - this.getBlockPos().getX() - 0.5f));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    public float getSpeed(double pos, double posTo) {
        return (float) (0.01f + 0.10f * (Math.abs(pos - posTo) / 3f));
    }

    public Vec3 rotateAroundVec(Vec3 vector3dCenter, float rotation, Vec3 vector3d) {
        Vec3 newVec = vector3d.subtract(vector3dCenter);
        newVec = newVec.yRot(rotation / 180f * (float) Math.PI);
        newVec = newVec.add(vector3dCenter);

        return newVec;
    }

    public int putItems(int slot, @Nonnull ItemStack stack) {
        ItemStack stack1 = stack.copy();
        Random rand = new Random();

        if (this.items.get(slot).isEmpty()) {
            stack1.setCount(1);
//            this.itemHandler.setStackInSlot(slot, stack1);
            this.items.set(slot, stack1);
            this.grindingTime = this.grindingTimeMax;
            setChanged();
            stack.shrink(1);
            level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
            return 1;
        }

        return 0;
    }

    public int interactPestleAndMortar(Player player, BlockHitResult hit) {
        if (level == null) return 0;
        if (!player.isShiftKeyDown()) {

            if (!level.isClientSide) {
                if (!this.items.get(5).isEmpty()) {
                    player.getInventory().placeItemBackInInventory(this.items.get(5).copy());
                    level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
                    this.items.set(5, ItemStack.EMPTY);
                    setChanged();
                } else if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !level.isClientSide) {
                    Random rand = new Random();
                    if (this.items.get(0).isEmpty()) {
                        putItems(0, player.getItemInHand(InteractionHand.MAIN_HAND));
                        return 1;
                    } else if (this.items.get(1).isEmpty()) {
                        putItems(1, player.getItemInHand(InteractionHand.MAIN_HAND));
                        return 1;
                    } else if (this.items.get(2).isEmpty()) {
                        putItems(2, player.getItemInHand(InteractionHand.MAIN_HAND));
                        return 1;
                    } else if (this.items.get(3).isEmpty()) {
                        putItems(3, player.getItemInHand(InteractionHand.MAIN_HAND));
                        return 1;
                    } else if (this.items.get(4).isEmpty()) {
                        putItems(4, player.getItemInHand(InteractionHand.MAIN_HAND));
                        return 1;
                    }
                }
            }


        } else {
            if (!level.isClientSide) {
                if (!this.items.get(5).isEmpty()) {
                    player.getInventory().placeItemBackInInventory(this.items.get(5).copy());
                    level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
                    this.items.set(5, ItemStack.EMPTY);
                }

                if (!crafting) {
                    if (!this.items.get(0).isEmpty()) {
                        player.getInventory().placeItemBackInInventory(this.items.get(0).copy());
                        level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
                        this.items.set(0, ItemStack.EMPTY);
                        output = ItemStack.EMPTY;
                    }
                    if (!this.items.get(1).isEmpty()) {
                        player.getInventory().placeItemBackInInventory(this.items.get(1).copy());
                        level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
                        this.items.set(1, ItemStack.EMPTY);
                        output = ItemStack.EMPTY;
                    }
                    if (!this.items.get(2).isEmpty()) {
                        player.getInventory().placeItemBackInInventory(this.items.get(2).copy());
                        level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
                        this.items.set(2, ItemStack.EMPTY);
                        output = ItemStack.EMPTY;
                    }
                    if (!this.items.get(3).isEmpty()) {
                        player.getInventory().placeItemBackInInventory(this.items.get(3).copy());
                        level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
                        this.items.set(3, ItemStack.EMPTY);
                        output = ItemStack.EMPTY;
                    }
                    if (!this.items.get(4).isEmpty()) {
                        player.getInventory().placeItemBackInInventory(this.items.get(4).copy());
                        level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
                        this.items.set(4, ItemStack.EMPTY);
                        output = ItemStack.EMPTY;
                    }
                    setChanged();
                }
            }
        }

        return 0;
    }

    //    @Override
    public void tick() {

        if (level instanceof ServerLevel) {
            craft();
        }

        if (crafting) {
            if (this.grindingTime <= 0) {
                Random rand = new Random();
                if (level instanceof ServerLevel)
                    craftTheItem(output);
                //for setting a cooldown on crafting so the animations can take place
                this.crafted = true;
                this.crafting = false;
                setChanged();


            } else {
                this.grindingTime--;
                Random rand = new Random();
                float craftPercent2 = (this.grindingTimeMax - this.grindingTime) / 100f;
                double pestleYOffset = (Math.pow(Mth.sin(craftPercent2 * 3.14f * 5 - 1.2f), 4)) / 4f;
                if (pestleYOffset < 0.1 && level != null) {
                    if (!this.grindSoundPlayed) {
                        level.playSound(null, worldPosition, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.1F, level.random.nextFloat() * 0.4F + 2.1F);
                        this.grindSoundPlayed = true;
                    }
                    if (!this.items.get(0).isEmpty() && rand.nextInt(4) == 0)
                        level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.items.get(0)), worldPosition.getX() + 0.45f + rand.nextFloat() * 0.1f, worldPosition.getY() + 0.2d, worldPosition.getZ() + 0.45f + rand.nextFloat() * 0.1f, (rand.nextDouble() - 0.5d) / 15d, (rand.nextDouble() + 0.5d) * 0.15d, (rand.nextDouble() - 0.5d) / 15d);
                    if (!this.items.get(1).isEmpty() && rand.nextInt(4) == 0)
                        level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.items.get(1)), worldPosition.getX() + 0.45f + rand.nextFloat() * 0.1f, worldPosition.getY() + 0.2d, worldPosition.getZ() + 0.45f + rand.nextFloat() * 0.1f, (rand.nextDouble() - 0.5d) / 15d, (rand.nextDouble() + 0.5d) * 0.15d, (rand.nextDouble() - 0.5d) / 15d);
                    if (!this.items.get(2).isEmpty() && rand.nextInt(4) == 0)
                        level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.items.get(2)), worldPosition.getX() + 0.45f + rand.nextFloat() * 0.1f, worldPosition.getY() + 0.2d, worldPosition.getZ() + 0.45f + rand.nextFloat() * 0.1f, (rand.nextDouble() - 0.5d) / 15d, (rand.nextDouble() + 0.5d) * 0.15d, (rand.nextDouble() - 0.5d) / 15d);
                    if (!this.items.get(3).isEmpty() && rand.nextInt(4) == 0)
                        level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.items.get(3)), worldPosition.getX() + 0.45f + rand.nextFloat() * 0.1f, worldPosition.getY() + 0.2d, worldPosition.getZ() + 0.45f + rand.nextFloat() * 0.1f, (rand.nextDouble() - 0.5d) / 15d, (rand.nextDouble() + 0.5d) * 0.15d, (rand.nextDouble() - 0.5d) / 15d);
                    if (!this.items.get(4).isEmpty() && rand.nextInt(4) == 0)
                        level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.items.get(4)), worldPosition.getX() + 0.45f + rand.nextFloat() * 0.1f, worldPosition.getY() + 0.2d, worldPosition.getZ() + 0.45f + rand.nextFloat() * 0.1f, (rand.nextDouble() - 0.5d) / 15d, (rand.nextDouble() + 0.5d) * 0.15d, (rand.nextDouble() - 0.5d) / 15d);
                } else {
                    this.grindSoundPlayed = false;
                }
            }
        }

    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        if (direction == Direction.DOWN)
            return new int[]{5};
        return new int[]{0, 1, 2, 3, 4};
    }

    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return index != 5 && this.canPlaceItem(index, itemStackIn);
    }

    public boolean canPlaceItem(int index, ItemStack stack) {
        return this.items.get(index).isEmpty();
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack p_19240_, Direction p_19241_) {

        return (index == 5);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

}
