package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.books.*;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.sounds.ModSounds;
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
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static net.joefoxe.hexerei.util.HexereiUtil.moveTo;
import static net.joefoxe.hexerei.util.HexereiUtil.moveToAngle;

public class BookOfShadowsAltarTile extends RandomizableContainerBlockEntity implements Clearable, MenuProvider {

    public final ItemStackHandler itemHandler = createHandler();
    private final Optional<IItemHandler> handler = Optional.of(itemHandler);

    public PageDrawing drawing;
    public float[] bookmarkHoverAmount = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public float degreesSpun;
    public float degreesSpunTo;
    public float degreesSpunRender;
    public float degreesSpunSpeed;
    public float degreesOpened;
    public float degreesOpenedTo;
    public float degreesOpenedRender;
    public float degreesOpenedSpeed;
    public float degreesFlopped;
    public float degreesFloppedTo;
    public float degreesFloppedRender;
    public float degreesFloppedSpeed;
    public boolean drawTooltip;
    public float tooltipScale;
    public float tooltipScaleOld;
    public int turnPage;
    public int turnToPage;
    public int turnToChapter;
    public float buttonScale;
    public float buttonScaleTo;
    public float buttonScaleRender;
    public float buttonScaleSpeed;
    public float bookmarkSelectorScale;
    public float pageOneRotation;
    public float pageTwoRotation;
    public float pageOneRotationLast;
    public float pageTwoRotationLast;
    public float pageOneRotationTo;
    public float pageTwoRotationTo;
    public float pageOneRotationRender;
    public float pageTwoRotationRender;
    public float pageOneRotationSpeed;
    public float pageTwoRotationSpeed;
    public float numberOfCandles;
    public float maxCandles = 3;
    public BlockPos candlePos1;
    public BlockPos candlePos2;
    public BlockPos candlePos3;
    public int candlePos1Slot;
    public int candlePos2Slot;
    public int candlePos3Slot;
    public float degreesSpunCandles;
    public float tickCount;

    public Vec3 closestPlayerPos;
    public Player closestPlayer;
    public double closestDist;

    public final double maxDist = 5;

    public int slotClicked = -1;

    public int slotClickedTick = 0;


    public BookOfShadowsAltarTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);

        this.drawTooltip = false;
        this.tooltipScale = 0;
        this.tooltipScaleOld = 0;
        this.turnPage = 0;
        this.buttonScale = 1;
        this.buttonScaleTo = 1;
        this.buttonScaleRender = 1;
        this.buttonScaleSpeed = 0;
        this.bookmarkSelectorScale = 0;
        this.pageOneRotation = 0;
        this.pageOneRotationLast = 0;
        this.pageOneRotationRender = 0;
        this.pageOneRotationTo = 0;
        this.pageOneRotationSpeed = 0;
        this.pageTwoRotation = 0;
        this.pageTwoRotationLast = 0;
        this.pageTwoRotationRender = 0;
        this.pageTwoRotationTo = 0;
        this.pageTwoRotationSpeed = 0;
        this.degreesFlopped = 90;
        this.degreesFloppedTo = 90;
        this.degreesFloppedSpeed = 0;
        this.degreesFloppedRender = 90;
        this.degreesOpened = 90; // reversed because the model is made so the book is opened from the start so offsetting 90 degrees from the start will close the book
        this.degreesOpenedTo = 90;
        this.degreesOpenedSpeed = 0;
        this.degreesOpenedRender = 90;
        this.degreesSpun = 0;
        this.degreesSpunTo = 0;
        this.degreesSpunSpeed = 0;
        this.degreesSpunRender = 0;
        this.candlePos1Slot = 0;
        this.candlePos2Slot = 0;
        this.candlePos3Slot = 0;
        this.drawing = new PageDrawing();
    }


    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return true;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 64;
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
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
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

    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        return tag;
    }


    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inv", itemHandler.serializeNBT(registries));
        tag.putInt("turnPage", this.turnPage);
        tag.putInt("turnToPage", this.turnToPage);
        tag.putInt("turnToChapter", this.turnToChapter);
        tag.putFloat("degreesSpun", this.degreesSpun);
        tag.putFloat("degreesFlopped", this.degreesFlopped);
        tag.putFloat("degreesOpened", this.degreesOpened);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inv"));
        this.turnPage = tag.getInt("turnPage");
        this.turnToPage = tag.getInt("turnToPage");
        this.turnToChapter = tag.getInt("turnToChapter");
        this.degreesSpun = tag.getFloat("degreesSpun");
        this.degreesSpunRender = degreesSpun;
        this.degreesFlopped = tag.getFloat("degreesFlopped");
        this.degreesFloppedRender = degreesFlopped;
        this.degreesOpened = tag.getFloat("degreesOpened");
        this.degreesOpenedRender = degreesOpened;
    }

    public int interactWithItem(Player player, InteractionHand handIn) {
        ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
        if (!player.isShiftKeyDown() && level != null) {
            if (stack.isEmpty()) {
                Random rand = new Random();
                if (stack.isEmpty()) {
                    this.itemHandler.setStackInSlot(0, player.getItemInHand(handIn));
                    level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
                    player.setItemInHand(handIn, ItemStack.EMPTY);
                    ItemStack stack2 = this.itemHandler.getStackInSlot(0).copy();
                    BookData bookData = stack2.get(ModDataComponents.BOOK);
                    if (stack.getItem() instanceof HexereiBookItem) {

                        if (bookData != null){
                            this.turnToChapter = bookData.getChapter();
                            this.turnToPage = bookData.getPage();
                            this.closestDist = (getDistanceToEntity(player, this.worldPosition));
                            this.closestPlayerPos = player.position();
                            this.closestPlayer = player;
                            this.degreesSpun = 270 - getAngle(this.closestPlayerPos);
                            this.degreesSpunTo = 270 - getAngle(this.closestPlayerPos);
                            this.degreesSpunRender = 270 - getAngle(this.closestPlayerPos);
                        }

                        setChanged();
                    }
                    return 1;
                }
            } else {
                if (stack.getItem() instanceof HexereiBookItem) {
                    BookData bookData = stack.get(ModDataComponents.BOOK);
                    if (bookData != null){
                        if (!bookData.isOpened() && this.degreesOpened == 90) {

                            level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_OPENING.get(), SoundSource.BLOCKS, 1f, (level.random.nextFloat() * 0.25f + 0.75f));
                            bookData.setOpened(true);
                            stack.set(ModDataComponents.BOOK, bookData);
                            this.itemHandler.setStackInSlot(0, stack);
                            setChanged();
                            return 1;
                        }
                    }
                }
            }
        } else if (!stack.isEmpty()) {

            setChanged();

            player.getInventory().placeItemBackInInventory(this.itemHandler.getStackInSlot(0).copy());

            resetBookRotations();

            level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
            this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);

            setChanged();

            return 1;
        }

        return 0;
    }

    public int interactWithoutItem(Player player) {
        ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
        if (!player.isShiftKeyDown()) {
            if (stack.getItem() instanceof HexereiBookItem) {
                BookData bookData = stack.get(ModDataComponents.BOOK);
                if (bookData != null){
                    if (!bookData.isOpened() && this.degreesOpened == 90) {

                        level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_OPENING.get(), SoundSource.BLOCKS, 1f, (level.random.nextFloat() * 0.25f + 0.75f));
                        bookData.setOpened(true);
                        stack.set(ModDataComponents.BOOK, bookData);
                        this.itemHandler.setStackInSlot(0, stack);
                        setChanged();
                        return 1;
                    }
                }
            }
        } else if (!stack.isEmpty()) {

            setChanged();

            player.getInventory().placeItemBackInInventory(this.itemHandler.getStackInSlot(0).copy());

            resetBookRotations();

            level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
            this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);

            setChanged();

            return 1;
        }

        return 0;
    }

    public void resetBookRotations() {
        this.degreesFlopped = 90;
        this.degreesFloppedRender = 90;
        this.degreesOpened = 90; // reversed because the model is made so the book is opened from the start so offsetting 90 degrees from the start will close the book
        this.degreesOpenedRender = 90;
        this.degreesSpun = 0;
        this.degreesSpunRender = 0;
        this.degreesSpunTo = 0;
        this.pageOneRotation = 0;
        this.pageOneRotationTo = 0;
        this.pageOneRotationRender = 0;
        this.pageTwoRotation = 0;
        this.pageTwoRotationTo = 0;
        this.pageTwoRotationRender = 0;
        this.turnPage = 0;
        this.turnToPage = 0;
        this.turnToChapter = 0;
    }

    @Override
    public void requestModelDataUpdate() {
        super.requestModelDataUpdate();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
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

    public BookOfShadowsAltarTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.BOOK_OF_SHADOWS_ALTAR_TILE.get(), blockPos, blockState);
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
        float angle = (float) Math.toDegrees(Math.atan2(pos.z() - this.worldPosition.getZ() - 0.5f, pos.x() - this.worldPosition.getX() - 0.5f));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    private boolean getCandle(Level world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof CandleTile;
    }

    //    @Override
    public void tick() {
        if (level.isClientSide) {
//              used for testing positioning on the book pages
////            for(int i = 0; i< 10; i++){
//            {
//                float xIn = 0.75f;
//                float yIn = 0.25f;
//
//
//
//                Vector3f vector3f = new Vector3f(0, 0, 0);
//                Vector3f vector3f_1 = new Vector3f(0.35f - xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);
//
//                BlockPos blockPos = this.getBlockPos();
//
//                vector3f_1.transform(Vector3f.YP.rotationDegrees(10 + this.degreesOpened / 1.12f));
//                vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - this.degreesOpened / 2f));
//
//
////                Vector3f vector3f = new Vector3f(0, 0, 0);
////                Vector3f vector3f_1 = new Vector3f(-0.05f + -xIn * 0.06f, 0.5f - yIn * 0.061f, -0.02f);
////
////                BlockPos blockPos = this.getBlockPos();
////
////                vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + this.degreesOpened / 1.12f)));
////                vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - this.degreesOpened / 2f));
//
//                vector3f.add(vector3f_1);
//
//                vector3f.transform(Vector3f.YP.rotationDegrees(this.degreesSpun));
//
//                Vec3 vec = new Vec3(blockPos.getX() + 0.5f + (float) Math.sin((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f), blockPos.getY() + 18 / 16f, blockPos.getZ() + 0.5f + (float) Math.cos((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f));
//                Vec3 vec2 = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f), vector3f.y() + blockPos.getY() + 18 / 16f, vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f));
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec2.x, vec2.y, vec2.z, 0, 0, 0);
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec2.x, vec2.y, vec2.z, 0, 0, 0);
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec2.x, vec2.y, vec2.z, 0, 0, 0);
//            }
//
//
//
//
//            {
//                float xIn = 0.75f;
//                float yIn = 0.25f;
//
//                Vector3f vector3f = new Vector3f(0, 0, 0);
//                Vector3f vector3f_1 = new Vector3f(-0.05f + -xIn * 0.06f, 0.5f - yIn * 0.061f, -0.03f);
//
//                BlockPos blockPos = this.getBlockPos();
//
//                vector3f_1.transform(Vector3f.YP.rotationDegrees(-(10 + this.degreesOpened / 1.12f)));
//                vector3f_1.transform(Vector3f.XP.rotationDegrees(45 - this.degreesOpened / 2f));
//
//                vector3f.add(vector3f_1);
//
//                vector3f.transform(Vector3f.YP.rotationDegrees(this.degreesSpun));
//
//                Vec3 vec = new Vec3(blockPos.getX() + 0.5f + (float) Math.sin((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f), blockPos.getY() + 18 / 16f, blockPos.getZ() + 0.5f + (float) Math.cos((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f));
//                Vec3 vec2 = new Vec3(vector3f.x() + blockPos.getX() + 0.5f + (float) Math.sin((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f), vector3f.y() + blockPos.getY() + 18 / 16f, vector3f.z() + blockPos.getZ() + 0.5f + (float) Math.cos((this.degreesSpun) / 57.1f) / 32f * (this.degreesOpened / 5f - 12f));
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec2.x, vec2.y, vec2.z, 0, 0, 0);
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec2.x, vec2.y, vec2.z, 0, 0, 0);
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec2.x, vec2.y, vec2.z, 0, 0, 0);
//            }
//                this.level.addParticle(ModParticleTypes.BLOOD_BIT.get(), vec.x, vec.y, vec.z, 0, 0, 0);
//            }

            this.tooltipScaleOld = this.tooltipScale;
            if (this.drawTooltip) {
                this.tooltipScale = moveTo(this.tooltipScale, 1f, 0.075f);
            } else {
                this.tooltipScale = moveTo(this.tooltipScale, 0f, 0.15f);
            }

            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = stack.get(ModDataComponents.BOOK);

            if (this.turnPage != 0 || (bookData != null && !bookData.isOpened())) {
                this.buttonScaleSpeed = 0.1f * (this.buttonScale + 0.25f);
                this.buttonScaleTo = 0;
            } else {
                this.buttonScaleSpeed = 0.25f * (this.buttonScale + 0.25f);
                this.buttonScaleTo = 1;
            }


            tickCount++;

            closestPlayerPos = null;


        }
        numberOfCandles = 0;

        candlePos1 = new BlockPos(0, 0, 0);
        candlePos2 = new BlockPos(0, 0, 0);
        candlePos3 = new BlockPos(0, 0, 0);

        for (int k = -1; k <= 1; ++k) {
            for (int l = -1; l <= 1; ++l) {
                if ((k != 0 || l != 0)) {
                    if ((level.getBlockEntity(worldPosition.offset(l * 2, 0, k * 2))) instanceof CandleTile candleTile && numberOfCandles < maxCandles) {
                        for (int i = 0; i < candleTile.getNumberOfCandles(); i++) {

                            if ((i == 0 && candleTile.candles.get(0).lit)
                                    || (i == 1 && candleTile.candles.get(1).lit)
                                    || (i == 2 && candleTile.candles.get(2).lit)
                                    || (i == 3 && candleTile.candles.get(3).lit)) {
                                if (numberOfCandles == 0) {
                                    candlePos1 = worldPosition.offset(l * 2, 0, k * 2);
                                    candlePos1Slot = i;
                                }
                                if (numberOfCandles == 1) {
                                    candlePos2 = worldPosition.offset(l * 2, 0, k * 2);
                                    candlePos2Slot = i;
                                }
                                if (numberOfCandles == 2) {
                                    candlePos3 = worldPosition.offset(l * 2, 0, k * 2);
                                    candlePos3Slot = i;
                                }
                                numberOfCandles++;
                            }
                        }

                    }
                    if ((level.getBlockEntity(worldPosition.offset(l * 2, 1, k * 2))) instanceof CandleTile candleTile && numberOfCandles < maxCandles) {

                        for (int i = 0; i < candleTile.getNumberOfCandles(); i++) {

                            if ((i == 0 && candleTile.candles.get(0).lit)
                                    || (i == 1 && candleTile.candles.get(1).lit)
                                    || (i == 2 && candleTile.candles.get(2).lit)
                                    || (i == 3 && candleTile.candles.get(3).lit)) {
                                if (numberOfCandles == 0) {
                                    candlePos1 = worldPosition.offset(l * 2, 1, k * 2);
                                    candlePos1Slot = i;
                                }
                                if (numberOfCandles == 1) {
                                    candlePos2 = worldPosition.offset(l * 2, 1, k * 2);
                                    candlePos2Slot = i;
                                }
                                if (numberOfCandles == 2) {
                                    candlePos3 = worldPosition.offset(l * 2, 1, k * 2);
                                    candlePos3Slot = i;
                                }
                                numberOfCandles++;
                            }
                        }
                    }

                    if (l != 0 && k != 0) {

                        if ((level.getBlockEntity(worldPosition.offset(l * 2, 0, k))) instanceof CandleTile candleTile && numberOfCandles < maxCandles) {

                            for (int i = 0; i < candleTile.getNumberOfCandles(); i++) {

                                if ((i == 0 && candleTile.candles.get(0).lit)
                                        || (i == 1 && candleTile.candles.get(1).lit)
                                        || (i == 2 && candleTile.candles.get(2).lit)
                                        || (i == 3 && candleTile.candles.get(3).lit)) {
                                    if (numberOfCandles == 0) {
                                        candlePos1 = worldPosition.offset(l * 2, 0, k);
                                        candlePos1Slot = i;
                                    }
                                    if (numberOfCandles == 1) {
                                        candlePos2 = worldPosition.offset(l * 2, 0, k);
                                        candlePos2Slot = i;
                                    }
                                    if (numberOfCandles == 2) {
                                        candlePos3 = worldPosition.offset(l * 2, 0, k);
                                        candlePos3Slot = i;
                                    }
                                    numberOfCandles++;
                                }
                            }
                        }
                        if ((level.getBlockEntity(worldPosition.offset(l * 2, 1, k))) instanceof CandleTile candleTile && numberOfCandles < maxCandles) {

                            for (int i = 0; i < candleTile.getNumberOfCandles(); i++) {

                                if ((i == 0 && candleTile.candles.get(0).lit)
                                        || (i == 1 && candleTile.candles.get(1).lit)
                                        || (i == 2 && candleTile.candles.get(2).lit)
                                        || (i == 3 && candleTile.candles.get(3).lit)) {
                                    if (numberOfCandles == 0) {
                                        candlePos1 = worldPosition.offset(l * 2, 1, k);
                                        candlePos1Slot = i;
                                    }
                                    if (numberOfCandles == 1) {
                                        candlePos2 = worldPosition.offset(l * 2, 1, k);
                                        candlePos2Slot = i;
                                    }
                                    if (numberOfCandles == 2) {
                                        candlePos3 = worldPosition.offset(l * 2, 1, k);
                                        candlePos3Slot = i;
                                    }
                                    numberOfCandles++;
                                }
                            }

                        }
                        if ((level.getBlockEntity(worldPosition.offset(l, 0, k * 2))) instanceof CandleTile candleTile && numberOfCandles < maxCandles) {

                            for (int i = 0; i < candleTile.getNumberOfCandles(); i++) {

                                if ((i == 0 && candleTile.candles.get(0).lit)
                                        || (i == 1 && candleTile.candles.get(1).lit)
                                        || (i == 2 && candleTile.candles.get(2).lit)
                                        || (i == 3 && candleTile.candles.get(3).lit)) {
                                    if (numberOfCandles == 0) {
                                        candlePos1 = worldPosition.offset(l, 0, k * 2);
                                        candlePos1Slot = i;
                                    }
                                    if (numberOfCandles == 1) {
                                        candlePos2 = worldPosition.offset(l, 0, k * 2);
                                        candlePos2Slot = i;
                                    }
                                    if (numberOfCandles == 2) {
                                        candlePos3 = worldPosition.offset(l, 0, k * 2);
                                        candlePos3Slot = i;
                                    }
                                    numberOfCandles++;
                                }
                            }
                        }
                        if ((level.getBlockEntity(worldPosition.offset(l, 1, k * 2))) instanceof CandleTile candleTile && numberOfCandles < maxCandles) {

                            for (int i = 0; i < candleTile.getNumberOfCandles(); i++) {

                                if ((i == 0 && candleTile.candles.get(0).lit)
                                        || (i == 1 && candleTile.candles.get(1).lit)
                                        || (i == 2 && candleTile.candles.get(2).lit)
                                        || (i == 3 && candleTile.candles.get(3).lit)) {
                                    if (numberOfCandles == 0) {
                                        candlePos1 = worldPosition.offset(l, 1, k * 2);
                                        candlePos1Slot = i;
                                    }
                                    if (numberOfCandles == 1) {
                                        candlePos2 = worldPosition.offset(l, 1, k * 2);
                                        candlePos2Slot = i;
                                    }
                                    if (numberOfCandles == 2) {
                                        candlePos3 = worldPosition.offset(l, 1, k * 2);
                                        candlePos3Slot = i;
                                    }
                                    numberOfCandles++;
                                }
                            }
                        }

                    }
                }
            }
        }

        degreesSpunCandles = moveToAngle(degreesSpunCandles, degreesSpunCandles + 1, 0.025f);

        if (numberOfCandles >= 1 && level.getBlockEntity(candlePos1) instanceof CandleTile candle) {

            CandleData candleData = candle.candles.get(candlePos1Slot);
            candleData.setNotReturn((int)this.tickCount);
            candleData.xTarget = (worldPosition.getX() - candlePos1.getX() + (float) Math.sin(degreesSpunCandles) * 1.25f);
            candleData.yTarget = (worldPosition.getY() - candlePos1.getY() + 1f + (float) Math.sin(this.tickCount / 20f) / 10);
            candleData.zTarget = (worldPosition.getZ() - candlePos1.getZ() + (float) Math.cos(degreesSpunCandles) * 1.25f);
        }
        if (numberOfCandles >= 2 && level.getBlockEntity(candlePos2) instanceof CandleTile candle) {

            CandleData candleData = candle.candles.get(candlePos2Slot);
            candleData.setNotReturn((int)this.tickCount);
            candleData.xTarget = (worldPosition.getX() - candlePos2.getX() + (float) Math.sin(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI * 2f / 3f)) * 1.25f);
            candleData.yTarget = (worldPosition.getY() - candlePos2.getY() + 1f + (float) Math.sin((this.tickCount + 10) / 20f) / 10);
            candleData.zTarget = (worldPosition.getZ() - candlePos2.getZ() + (float) Math.cos(degreesSpunCandles + (numberOfCandles == 2 ? Math.PI : Math.PI * 2f / 3f)) * 1.25f);
        }
        if (numberOfCandles >= 3 && level.getBlockEntity(candlePos3) instanceof CandleTile candle) {

            CandleData candleData = candle.candles.get(candlePos3Slot);
            candleData.setNotReturn((int)this.tickCount);
            candleData.xTarget = (worldPosition.getX() - candlePos3.getX() + (float) Math.sin(degreesSpunCandles + Math.PI * 2f / 3f * 2f) * 1.25f);
            candleData.yTarget = (worldPosition.getY() - candlePos3.getY() + 1f + (float) Math.sin((this.tickCount + 20) / 20f) / 10);
            candleData.zTarget = (worldPosition.getZ() - candlePos3.getZ() + (float) Math.cos(degreesSpunCandles + Math.PI * 2f / 3f * 2f) * 1.25f);
        }


        closestDist = maxDist;
        Item item = this.itemHandler.getStackInSlot(0).getItem();
        if (item instanceof HexereiBookItem) {
            Player playerEntity = this.level.getNearestPlayer(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), maxDist, false);
            if (playerEntity != null) {
                double dist = (getDistanceToEntity(playerEntity, this.worldPosition));
                if (dist < maxDist) {
                    if (dist < this.closestDist) {
                        this.closestDist = dist;
                        this.closestPlayerPos = playerEntity.position();
                        this.closestPlayer = playerEntity;
                    }
                }
            }


            if (slotClicked != -1)
                this.slotClickedTick++;

            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = stack.get(ModDataComponents.BOOK);
            this.pageOneRotationLast = this.pageOneRotation;
            this.pageTwoRotationLast = this.pageTwoRotation;
            if (bookData != null && bookData.isOpened()) {


                this.buttonScale = moveTo(this.buttonScale, this.buttonScaleTo, this.buttonScaleSpeed);
                this.buttonScaleRender = this.buttonScale;

                if (this.slotClicked != -1 && this.slotClickedTick > 5)
                    this.bookmarkSelectorScale = moveTo(this.bookmarkSelectorScale, 1, 0.15f * (this.bookmarkSelectorScale + 0.25f));
                else
                    this.bookmarkSelectorScale = 0;

                if (this.closestPlayerPos != null) {
                    if (this.degreesFlopped == 0) {
                        this.degreesSpunTo = 270 - getAngle(this.closestPlayerPos);
                        this.degreesSpunSpeed = 2.22f;
                        this.degreesSpun = moveToAngle(this.degreesSpun, this.degreesSpunTo, this.degreesSpunSpeed);
                    }
                    this.degreesFloppedTo = 0;
                    this.degreesFloppedSpeed = 3f + 6 * (Math.abs(degreesFlopped - 60)) / 90;
                    this.degreesFlopped = moveTo(this.degreesFlopped, this.degreesFloppedTo, this.degreesFloppedSpeed);
                } else {
                    if (this.degreesOpened == 90) {
                        this.degreesFloppedTo = 90;
                        this.degreesFloppedSpeed = 2f + 4 * (45 - Math.abs(45 - degreesFlopped)) / 90;
                        this.degreesFlopped = moveTo(this.degreesFlopped, this.degreesFloppedTo, this.degreesFloppedSpeed);
                    }
                }


                if (this.degreesFlopped == 0) {
                    this.degreesOpenedTo = Mth.clamp((float) (Math.max(0, this.closestDist - 1) * (360 / (maxDist - 1))) / 4, 4.0f, 90f);
                    this.degreesOpenedSpeed = 2f + 5 * (45 - Math.abs(45 - degreesOpened)) / 90;
                } else {
                    this.degreesOpenedTo = 90;
                    this.degreesOpenedSpeed = 2f + 6 * (45 - Math.abs(45 - degreesOpened)) / 90;
                }
                this.degreesOpened = moveTo(this.degreesOpened, this.degreesOpenedTo, this.degreesOpenedSpeed);

                if (this.turnPage == 1) {

                    if (this.pageOneRotation == 180) {
                        clickedNext(this, 1);
                        this.pageOneRotationRender = 0;
                        this.pageOneRotation = 0;
                        this.pageOneRotationTo = 0;
//                        this.pageOneRotationSpeed = 0;
                        this.turnPage = 0;
                        this.pageOneRotationLast = this.pageOneRotation;
//                        setChanged();
                    } else {
                        if (this.pageOneRotation == 0 && !level.isClientSide)
                            level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_TURN_PAGE_SLOW.get(), SoundSource.BLOCKS, (level.random.nextFloat() * 0.25f + 0.5f), (level.random.nextFloat() * 0.25f + 0.75f));

                        float f = (float) Math.sin(this.pageOneRotation / 180 * Math.PI);
                        this.pageOneRotationSpeed = (f * f * 35) + 10f;
                        this.pageOneRotationTo = (float) 180;
                        this.pageOneRotation = moveTo(this.pageOneRotation, this.pageOneRotationTo, this.pageOneRotationSpeed);
                    }
                }
                if (pageOneRotationTo == 0)
                    this.pageOneRotation = moveTo(this.pageOneRotation, this.pageOneRotationTo, this.pageOneRotationSpeed);
                if (this.turnPage == 2) {
                    if (this.pageTwoRotation == 180) {
                        clickedBack(this, 1);
                        this.pageTwoRotationRender = 0;
                        this.pageTwoRotation = 0;
                        this.pageTwoRotationTo = 0;
                        this.pageTwoRotationLast = this.pageTwoRotation;
//                        this.pageTwoRotationSpeed = 0;
                        this.turnPage = 0;
//                        setChanged();
                    } else {

                        if (this.pageTwoRotation == 0 && !level.isClientSide)
                            level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_TURN_PAGE_SLOW.get(), SoundSource.BLOCKS, (level.random.nextFloat() * 0.25f + 0.5f), (level.random.nextFloat() * 0.25f + 0.75f));

                        float f = (float) Math.sin(this.pageTwoRotation / 180 * Math.PI);
                        this.pageTwoRotationSpeed = (f * f * 35) + 10f;
                        this.pageTwoRotationTo = (float) 180;
                        this.pageTwoRotation = moveTo(this.pageTwoRotation, this.pageTwoRotationTo, this.pageTwoRotationSpeed);
                    }
                }
                if (pageTwoRotationTo == 0)
                    this.pageTwoRotation = moveTo(this.pageTwoRotation, this.pageTwoRotationTo, this.pageTwoRotationSpeed);
                if (this.turnPage == -1) {

                    BookEntries bookEntries = BookManager.getBookEntries();
                    int chapter = bookData.getChapter();
                    int page = bookData.getPage();
                    int pageOnNum = bookEntries.chapterList.get(chapter).startPage + page;
                    if (this.turnToChapter >= bookEntries.chapterList.size())
                        this.turnToChapter = bookEntries.chapterList.size() - 1;
                    if (this.turnToPage >= bookEntries.chapterList.get(this.turnToChapter).pages.size())
                        this.turnToPage = bookEntries.chapterList.get(this.turnToChapter).pages.size() - 1;
                    int destPageNum = bookEntries.chapterList.get(this.turnToChapter).startPage + this.turnToPage;
                    int numPagesToDest = Math.abs(destPageNum - pageOnNum);
                    if (page % 2 == 1)
                        page--;

                    int pagesToTurn = numPagesToDest > 90 ? 13 : numPagesToDest > 75 ? 11 : numPagesToDest > 60 ? 9 : numPagesToDest > 45 ? 7 : numPagesToDest > 30 ? 5 : numPagesToDest > 15 ? 3 : 1;

                    if (chapter > this.turnToChapter || (chapter == this.turnToChapter && page > this.turnToPage)) {

                        if (this.pageTwoRotation == 180) {
                            clickedBack(this, pagesToTurn);
                            this.pageTwoRotation = 0;
                            this.pageTwoRotationRender = 0;
                            this.pageTwoRotationLast = this.pageTwoRotation;
                            this.pageTwoRotationTo = 0;
                            this.pageTwoRotationSpeed = 0.01f;
                        } else {
                            if (this.pageTwoRotation == 0 && !level.isClientSide && numPagesToDest > 1) {
                                level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_TURN_PAGE_FAST.get(), SoundSource.BLOCKS, (level.random.nextFloat() * 0.25f + 0.5f), (level.random.nextFloat() * 0.3f + 0.7f));
                            }


                            float f = (1 + Math.min(numPagesToDest, 50) / 200f);
                            this.pageTwoRotationSpeed = 65 * f * f + 15;
                            this.pageTwoRotationTo = (float) 180;
                            this.pageTwoRotation = moveTo(this.pageTwoRotation, this.pageTwoRotationTo, this.pageTwoRotationSpeed);
                        }
                    }


                    if (chapter < this.turnToChapter || (chapter == this.turnToChapter && page < this.turnToPage)) {


                        if (this.pageOneRotation == 180) {
                            clickedNext(this, pagesToTurn);
                            this.pageOneRotation = 0;
                            this.pageOneRotationRender = 0;
                            this.pageOneRotationTo = 0;
                            this.pageOneRotationLast = this.pageOneRotation;
                            this.pageOneRotationSpeed = 0.01f;
                        } else {
                            if (this.pageOneRotation == 0 && !level.isClientSide && numPagesToDest > 0) {
                                level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_TURN_PAGE_FAST.get(), SoundSource.BLOCKS, (level.random.nextFloat() * 0.25f + 0.5f), (level.random.nextFloat() * 0.3f + 0.7f));
                            }
                            float f = (1 + Math.min(numPagesToDest, 50) / 200f);
                            this.pageOneRotationSpeed = 65 * f * f + 15;
                            this.pageOneRotationTo = (float) 180;
                            this.pageOneRotation = moveTo(this.pageOneRotation, this.pageOneRotationTo, this.pageOneRotationSpeed);
                        }
                    }


                    if (chapter == this.turnToChapter && (page == this.turnToPage || page + 1 == turnToPage)) {
                        this.turnPage = 0;
                        this.pageTwoRotation = 0;
                        this.pageTwoRotationTo = 0;
                        this.pageTwoRotationRender = 0;
                        this.pageTwoRotationSpeed = 0.01f;
                        this.pageOneRotation = 0;
                        this.pageOneRotationTo = 0;
                        this.pageOneRotationRender = 0;
                        this.pageOneRotationSpeed = 0.01f;
                        this.pageTwoRotationLast = this.pageTwoRotation;
                        this.pageOneRotationLast = this.pageOneRotation;
                    }

                }


            } else {
                this.degreesOpenedTo = 90;
                this.degreesOpenedSpeed = 2f + 6 * (Math.abs(45 - degreesOpened)) / 90;
                this.degreesOpened = moveTo(this.degreesOpened, this.degreesOpenedTo, this.degreesOpenedSpeed);
                if (this.degreesOpened == 90) {
                    this.degreesFloppedTo = 90;
                    this.degreesFloppedSpeed = 2f + 7 * (45 - Math.abs(45 - degreesFlopped)) / 90;
                    this.degreesFlopped = moveTo(this.degreesFlopped, this.degreesFloppedTo, this.degreesFloppedSpeed);
                }
            }
        } else {
            this.degreesFlopped = 90;
            this.degreesFloppedRender = 90;
            this.degreesOpened = 90; // reversed because the model is made so the book is opened from the start so offsetting 90 degrees from the start will close the book
            this.degreesOpenedRender = 90;
            this.degreesSpun = 0;
            this.degreesSpunRender = 0;
            this.degreesSpunTo = 0;
            this.pageOneRotation = 0;
            this.pageOneRotationTo = 0;
            this.pageOneRotationRender = 0;
            this.pageTwoRotation = 0;
            this.pageTwoRotationTo = 0;
            this.pageTwoRotationRender = 0;
        }


    }

    public void clickedNext(BookOfShadowsAltarTile altarTile, int pages) {
        ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
        BookData bookData = stack.get(ModDataComponents.BOOK);

        if (bookData != null && BookManager.getBookEntries() != null) {
            for (int i = 0; i < pages; i++) {
                int currentPage = bookData.getPage();
                int currentChapter = bookData.getChapter();
                if (currentPage < BookManager.getBookEntries().chapterList.get(currentChapter).pages.size() - (2)) {
                    bookData.setPage(currentPage + (2));
                    if (currentChapter < BookManager.getBookEntries().chapterList.size() - 1 && currentPage + (2) > BookManager.getBookEntries().chapterList.get(currentChapter).pages.size() - 1) {
                        bookData.setChapter(++currentChapter);
                        bookData.setPage(BookManager.getBookEntries().chapterList.get(currentChapter).pages.size() - 1);
                    }
                } else {
                    if (currentChapter < BookManager.getBookEntries().chapterList.size() - 1) {
                        bookData.setChapter(++currentChapter);
                        bookData.setPage(0);
                    } else {
                        bookData.setPage(BookManager.getBookEntries().chapterList.get(currentChapter).pages.size() - 1);
                    }
                }
            }
        }

        stack.set(ModDataComponents.BOOK, bookData);
        this.itemHandler.setStackInSlot(0, stack);
    }

    public void clickedBack(BookOfShadowsAltarTile altarTile, int pages) {
        ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
        BookData bookData = stack.get(ModDataComponents.BOOK);

        if (bookData != null && BookManager.getBookEntries() != null) {
            for (int i = 0; i < pages; i++) {

                int currentPage = bookData.getPage();
                int currentChapter = bookData.getChapter();
                if (currentPage > 0) {

                    if (currentChapter > 0 && currentPage - (2) < 0) {
                        bookData.setChapter(--currentChapter);
                        bookData.setPage(BookManager.getBookEntries().chapterList.get(currentChapter).pages.size() - 1);
                    } else {
                        bookData.setPage(Math.max(currentPage - (2), 0));
                    }

                } else {
                    if (currentChapter > 0) {
                        bookData.setChapter(--currentChapter);
                        bookData.setPage(BookManager.getBookEntries().chapterList.get(currentChapter).pages.size() - 1);
                    } else {
                        bookData.setPage(0);
                    }
                }
            }
        }

        stack.set(ModDataComponents.BOOK, bookData);
        this.itemHandler.setStackInSlot(0, stack);
    }

    public void setTurnPage(int turnPage, int chapter, int page) {

        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new BookTurnPageToServer(this, turnPage, chapter, page));
        else {
            setChanged();
        }

        this.turnToChapter = chapter;
        this.turnToPage = page;

        boolean flag = false;
        if (turnPage == -2) {
            turnPage += 2;
            flag = true;
        }

        if (flag) {

            level.playSound(null, this.worldPosition.above(), ModSounds.BOOK_CLOSE.get(), SoundSource.BLOCKS, 1f, (level.random.nextFloat() * 0.25f + 0.75f));

            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = stack.get(ModDataComponents.BOOK);

            if (bookData != null && bookData.isOpened())
                bookData.setOpened(false);

            stack.set(ModDataComponents.BOOK, bookData);
            this.itemHandler.setStackInSlot(0, stack);

        }

        this.turnPage = turnPage;

    }

    public void setTurnPage(int turnPage) {

        setTurnPage(turnPage, -1, -1);

    }

    public void clickPageBookmark(int chapter, int page) {

        if (level == null) return;

        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new BookBookmarkPageToServer(this, chapter, page));
        else {

            level.playSound(null, this.worldPosition.above(), ModSounds.BOOKMARK_BUTTON.get(), SoundSource.BLOCKS, 0.75f, (level.random.nextFloat() * 0.25f + 0.75f));
            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = stack.get(ModDataComponents.BOOK);
            if (bookData != null) {
                List<BookData.Bookmarks.Slot> slots = bookData.getBookmarks().getSlots();
                boolean flag = false;
                BookData.Bookmarks.Slot firstEmpty = null;
                for (BookData.Bookmarks.Slot slot : slots) {
                    if (!slot.getId().isEmpty()) {
                        if (BookManager.getBookEntries().chapterList.get(chapter).pages.get(page).location.equals(slot.getId())) {
                            slot.setColor(DyeColor.byId(slot.getColor().getId() + 1 >= DyeColor.values().length ? 0 : slot.getColor().getId() + 1));
                            flag = true;
                            break;
                        }

                    } else if (firstEmpty == null) {
                        firstEmpty = slot;
                    }
                }
                if (!flag && firstEmpty != null) {
                    firstEmpty.setId(BookManager.getBookEntries().chapterList.get(chapter).pages.get(page).location);
                    firstEmpty.setColor(DyeColor.values()[new Random().nextInt(DyeColor.values().length)]);
                }

                stack.set(ModDataComponents.BOOK, bookData);
                this.itemHandler.setStackInSlot(0, stack);
            }

            setChanged();
        }


    }

    public void swapBookmarks(int slot1, int slot2) {

        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new BookBookmarkSwapToServer(this, slot1, slot2));
        else {
            level.playSound(null, this.worldPosition.above(), ModSounds.BOOKMARK_SWAP.get(), SoundSource.BLOCKS, 0.75f, (level.random.nextFloat() * 0.25f + 0.75f));
            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = stack.get(ModDataComponents.BOOK);
            if (bookData != null) {
                List<BookData.Bookmarks.Slot> slots = bookData.getBookmarks().getSlots();

                BookData.Bookmarks.Slot temp = slots.get(slot1).copyWithIndex(slot2);
                slots.set(slot1, slots.get(slot2).copyWithIndex(slot1));
                slots.set(slot2, temp);

                stack.set(ModDataComponents.BOOK, bookData);
                this.itemHandler.setStackInSlot(0, stack);
            }
            setChanged();
        }

    }

    public void deleteBookmark(int slot1) {

        if (level.isClientSide)
            HexereiPacketHandler.sendToServer(new BookBookmarkDeleteToServer(this, slot1));
        else {
            level.playSound(null, this.worldPosition.above(), ModSounds.BOOKMARK_DELETE.get(), SoundSource.BLOCKS, 1f, (level.random.nextFloat() * 0.25f + 0.75f));
            ItemStack stack = this.itemHandler.getStackInSlot(0).copy();
            BookData bookData = stack.get(ModDataComponents.BOOK);
            if (bookData != null) {
                List<BookData.Bookmarks.Slot> slots = bookData.getBookmarks().getSlots();

                slots.set(slot1, new BookData.Bookmarks.Slot("", null, slot1));

                stack.set(ModDataComponents.BOOK, bookData);
                this.itemHandler.setStackInSlot(0, stack);
            }
            setChanged();
        }


    }

    @Override
    public int getContainerSize() {
        return 0;
    }

}
