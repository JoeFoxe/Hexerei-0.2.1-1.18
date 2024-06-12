package net.joefoxe.hexerei.block.connected;

import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed.COLOR;

public class FabricBlock extends WaxedLayeredBlock implements CTDyable {

	public FabricBlock(Properties p_55926_) {
		super(p_55926_);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(COLOR);
	}


	@Override
	public DyeColor getDyeColor(BlockState blockState) {
		if (blockState.hasProperty(COLOR))
			return blockState.getValue(COLOR);
		return DyeColor.WHITE;
	}

	@Nullable
	@Override
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
		return getUnWaxed(state, context, toolAction);
	}


	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		ItemStack stack = context.getItemInHand();
		if (state != null && state.hasProperty(COLOR)){
			if (stack.hasTag()) {
				String colorName = stack.getOrCreateTag().getString("color");
				DyeColor color = DyeColor.byName(colorName, DyeColor.WHITE); // Default to WHITE if the colorName is invalid
				return state.setValue(COLOR, color);
			}
		}
		return super.getStateForPlacement(context);
	}

	@Override
	public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
		List<ItemStack> drops = super.getDrops(pState, pParams);
		if (!pState.hasProperty(COLOR))
			return drops;
		List<ItemStack> updated_drops = new ArrayList<>();
		for (ItemStack stack : drops){
			if (stack.getItem() == ModBlocks.INFUSED_FABRIC_BLOCK.get().asItem() || stack.getItem() == ModBlocks.WAXED_INFUSED_FABRIC_BLOCK.get().asItem()){
				DyeColor color = pState.getValue(COLOR);
				stack.getOrCreateTag().putString("color", color.getName());
			}
			updated_drops.add(stack);
		}
		return updated_drops;
	}
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos blockpos, Player player, InteractionHand pHand, BlockHitResult pHit) {
		if(player.getItemInHand(pHand).getItem() instanceof DyeItem dyeItem) {
			DyeColor dyecolor = dyeItem.getDyeColor();
			if(pState.getBlock() != ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get())
				if(this.getDyeColor(pState) == dyecolor)
					return InteractionResult.FAIL;

			if (player instanceof ServerPlayer) {
				CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, player.getItemInHand(pHand));
			}
			BlockState newBlockstate = pLevel.getBlockState(blockpos).setValue(COLOR, dyecolor);

			if(pState.getBlock() == ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get()) {
				Block.popResource(pLevel, blockpos, new ItemStack(Items.GOLD_NUGGET));
				newBlockstate = ModBlocks.INFUSED_FABRIC_BLOCK.get().defaultBlockState().setValue(COLOR, dyecolor);
			}

			pLevel.setBlockAndUpdate(blockpos, newBlockstate);
			pLevel.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, newBlockstate));
			pLevel.levelEvent(player, 3003, blockpos, 0);
			return InteractionResult.sidedSuccess(pLevel.isClientSide);

		}
		else if(player.getItemInHand(pHand).getItem() == Items.GOLD_NUGGET) {
			if(pState.getBlock() == ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get())
				return InteractionResult.FAIL;

			if (player instanceof ServerPlayer) {
				CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, player.getItemInHand(pHand));
			}
			BlockState newBlockstate = ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get().defaultBlockState();
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

}