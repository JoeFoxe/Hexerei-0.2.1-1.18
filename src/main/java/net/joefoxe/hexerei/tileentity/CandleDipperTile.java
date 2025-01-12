package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.recipes.DipperRecipe;
import net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.EmitParticlesPacket;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.joefoxe.hexerei.util.HexereiUtil.moveTo;

public class CandleDipperTile extends RandomizableContainerBlockEntity implements WorldlyContainer, Clearable, MenuProvider {

    protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

    public float numberOfCandles;
    public static int DRYING_START_TICKS = 60;

    public List<DipperSlot> dipperSlots = new ArrayList<>(3);

    public static class DipperSlot {
        public int index;
        public Vec3 pos;
        public Vec3 posLast;
        public DipperState state;
        public int dippingTicks;
        public int dippingTicksMax;
        public int dryingTicks;
        public int dryingTicksMax;
        public int timesDipped;
        public int timesDippedMax;
        public int fluidConsumptionAmount;
        public ItemStack output;

        public DipperSlot(int index, Vec3 pos, DipperState state, int dippingTicksMax, int dryingTicksMax, int timesDippedMax, int fluidConsumptionAmount, ItemStack output) {
            this.index = index;
            this.pos = pos;
            this.posLast = pos;
            this.state = state;
            this.dippingTicks = dippingTicksMax;
            this.dippingTicksMax = dippingTicksMax;
            this.dryingTicks = dryingTicksMax;
            this.dryingTicksMax = dryingTicksMax;
            this.timesDipped = 0;
            this.timesDippedMax = timesDippedMax;
            this.fluidConsumptionAmount = fluidConsumptionAmount;
            this.output = output;
        }

        public boolean isCrafting() {
            return this.state != DipperState.NON && this.state != DipperState.FINISHED;
        }

        public boolean isDrying() {
            return this.state == DipperState.DRYING;
        }

        public boolean isDunking() {
            return this.state == DipperState.DUNKING;
        }

        public boolean isFinished() {
            return this.state == DipperState.FINISHED;
        }

        public boolean isNon() {
            return this.state == DipperState.NON;
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("state", this.state.ordinal());
            tag.putInt("dippingTicks", this.dippingTicks);
            tag.putInt("dippingTicksMax", this.dippingTicksMax);
            tag.putInt("dryingTicks", this.dryingTicks);
            tag.putInt("dryingTicksMax", this.dryingTicksMax);
            tag.putInt("timesDipped", this.timesDipped);
            tag.putInt("timesDippedMax", this.timesDippedMax);
            tag.putInt("fluidConsumptionAmount", this.fluidConsumptionAmount);
            tag.put("output", this.output.save(new CompoundTag()));

            return tag;
        }

        public void load(CompoundTag tag) {
            this.state = DipperState.byId(tag.getInt("state"));
            this.dippingTicks = tag.getInt("dippingTicks");
            this.dippingTicksMax = tag.getInt("dippingTicksMax");
            this.dryingTicks = tag.getInt("dryingTicks");
            this.dryingTicksMax = tag.getInt("dryingTicksMax");
            this.timesDipped = tag.getInt("timesDipped");
            this.timesDippedMax = tag.getInt("timesDippedMax");
            this.fluidConsumptionAmount = tag.getInt("fluidConsumptionAmount");
            this.output = ItemStack.of(tag.getCompound("output"));
        }
    }

    public enum DipperState {
        DRYING, DUNKING, FINISHED, NON;


        public static DipperState byId(int id) {
            DipperState[] type = values();
            return type[id < 0 || id >= type.length ? 0 : id];
        }
    }

    public CandleDipperTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);

        dipperSlots.add(new DipperSlot(
                0,
                new Vec3(0.5f, 0.4f, 0.5f),
                DipperState.NON,
                200,
                60,
                3,
                100,
                ItemStack.EMPTY));
        dipperSlots.add(new DipperSlot(
                1,
                new Vec3(0.5f, 0.4f, 0.5f),
                DipperState.NON,
                200,
                60,
                3,
                100,
                ItemStack.EMPTY));
        dipperSlots.add(new DipperSlot(
                2,
                new Vec3(0.5f, 0.4f, 0.5f),
                DipperState.NON,
                200,
                60,
                3,
                100,
                ItemStack.EMPTY));
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
            if (!level.isClientSide)
                HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new TESyncPacket(worldPosition, save(new CompoundTag())));

            if (this.level != null)
                this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition),
                        Block.UPDATE_CLIENTS);
        }
    }

    LazyOptional<? extends IItemHandler>[] handlers =
            SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
            if (facing == Direction.UP)
                return handlers[0].cast();
            else if (facing == Direction.DOWN)
                return handlers[1].cast();
            else
                return handlers[2].cast();
        }

        return super.getCapability(capability, facing);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {

        return super.getCapability(cap);
    }


    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
    }

    @Override
    public CompoundTag serializeNBT() {
        return super.serializeNBT();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }


    public CandleDipperTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.CANDLE_DIPPER_TILE.get(), blockPos, blockState);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= 0 && index < this.items.size()) {
            ItemStack itemStack = stack.copy();
            itemStack.setCount(1);
            this.items.set(index, itemStack);
            dipperSlots.get(index).dryingTicks = DRYING_START_TICKS;
            level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F);
        }

        setChanged();
    }

    @Override
    public ItemStack removeItem(int index, int p_59614_) {
        this.unpackLootTable(null);
        ItemStack itemstack = ContainerHelper.removeItem(this.getItems(), index, p_59614_);
        if (!itemstack.isEmpty()) {

            dipperSlots.get(index).state = DipperState.NON;
            setChanged();
        }


        return itemstack;
    }

    public void craft() {
        SimpleContainer inv = new SimpleContainer(3);
        for (int i = 0; i < 3; i++)
            inv.setItem(i, this.items.get(i));

        BlockEntity blockEntity = level.getBlockEntity(this.worldPosition.below());
        AtomicBoolean[] matchesRecipe = new AtomicBoolean[3];
        for (int i = 0; i < matchesRecipe.length; i++)
            matchesRecipe[i] = new AtomicBoolean(false);

        if (blockEntity instanceof MixingCauldronTile mixingCauldronTile) {

            List<DipperRecipe> recipes = level.getRecipeManager().getRecipesFor(DipperRecipe.Type.INSTANCE, inv, level).stream().filter((dipperRecipe) -> {
                FluidStack tileFluid = mixingCauldronTile.getFluidStack();
                FluidStack recipeFluid = dipperRecipe.getLiquid();

                CompoundTag tag = tileFluid.hasTag() ? tileFluid.getOrCreateTag() : new CompoundTag();
                CompoundTag tag2 = recipeFluid.hasTag() ? recipeFluid.getOrCreateTag() : new CompoundTag();

                return NbtUtils.compareNbt(tag2, tag, true);
            }).toList();

            recipes.forEach((iRecipe -> {

                ItemStack output = iRecipe.getResultItem(this.level.registryAccess());
                ItemStack input = iRecipe.getIngredients().get(0).getItems()[0];
                boolean matchesFluid = iRecipe.getLiquid().getFluid().isSame(mixingCauldronTile.getFluidStack().getFluid()) && iRecipe.getFluidLevelsConsumed() <= mixingCauldronTile.getFluidStack().getAmount();
                boolean hasFluidTag = iRecipe.getLiquid().hasTag();
                if (hasFluidTag && !mixingCauldronTile.getFluidStack().isEmpty() && !mixingCauldronTile.getFluidStack().getOrCreateTag().equals(iRecipe.getLiquid().getOrCreateTag()))
                    matchesFluid = false;

                ResourceLocation fl2 = ForgeRegistries.FLUIDS.getKey(mixingCauldronTile.getFluidStack().getFluid());
                CompoundTag fluidTag = mixingCauldronTile.getFluidStack().isEmpty() ? new CompoundTag() : mixingCauldronTile.getFluidStack().copy().getOrCreateTag();

                ResourceLocation fl1 = ForgeRegistries.FLUIDS.getKey(iRecipe.getLiquid().getFluid());
                if (!matchesFluid && fl1 != null && fl2 != null && fl1.getPath().equals(fl2.getPath())) {
                    boolean flag = NbtUtils.compareNbt(iRecipe.getLiquid().copy().getOrCreateTag(), fluidTag, true);
                    if (flag) {
                        matchesFluid = true;
                    }
                }

                boolean useInputItemAsOutput = iRecipe.getUseInputItemAsOutput();
                CompoundTag tag = input.getOrCreateTag();


                for (int i = 0; i < matchesRecipe.length; i++) {
                    CompoundTag tag2 = this.items.get(i).getOrCreateTag();
                    boolean compare = NbtUtils.compareNbt(tag, tag2, true);
                    DipperSlot dipperSlot = dipperSlots.get(i);
                    if (input.getItem() == this.items.get(i).getItem() && compare && !matchesRecipe[i].get()) {

                        if (matchesFluid) {

                            matchesRecipe[i].set(true);

                            if (dipperSlot.isNon()) {
                                dipperSlot.state = DipperState.DRYING;

                                dipperSlot.output = output.copy();
                                if (useInputItemAsOutput) {
                                    ItemStack stack = this.items.get(i).copy();
                                    stack.getOrCreateTag().merge(output.getOrCreateTag());
                                    dipperSlot.output = stack;
                                }
                                dipperSlot.fluidConsumptionAmount = iRecipe.getFluidLevelsConsumed();
                                dipperSlot.timesDipped = 0;
                                dipperSlot.timesDippedMax = iRecipe.getNumberOfDips();
                                dipperSlot.dryingTicksMax = iRecipe.getDryingTime();
                                dipperSlot.dryingTicks = DRYING_START_TICKS;
                                dipperSlot.dippingTicksMax = iRecipe.getDippingTime();
                                dipperSlot.dippingTicks = dipperSlot.dippingTicksMax;
                                setChanged();
                            }
                        }

                    } else {
                        if (matchesFluid) {
                            if (dipperSlot.isCrafting()) {
                                dipperSlot.state = DipperState.NON;
                                setChanged();
                            }
                        }
                    }
                }


            }));

            for (int i = 0; i < matchesRecipe.length; i++) {
                if (!matchesRecipe[i].get()) {
                    if (dipperSlots.get(i).isCrafting()) {
                        dipperSlots.get(i).state = DipperState.NON;
                        dipperSlots.get(i).dryingTicks = DRYING_START_TICKS;
                        setChanged();
                    }
                }
            }


            for(DipperSlot slot : dipperSlots) {
                if (slot.isDrying() && slot.timesDipped < slot.timesDippedMax) {
                    slot.dryingTicks--;
                    if (slot.dryingTicks <= 0) {
                        slot.dryingTicks = slot.dryingTicksMax;
                        slot.state = DipperState.DUNKING;
                        setChanged();
                    }
                }
                else if (slot.isDunking()) {
                    if (mixingCauldronTile.getFluidStack().getAmount() > 0) {
                        slot.dippingTicks--;
                    }
                    if (slot.dippingTicks <= 0) {
                        slot.dippingTicks = slot.dippingTicksMax;
                        slot.state = DipperState.DRYING;
                        slot.dryingTicks = slot.dryingTicksMax;
                        slot.timesDipped++;
                        decreaseFluid(slot.fluidConsumptionAmount);

                        if (slot.timesDipped >= slot.timesDippedMax) {
                            slot.state = DipperState.FINISHED;
                            slot.timesDipped = 0;
                            slot.dippingTicks = slot.dippingTicksMax;
                            slot.dryingTicks = slot.dryingTicksMax;

                            level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);

                            this.items.set(slot.index, slot.output);
                        }
                        setChanged();
                    }
                }
            }

        }


    }


    @Override
    public void load(CompoundTag nbt) {
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(nbt))
            ContainerHelper.loadAllItems(nbt, this.items);

        if (nbt.contains("slot0"))
            dipperSlots.get(0).load(nbt.getCompound("slot0"));
        if (nbt.contains("slot1"))
            dipperSlots.get(1).load(nbt.getCompound("slot1"));
        if (nbt.contains("slot2"))
            dipperSlots.get(2).load(nbt.getCompound("slot2"));
        super.load(nbt);

    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container." + Hexerei.MOD_ID + ".dipper");
    }

    @Override
    protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
        return null;
    }

    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, this.items);

        compound.put("slot0", dipperSlots.get(0).save());
        compound.put("slot1", dipperSlots.get(1).save());
        compound.put("slot2", dipperSlots.get(2).save());
    }


    public CompoundTag save(CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, this.items);

        compound.put("slot0", dipperSlots.get(0).save());
        compound.put("slot1", dipperSlots.get(1).save());
        compound.put("slot2", dipperSlots.get(2).save());

        return compound;
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {

        return ClientboundBlockEntityDataPacket.create(this, (tag) -> this.getUpdateTag());
    }

    @Override
    public void onDataPacket(final Connection net, final ClientboundBlockEntityDataPacket pkt) {
        this.deserializeNBT(pkt.getTag());
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(5, 5, 5);
    }

    public float getAngle(Vec3 pos) {
        float angle = (float) Math.toDegrees(Math.atan2(pos.z() - this.getBlockPos().getZ() - 0.5f, pos.x() - this.getBlockPos().getX() - 0.5f));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    public float getSpeed(double pos, double posTo) {
        return (float) (0.0001f + 0.15f * (Math.abs(pos - posTo)));
    }

    public Vec3 rotateAroundVec(Vec3 vector3dCenter, float rotation, Vec3 vector3d) {
        Vec3 newVec = vector3d.subtract(vector3dCenter);
        newVec = newVec.yRot(rotation / 180f * (float) Math.PI);
        newVec = newVec.add(vector3dCenter);

        return newVec;
    }

    public int putItems(int slot, @Nonnull ItemStack stack) {
        if (this.items.get(slot).isEmpty()) {
            ItemStack stack1 = stack.copy();
            stack1.setCount(1);
            this.items.set(slot, stack1);
            setChanged();
            stack.shrink(1);
            return 1;
        }

        if (!ItemStack.isSameItemSameTags(stack, this.items.get(slot)))
            return 0;

        return 1;
    }

    public int interactDipper(Player player, BlockHitResult hit) {
        if (level == null) return 0;
        if (!player.isShiftKeyDown()) {
            if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                Random rand = new Random();

                for(int i = 0; i < 3; i++){
                    if (this.items.get(i).isEmpty()) {
                        putItems(i, player.getItemInHand(InteractionHand.MAIN_HAND));
                        dipperSlots.get(i).dryingTicks = DRYING_START_TICKS;
                        level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
                        return 1;
                    }
                }
            }

            for(int i = 0; i < 3; i++){
                DipperSlot dipperSlot = dipperSlots.get(i);
                if (dipperSlot.isFinished()) {
                    dipperSlot.timesDipped = 0;
                    dipperSlot.dippingTicks = dipperSlot.dippingTicksMax;
                    dipperSlot.state = DipperState.NON;
                    dipperSlot.dryingTicks = dipperSlot.dryingTicksMax;
                    player.inventory.placeItemBackInInventory(this.items.get(i).copy());
                    level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
                    this.items.set(i, ItemStack.EMPTY);
                    dipperSlot.output = ItemStack.EMPTY;
                }
            }
        } else {
            for(int i = 0; i < 3; i++){
                DipperSlot dipperSlot = dipperSlots.get(i);
                if (!this.items.get(i).isEmpty() && !dipperSlot.isCrafting()) {
                    dipperSlot.timesDipped = 0;
                    dipperSlot.dippingTicks = dipperSlot.dippingTicksMax;
                    dipperSlot.state = DipperState.NON;
                    dipperSlot.dryingTicks = dipperSlot.dryingTicksMax;
                    player.inventory.placeItemBackInInventory(this.items.get(i).copy());
                    level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 1.0F);
                    this.items.set(i, ItemStack.EMPTY);
                    dipperSlot.output = ItemStack.EMPTY;
                }
            }
        }

        return 0;
    }

    public void tick() {

        if (level instanceof ServerLevel)
            craft();

        for(DipperSlot slot : dipperSlots)
            slot.posLast = slot.pos;

        numberOfCandles = 0;

        Vec3[] targetPos = new Vec3[3];
        targetPos[0] = new Vec3(4f / 16f, 0f / 16f, 1f / 16f);
        targetPos[1] = new Vec3(8f / 16f, 0f / 16f, 1f / 16f);
        targetPos[2] = new Vec3(12f / 16f, 0f / 16f, 1f / 16f);


        if (level != null && level.getBlockEntity(this.worldPosition.below()) instanceof MixingCauldronTile cauldronTile) {
            float fillPercentage = 0;
            FluidStack fluidStack = cauldronTile.getFluidInTank(0);
            if (!fluidStack.isEmpty())
                fillPercentage = Math.min(1, (float) fluidStack.getAmount() / cauldronTile.getTankCapacity(0));
            float height = MixingCauldronRenderer.MIN_Y + (MixingCauldronRenderer.MAX_Y - MixingCauldronRenderer.MIN_Y) * fillPercentage - 1 + 1 / 16f;

            DipperSlot dipperSlot = dipperSlots.get(0);
            if (dipperSlot.isDrying() || !this.items.get(0).isEmpty())
                targetPos[0] = new Vec3(targetPos[0].x(), 5f / 16f + Math.sin((this.level.getGameTime()) / 16f) / 32f, 8f / 16f);
            if (dipperSlot.isDunking())
                targetPos[0] = new Vec3(targetPos[0].x(), height + Math.sin((this.level.getGameTime()) / 16f) / 32f, 8f / 16f);
            if (dipperSlot.isFinished())
                targetPos[0] = new Vec3(targetPos[0].x(), 10f / 16f + Math.sin((this.level.getGameTime()) / 16f) / 32f, 8f / 16f);

            dipperSlot = dipperSlots.get(1);
            if (dipperSlot.isDrying() || !this.items.get(1).isEmpty())
                targetPos[1] = new Vec3(targetPos[1].x(), 5f / 16f + Math.sin((this.level.getGameTime() + 20f) / 14f) / 32f, 8f / 16f);
            if (dipperSlot.isDunking())
                targetPos[1] = new Vec3(targetPos[1].x(), height + Math.sin((this.level.getGameTime() + 20f) / 14f) / 32f, 8f / 16f);
            if (dipperSlot.isFinished())
                targetPos[1] = new Vec3(targetPos[1].x(), 10f / 16f + Math.sin((this.level.getGameTime() + 20f) / 14f) / 32f, 8f / 16f);

            dipperSlot = dipperSlots.get(2);
            if (dipperSlot.isDrying() || !this.items.get(2).isEmpty())
                targetPos[2] = new Vec3(targetPos[2].x(), 5f / 16f + Math.sin((this.level.getGameTime() + 40f) / 15f) / 32f, 8f / 16f);
            if (dipperSlot.isDunking())
                targetPos[2] = new Vec3(targetPos[2].x(), height + Math.sin((this.level.getGameTime() + 40f) / 15f) / 32f, 8f / 16f);
            if (dipperSlot.isFinished())
                targetPos[2] = new Vec3(targetPos[2].x(), 10f / 16f + Math.sin((this.level.getGameTime() + 40f) / 15f) / 32f, 8f / 16f);

            Direction dir = this.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
            int rot = dir == Direction.NORTH ? 180 : dir == Direction.SOUTH ? 0 : dir == Direction.EAST ? 90 : 270;

            for(int i = 0; i < dipperSlots.size(); i++) {
                targetPos[i] = rotateAroundVec(new Vec3(0.5f, 0, 0.5f), rot, targetPos[i]);
                DipperSlot slot = dipperSlots.get(i);
                slot.pos = new Vec3(
                        moveTo((float) slot.pos.x, (float) targetPos[i].x(), getSpeed((float) slot.pos.x, targetPos[i].x())),
                        moveTo((float) slot.pos.y, (float) targetPos[i].y(), 0.75f * getSpeed((float) slot.pos.y, targetPos[i].y())),
                        moveTo((float) slot.pos.z, (float) targetPos[i].z(), getSpeed((float) slot.pos.z, targetPos[i].z())));
            }

        }



    }

    private void decreaseFluid(int amount) {
        if (level.getBlockEntity(this.worldPosition.below()) instanceof MixingCauldronTile cauldronTile && !level.isClientSide()) {
            cauldronTile.getFluidStack().shrink(amount);
            cauldronTile.setChanged();
            HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new EmitParticlesPacket(worldPosition.below(), 10, false));
        }
    }

    @Override
    public int[] getSlotsForFace(Direction p_19238_) {
        return new int[]{0, 1, 2};
    }

    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return this.canPlaceItem(index, itemStackIn);
    }

    public boolean canPlaceItem(int index, ItemStack stack) {
        return this.items.get(index).isEmpty();
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack p_19240_, Direction p_19241_) {
        return !dipperSlots.get(index).isCrafting();
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

}
