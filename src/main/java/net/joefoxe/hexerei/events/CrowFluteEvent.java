package net.joefoxe.hexerei.events;

import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.custom.CrowFluteItem;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class CrowFluteEvent {

    @SubscribeEvent
    public static void selectBlockPosition(PlayerInteractEvent.RightClickBlock event) {
        Item item = event.getItemStack().getItem();
        if (item instanceof CrowFluteItem) {
            if (event.getItemStack().getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandMode() == 2)
                event.setUseBlock(TriState.FALSE);
        }
    }
}
