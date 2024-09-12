package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.container.MixingCauldronContainer;
import net.joefoxe.hexerei.data.recipes.CauldronEmptyingRecipe;
import net.joefoxe.hexerei.data.recipes.CauldronFillingRecipe;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.fluid.FluidIngredient;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.fluid.PotionFluid;
import net.joefoxe.hexerei.fluid.PotionFluidHandler;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.particle.CauldronParticleData;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.EmitParticlesPacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer.MAX_Y;
import static net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer.MIN_Y;

@SuppressWarnings("deprecation")
public class MixingCauldron extends BaseEntityBlock implements ITileEntity<MixingCauldronTile>, DyeableLeatherItem {

    //Moved to constant in case this is changed in the future.
    public static final int POTION_MB_AMOUNT = 250;

    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);
    public static final IntegerProperty CRAFT_DELAY = IntegerProperty.create("delay", 0, MixingCauldronTile.craftDelayMax);
    public static final BooleanProperty GUI_RENDER = BooleanProperty.create("gui_render");
    public static final BooleanProperty DYED = BooleanProperty.create("dyed");
    public int emitParticles;

    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(CRAFT_DELAY, 0).setValue(DYED, false).setValue(GUI_RENDER, false);
    }

//    public BlockState rotate(BlockState pState, Rotation pRot) {
//        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
//    }

    // hitbox
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(0, 1, 0, 16, 16, 2),
            Block.box(0, 1, 14, 16, 16, 16),
            Block.box(0, 1, 2, 2, 16, 14),
            Block.box(14, 1, 2, 16, 16, 14),
            Block.box(-1, 3, -1, 17, 14, 2),
            Block.box(-1, 3, 2, 2, 14, 14),
            Block.box(0, 0, 13, 3, 1, 16),
            Block.box(0, 0, 0, 3, 1, 3),
            Block.box(13, 0, 0, 16, 1, 3),
            Block.box(13, 0, 13, 16, 1, 16),
            Block.box(2, 3, 2, 14, 4, 14),
            Block.box(14, 3, 2, 17, 14, 14),
            Block.box(-1, 3, 14, 17, 14, 17)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();


    protected boolean canReceiveStalactiteDrip(Fluid pFluid) {
        return true;
    }

    protected void receiveStalactiteDrip(BlockState pState, Level pLevel, BlockPos pPos, Fluid pFluid) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof MixingCauldronTile mixingCauldron) {
            if (mixingCauldron.getFluidStack().isEmpty()) {
                mixingCauldron.fill(new FluidStack(pFluid, 100), IFluidHandler.FluidAction.EXECUTE);
            } else if (mixingCauldron.getFluidStack().getFluid() == pFluid) {
                if (mixingCauldron.getFluidStack().getAmount() < 2000) {
                    mixingCauldron.getFluidStack().grow(100);
                    if (mixingCauldron.getFluidStack().getAmount() > 2000) {
                        mixingCauldron.getFluidStack().setAmount(2000);
                    }
                }
            }
            mixingCauldron.sync();
        }

    }

    public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return SHAPE;
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
        //prevent older checks modifying newer ones, though it probably shouldn't?
        ItemStack stack = player.getItemInHand(hand).copy();
        if (!(world.getBlockEntity(pos) instanceof MixingCauldronTile cauldronTile)) {
            return InteractionResult.FAIL;
        }

        //Crow Flute
        if (stack.is(ModItems.CROW_FLUTE.get()) && stack.getOrCreateTag().getInt("commandMode") == 2) {
            stack.useOn(new UseOnContext(player, hand, rayTraceResult));
            return InteractionResult.SUCCESS;
        }

        //Blood sigil
        if (stack.is(HexereiTags.Items.SIGILS)) {

            if (cauldronTile.getItemStackInSlot(9).isEmpty()) {
                stack.setCount(1);
                cauldronTile.setItem(9, stack);
                player.getItemInHand(hand).shrink(1);
                cauldronTile.setChanged();
                return InteractionResult.SUCCESS;
            } else if (!cauldronTile.getItemStackInSlot(9).is(stack.getItem())) {
                player.inventory.placeItemBackInInventory(cauldronTile.getItemStackInSlot(9));
                stack.setCount(1);
                cauldronTile.setItem(9, stack);
                player.getItemInHand(hand).shrink(1);
                cauldronTile.setChanged();
                return InteractionResult.SUCCESS;
            }

        }

        FluidStack cauldronFluid = cauldronTile.getFluidStack();

        //Filling from recipe
        Optional<CauldronFillingRecipe> fillingOptional = world.getRecipeManager().getRecipeFor(ModRecipeTypes.CAULDRON_FILLING_TYPE.get(), new SimpleContainer(stack), world);
        if(fillingOptional.isPresent()) {
            CauldronFillingRecipe recipe = fillingOptional.get();
            ItemStack output = recipe.getResultItem(world.registryAccess());
            FluidStack fluidOut = recipe.getResultingFluid();
            if(cauldronFluid.getFluid().isSame(Fluids.EMPTY) || (fluidOut.getFluid().isSame(cauldronFluid.getFluid()) && cauldronFluid.getAmount() + fluidOut.getAmount() <= cauldronTile.getTankCapacity(0))) {
                return fillFromItem(cauldronTile, world, player, hand, player.getItemInHand(hand), output, fluidOut);
            }
        }

        //Emptying from recipe
        Optional<CauldronEmptyingRecipe> emptyingOptional = world.getRecipeManager().getRecipeFor(ModRecipeTypes.CAULDRON_EMPTYING_TYPE.get(), new CauldronEmptyingRecipe.Wrapper(stack, cauldronFluid), world);
        if(emptyingOptional.isPresent()) {
            CauldronEmptyingRecipe recipe = emptyingOptional.get();
            ItemStack output = recipe.getResultItem(world.registryAccess());
            FluidIngredient fluidIn = recipe.getFluid();
            if(cauldronFluid.getAmount() - fluidIn.getRequiredAmount() >= 0) {
                return emptyToItem(cauldronTile, world, player, hand, player.getItemInHand(hand), new FluidStack(cauldronFluid.getFluid(), fluidIn.getRequiredAmount()), output);
            }
        }

        //Filling from potion
        if (cauldronFluid.getFluid() instanceof PotionFluid && stack.getItem() == Items.GLASS_BOTTLE) {
            ItemStack potionOut = PotionFluidHandler.fillBottle(cauldronFluid);
//                        ItemStack potionOut = PotionUtils.setPotion(new ItemStack(Items.POTION), PotionFluidHandler.getPotionFromFluidStack(cauldronFluid));

            shrinkItem(player, hand, player.getItemInHand(hand), potionOut);
            cauldronFluid.shrink(POTION_MB_AMOUNT);
            cauldronTile.setChanged();

            //Effects
            Random random = new Random();
            if (!world.isClientSide) {
                HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(cauldronTile.getPos())), new EmitParticlesPacket(cauldronTile.getPos(), 3, false));
            }

            world.playSound(null, cauldronTile.getPos().getX() + 0.5f, cauldronTile.getPos().getY() + 0.5f, cauldronTile.getPos().getZ() + 0.5f, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * random.nextFloat());
            return InteractionResult.SUCCESS;
        }
        //Emptying from potion
        else if (stack.getItem() == Items.POTION || stack.getItem() == Items.LINGERING_POTION || stack.getItem() == Items.SPLASH_POTION) {
            if ((cauldronFluid.isFluidEqual(PotionFluidHandler.getFluidFromPotionItem(stack)) && cauldronFluid.getAmount() + POTION_MB_AMOUNT <= cauldronTile.getTankCapacity(0)) || cauldronFluid.isEmpty()) {
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                player.awardStat(Stats.USE_CAULDRON);
                shrinkItem(player, hand, player.getItemInHand(hand), bottle);
                if (cauldronFluid.isEmpty()) {
                    cauldronTile.fill(new FluidStack(PotionFluidHandler.getFluidFromPotionItem(stack), POTION_MB_AMOUNT), IFluidHandler.FluidAction.EXECUTE);
                }
                else {
                    cauldronFluid.grow(POTION_MB_AMOUNT);
                }
                cauldronTile.setChanged();

                //Effects
                Random random = new Random();
                if (!world.isClientSide) {
                    HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(cauldronTile.getPos())), new EmitParticlesPacket(cauldronTile.getPos(), 3, false));
                }
                world.playSound(null, cauldronTile.getPos().getX() + 0.5f, cauldronTile.getPos().getY() + 0.5f, cauldronTile.getPos().getZ() + 0.5f, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * random.nextFloat());
                return InteractionResult.SUCCESS;
            }
        }

        //Item fluid tanks (buckets)
        ItemStack fillStack = stack.copy();
        fillStack.setCount(1);
        LazyOptional<IFluidHandlerItem> fluidHandlerOptional = fillStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (fluidHandlerOptional.isPresent()) {
            IFluidHandlerItem fluidHandler = fluidHandlerOptional.resolve().get();

            if (cauldronTile.interactWithFluid(fluidHandler)) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {

                        player.setItemInHand(hand, fluidHandler.getContainer());
                    } else {
                        player.setItemInHand(hand, stack);
                        if (!player.getInventory().add(fluidHandler.getContainer()))
                            player.drop(fluidHandler.getContainer(), false);
                    }
                }
                if (!world.isClientSide)
                    HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(cauldronTile.getPos())), new EmitParticlesPacket(cauldronTile.getPos(), 3, false));


                return InteractionResult.sidedSuccess(world.isClientSide);
            }
            return InteractionResult.SUCCESS;
        }


        if (!world.isClientSide()) {

            MenuProvider containerProvider = createContainerProvider(world, pos);

            NetworkHooks.openScreen(((ServerPlayer) player), containerProvider, cauldronTile.getBlockPos());

        }

        return InteractionResult.SUCCESS;
    }

    private InteractionResult fillFromItem(MixingCauldronTile mixingCauldron, Level level, Player player, InteractionHand hand, ItemStack stackIn, ItemStack stackOut, FluidStack fluid) {
        player.awardStat(Stats.USE_CAULDRON);
        shrinkItem(player, hand, stackIn, stackOut);
        if(mixingCauldron.getFluidStack().isEmpty()) {
            mixingCauldron.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
        }
        else {
            mixingCauldron.getFluidStack().grow(fluid.getAmount());
        }
        mixingCauldron.normalizeTank();

        //Effects
        Random random = new Random();
        if (!level.isClientSide) {
            HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(mixingCauldron.getPos())), new EmitParticlesPacket(mixingCauldron.getPos(), 3, false));
        }
        level.playSound(null, mixingCauldron.getPos().getX() + 0.5f, mixingCauldron.getPos().getY() + 0.5f, mixingCauldron.getPos().getZ() + 0.5f, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * random.nextFloat());
        mixingCauldron.setChanged();
        return InteractionResult.SUCCESS;
    }

    private InteractionResult emptyToItem(MixingCauldronTile mixingCauldron, Level level, Player player, InteractionHand hand, ItemStack stackIn, FluidStack fluid, ItemStack stackOut) {
        player.awardStat(Stats.USE_CAULDRON);
        shrinkItem(player, hand, stackIn, stackOut);
        mixingCauldron.getFluidStack().shrink(fluid.getAmount());
        mixingCauldron.normalizeTank();
        mixingCauldron.setChanged();

        //Effects
        Random random = new Random();
        if (!level.isClientSide) {
            HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(mixingCauldron.getPos())), new EmitParticlesPacket(mixingCauldron.getPos(), 3, false));
        }
        level.playSound(null, mixingCauldron.getPos().getX() + 0.5f, mixingCauldron.getPos().getY() + 0.5f, mixingCauldron.getPos().getZ() + 0.5f, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * random.nextFloat());

        return InteractionResult.SUCCESS;
    }

    private void shrinkItem(Player player, InteractionHand hand, ItemStack stackIn, ItemStack stackOut) {
        if(!player.isCreative()) {
            stackIn.shrink(1);
            if(stackIn.isEmpty()) {
                player.setItemInHand(hand, stackOut);
            }
            else {
                player.getInventory().placeItemBackInInventory(stackOut);
            }
        }
    }


    public MixingCauldron(Properties properties) {

        super(properties.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(GUI_RENDER, false).setValue(DYED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CRAFT_DELAY, GUI_RENDER, DYED);
    }

    // drop blocks in getInventory() of the tile entity
//    @Override
//    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
//        MixingCauldronTile te = (MixingCauldronTile) worldIn.getBlockEntity(pos);
//
//        te.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
//
//            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(0)));
//            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(1)));
//            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(2)));
//            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(3)));
//            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(4)));
//            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(5)));
//            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(6)));
//            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(7)));
//            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(8)));
//            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(9)));
//            if (!player.getAbilities().instabuild)
//                worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f, new ItemStack(ModBlocks.MIXING_CAULDRON.get().asItem())));
//        });
//
//        super.playerWillDestroy(worldIn, pos, state, player);
//    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity instanceof MixingCauldronTile te) {

                te.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {

                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(0)));
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(1)));
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(2)));
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(3)));
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(4)));
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(5)));
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(6)));
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(7)));
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(8)));
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, h.getStackInSlot(9)));
                });
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        withTileEntityDo(worldIn, pos, te -> te.setDyeColor(MixingCauldron.getColorStatic(stack)));
        super.setPlacedBy(worldIn, pos, state, placer, stack);

        if (stack.hasCustomHoverName()) {
            if (worldIn.getBlockEntity(pos) instanceof MixingCauldronTile mixingCauldronTile)
                mixingCauldronTile.customName = stack.getHoverName();
        }

    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);

        withTileEntityDo(level, pos, te -> {
            if (level.getBlockState(neighbor).is(HexereiTags.Blocks.HEAT_SOURCES))
                te.checkCraft = true;
        });

    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {
        ItemStack item = new ItemStack(this);
        Optional<MixingCauldronTile> tileEntityOptional = Optional.ofNullable(getBlockEntity(worldIn, pos));

        setColor(item, tileEntityOptional.map(cauldron -> cauldron.dyeColor).orElse(0x422F1E));

        Component customName = tileEntityOptional.map(MixingCauldronTile::getCustomName)
                .orElse(null);

        if (customName != null)
            if (customName.getString().length() > 0)
                item.setHoverName(customName);
        return item;
    }

    public static int getColorStatic(ItemStack p_41122_) {
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0xFFBE1C;
    }


    public static DyeColor getDyeColorNamed(ItemStack stack) {

        return HexereiUtil.getDyeColorNamed(stack.getHoverName().getString(), 0);
    }

    public static int getColorValue(BlockState state, BlockPos pos, BlockGetter level) {
        ItemStack clone = ((MixingCauldron) state.getBlock()).getCloneItemStack(level, pos, state);
        int dyeCol = MixingCauldron.getColorStatic(clone);
        DyeColor color = MixingCauldron.getDyeColorNamed(clone);
        if (color == null && dyeCol != -1)
            return dyeCol;
        float[] colors = color.getTextureDiffuseColors();
        int r = (int) (colors[0] * 255.0F);
        int g = (int) (colors[1] * 255.0F);
        int b = (int) (colors[2] * 255.0F);
        return (r << 16) | (g << 8) | b;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {

        // get slots and animate particles based off number of items in the cauldron and based off the level and fluid type
        float height = MIN_Y;
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof MixingCauldronTile cauldronTile) {
            height = MIN_Y + (MAX_Y - MIN_Y) * Math.min(1, (float) cauldronTile.getFluidStack().getAmount() / cauldronTile.getTankCapacity(0)) + 1 / 16f;
//
            int num = cauldronTile.getNumberOfItems();

            if (cauldronTile.getFluidStack().getAmount() > 0) {
                for (int i = 0; i < Mth.floor(cauldronTile.getFluidStack().getAmount() / 666f + 0.5f); i++) {
                    if (rand.nextDouble() > 0.5f)
                        world.addParticle(new CauldronParticleData(ModParticleTypes.CAULDRON.get(), cauldronTile.getFluidStack()), pos.getX() + 0.2d + (0.6d * rand.nextDouble()), pos.getY() + height, pos.getZ() + 0.2d + (0.6d * rand.nextDouble()), (rand.nextDouble() - 0.5d) / 50d, (rand.nextDouble() + 0.5d) * 0.004d, (rand.nextDouble() - 0.5d) / 50d);
                }
                for (int i = 0; i < num; i++) {
                    if (rand.nextDouble() > 0.5f)
                        world.addParticle(new CauldronParticleData(ModParticleTypes.CAULDRON.get(), cauldronTile.getFluidStack()), pos.getX() + 0.2d + (0.6d * rand.nextDouble()), pos.getY() + height, pos.getZ() + 0.2d + (0.6d * rand.nextDouble()), (rand.nextDouble() - 0.5d) / 50d, (rand.nextDouble() + 0.5d) * 0.004d, (rand.nextDouble() - 0.5d) / 50d);
                }

                BlockState heatSource = world.getBlockState(pos.below());
                if(heatSource.is(HexereiTags.Blocks.HEAT_SOURCES)){
                    for (int i = 0; i < num + 5; i++) {
                        if (rand.nextDouble() > 0.5f)
                            world.addParticle(new CauldronParticleData(ModParticleTypes.CAULDRON.get(), cauldronTile.getFluidStack()), pos.getX() + 0.2d + (0.6d * rand.nextDouble()), pos.getY() + height, pos.getZ() + 0.2d + (0.6d * rand.nextDouble()), (rand.nextDouble() - 0.5d) / 50d, (rand.nextDouble() + 0.5d) * 0.014d, (rand.nextDouble() - 0.5d) / 50d);
                    }
                }

                if (cauldronTile.getFluidStack().isFluidEqual(new FluidStack(Fluids.WATER, 1)) || cauldronTile.getFluidStack().isFluidEqual(new FluidStack(ModFluids.TALLOW_FLUID.get(), 1))) {
                    world.addParticle(ParticleTypes.BUBBLE, pos.getX() + 0.2d + (0.6d * rand.nextDouble()), pos.getY() + height, pos.getZ() + 0.2d + (0.6d * rand.nextDouble()), (rand.nextDouble() - 0.5d) / 50d, (rand.nextDouble() + 0.5d) * 0.005d, (rand.nextDouble() - 0.5d) / 50d);
                } else if (cauldronTile.getFluidStack().isFluidEqual(new FluidStack(ModFluids.BLOOD_FLUID.get(), 1))) {
                    if (rand.nextInt(20) == 0)
                        world.addParticle(ModParticleTypes.BLOOD.get(), pos.getX() + 0.2d + (0.6d * rand.nextDouble()), pos.getY() + height, pos.getZ() + 0.2d + (0.6d * rand.nextDouble()), (rand.nextDouble() - 0.5d) / 75d, (rand.nextDouble() + 0.5d) * 0.0005d, (rand.nextDouble() - 0.5d) / 75d);
                }
            }
            if (state.getValue(CRAFT_DELAY) >= MixingCauldronTile.craftDelayMax * 0.80) {
                if (!world.isClientSide)
                    HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> tileEntity.getLevel().getChunkAt(cauldronTile.getPos())), new EmitParticlesPacket(cauldronTile.getPos(), 3, false));

            }
        }
        super.animateTick(state, world, pos, rand);
    }


    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockPos blockpos = PointedDripstoneBlock.findStalactiteTipAboveCauldron(pLevel, pPos);
        if (blockpos != null) {
            Fluid fluid = PointedDripstoneBlock.getCauldronFillFluidType(pLevel, blockpos);
            if (fluid != Fluids.EMPTY && this.canReceiveStalactiteDrip(fluid)) {
                this.receiveStalactiteDrip(pState, pLevel, pPos, fluid);
            }

        }
    }

    private MenuProvider createContainerProvider(Level worldIn, BlockPos pos) {
        return new MenuProvider() {
            @org.jetbrains.annotations.Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new MixingCauldronContainer(i, worldIn, pos, playerInventory, playerEntity);
            }

            @Override
            public Component getDisplayName() {
                if (worldIn.getBlockEntity(pos) instanceof MixingCauldronTile mixingCauldronTile && mixingCauldronTile.customName != null)
                    return Component.translatable(mixingCauldronTile.customName.getString());
                return Component.translatable("screen.hexerei.mixing_cauldron");
            }
        };
    }

//    @Nullable
//    @Override
//    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
//        BlockEntity te = ModTileEntities.MIXING_CAULDRON_TILE.get().create();
//        return te;
//    }


    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof MixingCauldronTile) {
            ((MixingCauldronTile) tileentity).entityInside(entityIn);
        }

    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn) {

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            tooltip.add(Component.translatable("tooltip.hexerei.mixing_cauldron_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
//            tooltip.add(Component.translatable("tooltip.hexerei.mixing_cauldron"));
        }
        super.appendHoverText(stack, world, tooltip, flagIn);
    }

//    @Override
//    public boolean hasBlockEntity(BlockState state) {
//        return true;
//    }


    @Override
    public Class<MixingCauldronTile> getTileEntityClass() {
        return MixingCauldronTile.class;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MixingCauldronTile(ModTileEntities.MIXING_CAULDRON_TILE.get(), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType) {
        return entityType == ModTileEntities.MIXING_CAULDRON_TILE.get() ?
                (world2, pos, state2, entity) -> ((MixingCauldronTile) entity).tick() : null;
    }
}
