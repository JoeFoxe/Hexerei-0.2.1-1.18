package net.joefoxe.hexerei.block.custom;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.connected.CTDyable;
import net.joefoxe.hexerei.block.connected.Waxed;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ConnectingCarpetDyed extends CarpetBlock implements Waxed, CTDyable {

    public static BooleanProperty WEST = BooleanProperty.create("west"),
                                  EAST = BooleanProperty.create("east");
    public static final EnumProperty<North> NORTH = EnumProperty.create("north", North.class);
    public static final EnumProperty<South> SOUTH = EnumProperty.create("south", South.class);
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    public DyeColor dyeColor;

    public ConnectingCarpetDyed(Properties pProperties, DyeColor dyeColor){
        super(pProperties.noOcclusion());
        registerDefaultState(super.defaultBlockState()
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(NORTH, North.NONE)
                .setValue(SOUTH, South.NONE)
                .setValue(COLOR, dyeColor));

        this.dyeColor = dyeColor;
    }


    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        List<ItemStack> drops = super.getDrops(pState, pParams);
        if (!pState.hasProperty(COLOR))
            return drops;
        List<ItemStack> updated_drops = new ArrayList<>();
        for (ItemStack stack : drops){
            if (stack.getItem() == ModBlocks.INFUSED_FABRIC_CARPET.get().asItem() || stack.getItem() == ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get().asItem()){
                DyeColor color = pState.getValue(COLOR);
                stack.getOrCreateTag().putString("color", color.getName());
            }
            updated_drops.add(stack);
        }
        return updated_drops;
    }
    @Override
    public DyeColor getDyeColor(BlockState blockState) {
        if (blockState.hasProperty(COLOR))
            return blockState.getValue(COLOR);
        return DyeColor.WHITE;
    }

    public BlockState rotate(BlockState pState, Rotation pRot) {
        boolean east = pState.getValue(EAST);
        boolean west = pState.getValue(WEST);
        North northState = pState.getValue(NORTH);
        South southState = pState.getValue(SOUTH);
        boolean north = northState == North.ALL || northState == North.JUST_NORTH || northState == North.NORTH_AND_NORTH_EAST || northState == North.NORTH_AND_NORTH_WEST;
        boolean north_east = northState == North.ALL || northState == North.JUST_NORTH_EAST || northState == North.NORTH_AND_NORTH_EAST || northState == North.NORTH_EAST_AND_NORTH_WEST;
        boolean north_west = northState == North.ALL || northState == North.JUST_NORTH_WEST || northState == North.NORTH_AND_NORTH_WEST || northState == North.NORTH_EAST_AND_NORTH_WEST;
        boolean south = southState == South.ALL || southState == South.JUST_SOUTH || southState == South.SOUTH_AND_SOUTH_EAST || southState == South.SOUTH_AND_SOUTH_WEST;
        boolean south_east = southState == South.ALL || southState == South.JUST_SOUTH_EAST || southState == South.SOUTH_AND_SOUTH_EAST || southState == South.SOUTH_EAST_AND_SOUTH_WEST;
        boolean south_west = southState == South.ALL || southState == South.JUST_SOUTH_WEST || southState == South.SOUTH_AND_SOUTH_WEST || southState == South.SOUTH_EAST_AND_SOUTH_WEST;

        switch (pRot){
            case NONE -> {
                return pState;
            }
            case CLOCKWISE_90 -> {
                North northTemp = North.NONE;
                South southTemp = South.NONE;
                if(south_east && east && north_east)
                    southTemp = South.ALL;
                else if (!south_east && east && north_east)
                    southTemp = South.SOUTH_AND_SOUTH_EAST;
                else if (south_east && east)
                    southTemp = South.SOUTH_AND_SOUTH_WEST;
                else if (south_east && north_east)
                    southTemp = South.SOUTH_EAST_AND_SOUTH_WEST;
                else if (!south_east && east)
                    southTemp = South.JUST_SOUTH;
                else if (!south_east && north_east)
                    southTemp = South.JUST_SOUTH_EAST;
                else if (south_east)
                    southTemp = South.JUST_SOUTH_WEST;

                if(south_west && west && north_west)
                    northTemp = North.ALL;
                else if (!south_west && west && north_west)
                    northTemp = North.NORTH_AND_NORTH_EAST;
                else if (south_west && west)
                    northTemp = North.NORTH_AND_NORTH_WEST;
                else if (south_west && north_west)
                    northTemp = North.NORTH_EAST_AND_NORTH_WEST;
                else if (!south_west && west)
                    northTemp = North.JUST_NORTH;
                else if (!south_west && north_west)
                    northTemp = North.JUST_NORTH_EAST;
                else if (south_west)
                    northTemp = North.JUST_NORTH_WEST;

                return pState.setValue(EAST, north).setValue(WEST, south).setValue(NORTH, northTemp).setValue(SOUTH, southTemp);
            }
            case CLOCKWISE_180 -> {
                North northTemp = North.NONE;
                South southTemp = South.NONE;
                if (north && north_east && north_west)
                    southTemp = South.ALL;
                else if (north && north_west)
                    southTemp = South.SOUTH_AND_SOUTH_EAST;
                else if (north && north_east)
                    southTemp = South.SOUTH_AND_SOUTH_WEST;
                else if (north_west && north_east)
                    southTemp = South.SOUTH_EAST_AND_SOUTH_WEST;
                else if (!north_west && !north_east && north)
                    southTemp = South.JUST_SOUTH;
                else if (north_west)
                    southTemp = South.JUST_SOUTH_EAST;
                else if (north_east)
                    southTemp = South.JUST_SOUTH_WEST;

                if (south && south_east && south_west)
                    northTemp = North.ALL;
                else if (south && south_west)
                    northTemp = North.NORTH_AND_NORTH_EAST;
                else if (south && south_east)
                    northTemp = North.NORTH_AND_NORTH_WEST;
                else if (south_west && south_east)
                    northTemp = North.NORTH_EAST_AND_NORTH_WEST;
                else if (!south_west && !south_east && south)
                    northTemp = North.JUST_NORTH;
                else if (south_west)
                    northTemp = North.JUST_NORTH_EAST;
                else if (south_east)
                    northTemp = North.JUST_NORTH_WEST;

                return pState.setValue(EAST, west).setValue(WEST, east).setValue(NORTH, northTemp).setValue(SOUTH, southTemp);

            }
            case COUNTERCLOCKWISE_90 -> {
                North northTemp = North.NONE;
                South southTemp = South.NONE;
                if(north_west && west && south_west)
                    southTemp = South.ALL;
                else if (!north_west && west && south_west)
                    southTemp = South.SOUTH_AND_SOUTH_EAST;
                else if (north_west && west)
                    southTemp = South.SOUTH_AND_SOUTH_WEST;
                else if (north_west && south_west)
                    southTemp = South.SOUTH_EAST_AND_SOUTH_WEST;
                else if (!north_west && west)
                    southTemp = South.JUST_SOUTH;
                else if (!north_west && south_west)
                    southTemp = South.JUST_SOUTH_EAST;
                else if (north_west)
                    southTemp = South.JUST_SOUTH_WEST;

                if(north_east && east && south_east)
                    northTemp = North.ALL;
                else if (!north_east && east && south_east)
                    northTemp = North.NORTH_AND_NORTH_EAST;
                else if (north_east && east)
                    northTemp = North.NORTH_AND_NORTH_WEST;
                else if (north_east && south_east)
                    northTemp = North.NORTH_EAST_AND_NORTH_WEST;
                else if (!north_east && east)
                    northTemp = North.JUST_NORTH;
                else if (!north_east && south_east)
                    northTemp = North.JUST_NORTH_EAST;
                else if (north_east)
                    northTemp = North.JUST_NORTH_WEST;

                return pState.setValue(EAST, south).setValue(WEST, north).setValue(NORTH, northTemp).setValue(SOUTH, southTemp);
            }
        }
        return pState;
//        return pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos blockpos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if(player.getItemInHand(pHand).getItem() instanceof DyeItem dyeItem) {
            DyeColor dyecolor = dyeItem.getDyeColor();
            if(this.getDyeColor(pState) == dyecolor)
                return InteractionResult.FAIL;

            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, player.getItemInHand(pHand));
            }

            BlockState newBlockstate = pLevel.getBlockState(blockpos).setValue(COLOR, dyecolor);
//
            if(pState.getBlock() == ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get()) {
                Block.popResource(pLevel, blockpos, new ItemStack(Items.GOLD_NUGGET));
                newBlockstate = ModBlocks.INFUSED_FABRIC_CARPET.get().defaultBlockState().setValue(COLOR, dyecolor);
            }

            pLevel.setBlockAndUpdate(blockpos, newBlockstate);
            pLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, newBlockstate));
            pLevel.levelEvent(player, 3003, blockpos, 0);
            return InteractionResult.sidedSuccess(pLevel.isClientSide);

        }
        else if(player.getItemInHand(pHand).getItem() == Items.GOLD_NUGGET) {
            if(pState.getBlock() == ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get())
                return InteractionResult.FAIL;

            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, player.getItemInHand(pHand));
            }
            BlockState newBlockstate = ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get().defaultBlockState();
            if(!player.isCreative())
                player.getItemInHand(pHand).shrink(1);

            pLevel.setBlockAndUpdate(blockpos, newBlockstate);
            pLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, newBlockstate));
            pLevel.levelEvent(player, 3004, blockpos, 0);
            pLevel.playSound(player, blockpos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(pLevel.isClientSide);

        }

        return super.use(pState, pLevel, blockpos, player, pHand, pHit);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        ItemStack stack = super.getCloneItemStack(pLevel, pPos, pState);
        DyeColor color = getDyeColor(pState);
        if (color != DyeColor.WHITE)
            stack.getOrCreateTag().putString("color", color.getName());
        return stack;
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        return getUnWaxed(state, context, toolAction);
    }

    public static int getColorValue(ItemStack stack) {
        if (stack.hasTag() && stack.getTag() != null && stack.getTag().contains("color"))
            return getColorValue(DyeColor.byName(stack.getOrCreateTag().getString("color"), DyeColor.WHITE));
        return getColorValue(DyeColor.WHITE);
    }


    public static int getColorValue(BlockState state) {
        int col = getColorValue(DyeColor.WHITE);
        if (state.getBlock() instanceof CTDyable ctDyable)
            col = getColorValue(ctDyable.getDyeColor(state));
        return col;
    }

    public static float[] toDarkPastel(float[] rgb) {
        float[] hsl = HexereiUtil.rgbToHsl(rgb[0], rgb[1], rgb[2]);

        // Increase lightness and decrease saturation
        hsl[1] = Math.max(0.0f, hsl[1] - 0.3f); // Decrease saturation
//        hsl[2] = Math.max(0.0f, hsl[2] - 0.2f); // Decrease lightness

        return HexereiUtil.hslToRgb(hsl[0], hsl[1], hsl[2]);
    }

    public static int getColorValue(DyeColor color) {
        if (color == null)
            return 0;

        float[] colors = toDarkPastel(color.getTextureDiffuseColors());
        int r = (int) (colors[0] * 255.0F);
        int g = (int) (colors[1] * 255.0F);
        int b = (int) (colors[2] * 255.0F);
        return r << 16 | g << 8 | b;
    }

    private static boolean canConnect(BlockState state1, BlockState state2){
        if (state1.getBlock() == state2.getBlock()){
            if (state1.hasProperty(COLOR) && state2.hasProperty(COLOR)){
                return state1.getValue(COLOR) == state2.getValue(COLOR);
            }
        }
        return false;
    }


    protected BlockState updateCorners(BlockGetter world, BlockPos pos, BlockState state) {
        BlockState bs_north = world.getBlockState(pos.north());
        BlockState bs_north_east = world.getBlockState(pos.north().east());
        BlockState bs_north_west = world.getBlockState(pos.north().west());
        BlockState bs_east = world.getBlockState(pos.east());
        BlockState bs_south = world.getBlockState(pos.south());
        BlockState bs_south_east = world.getBlockState(pos.south().east());
        BlockState bs_south_west = world.getBlockState(pos.south().west());
        BlockState bs_west = world.getBlockState(pos.west());
        North north = North.NONE;
        South south = South.NONE;

        if(canConnect(state, bs_north)){
            north = North.JUST_NORTH;
            if(canConnect(state, bs_north_west) && !canConnect(state, bs_north_east)){
                north = North.NORTH_AND_NORTH_WEST;
            }
            if(!canConnect(state, bs_north_west) && canConnect(state, bs_north_east)){
                north = North.NORTH_AND_NORTH_EAST;
            }
            if(canConnect(state, bs_north_west) && canConnect(state, bs_north_east)){
                north = North.ALL;
            }
        }else{
            if(canConnect(state, bs_north_west) && !canConnect(state, bs_north_east)){
                north = North.JUST_NORTH_WEST;
            }
            if(!canConnect(state, bs_north_west) && canConnect(state, bs_north_east)){
                north = North.JUST_NORTH_EAST;
            }
        }
        if(canConnect(state, bs_south)){
            south = South.JUST_SOUTH;
            if(canConnect(state, bs_south_west) && !canConnect(state, bs_south_east)){
                south = South.SOUTH_AND_SOUTH_WEST;
            }
            if(!canConnect(state, bs_south_west) && canConnect(state, bs_south_east)){
                south = South.SOUTH_AND_SOUTH_EAST;
            }
            if(canConnect(state, bs_south_west) && canConnect(state, bs_south_east)){
                south = South.ALL;
            }
        }else{
            if(canConnect(state, bs_south_west) && !canConnect(state, bs_south_east)){
                south = South.JUST_SOUTH_WEST;
            }
            if(!canConnect(state, bs_south_west) && canConnect(state, bs_south_east)){
                south = South.JUST_SOUTH_EAST;
            }
        }


        boolean east = canConnect(state, bs_east),
                west = canConnect(state, bs_west);
        return state
                .setValue(NORTH, north).setValue(EAST, east)
                .setValue(SOUTH, south).setValue(WEST, west);
    }
    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState iBlockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        ItemStack stack = context.getItemInHand();
        BlockPos blockpos = context.getClickedPos();
        if (stack.hasTag()) {
            String colorName = stack.getOrCreateTag().getString("color");
            DyeColor color = DyeColor.byName(colorName, DyeColor.WHITE); // Default to WHITE if the colorName is invalid
            return updateCorners(iblockreader, blockpos, super.getStateForPlacement(context)).setValue(COLOR, color);
        } else {
            return updateCorners(iblockreader, blockpos, super.getStateForPlacement(context));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WEST, EAST, NORTH, SOUTH, COLOR);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {

        return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : updateCorners(world, pos, state);
    }
    public enum North implements StringRepresentable {
        JUST_NORTH,
        NORTH_AND_NORTH_WEST,
        NORTH_AND_NORTH_EAST,
        JUST_NORTH_WEST,
        JUST_NORTH_EAST,
        NORTH_EAST_AND_NORTH_WEST,
        ALL,
        NONE;

        public String toString() {
            return this.getSerializedName();
        }

        public String getSerializedName() {
            return switch (this){
                case JUST_NORTH -> "north";
                case NORTH_AND_NORTH_WEST -> "north_and_north_west";
                case NORTH_AND_NORTH_EAST -> "north_and_north_east";
                case JUST_NORTH_WEST -> "north_west";
                case JUST_NORTH_EAST -> "north_east";
                case NORTH_EAST_AND_NORTH_WEST -> "north_east_and_north_west";
                case ALL -> "all";
                case NONE -> "none";
            };
        }
    }
    public enum South implements StringRepresentable {
        JUST_SOUTH,
        SOUTH_AND_SOUTH_WEST,
        SOUTH_AND_SOUTH_EAST,
        JUST_SOUTH_WEST,
        JUST_SOUTH_EAST,
        SOUTH_EAST_AND_SOUTH_WEST,
        ALL,
        NONE;

        public String toString() {
            return this.getSerializedName();
        }

        public String getSerializedName() {
            return switch (this){
                case JUST_SOUTH -> "south";
                case SOUTH_AND_SOUTH_WEST -> "south_and_south_west";
                case SOUTH_AND_SOUTH_EAST -> "south_and_south_east";
                case JUST_SOUTH_WEST -> "south_west";
                case JUST_SOUTH_EAST -> "south_east";
                case SOUTH_EAST_AND_SOUTH_WEST -> "south_east_and_south_west";
                case ALL -> "all";
                case NONE -> "none";
            };
        }
    }
}
