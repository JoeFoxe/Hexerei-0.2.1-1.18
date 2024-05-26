package net.joefoxe.hexerei.item;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModItemGroup {

	public static final DeferredRegister<CreativeModeTab> ITEM_GROUP = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Hexerei.MOD_ID);

	public static final RegistryObject<CreativeModeTab> HEXEREI_GROUP = ITEM_GROUP.register("hexereiModTab", () -> CreativeModeTab.builder()
			.icon(() -> ModItems.MIXING_CAULDRON.get().getDefaultInstance())
			.title(Component.translatable("itemGroup.hexereiModTab"))
			.displayItems(new CreativeModeTab.DisplayItemsGenerator() {
				@Override
				public void accept(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
					ModItems.ITEMS.getEntries().forEach(entry -> output.accept(entry.get().getDefaultInstance()));
				}
			})
			.build());
}
