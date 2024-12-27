package net.joefoxe.hexerei.container;

import com.google.common.collect.Lists;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.custom.CrowFluteItem;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import java.util.ArrayList;
import java.util.List;


public class CrowFluteContainer extends AbstractContainerMenu {
    private final Player playerEntity;
    public final ItemStack stack;
    public InteractionHand hand;
    private final IItemHandler playerInventory;
    public List<Entity> crowList;

    public CrowFluteContainer(int windowId, Inventory playerInv) {
        this(windowId, playerInv, playerInv.player, InteractionHand.MAIN_HAND);
    }

    public CrowFluteContainer(int windowId, Inventory playerInv, RegistryFriendlyByteBuf byteBuf) {
        this(windowId, playerInv, playerInv.player, InteractionHand.MAIN_HAND);
    }

    public CrowFluteContainer(int windowId, Inventory playerInventory, Player player, InteractionHand hand) {
        super(ModContainers.CROW_FLUTE_CONTAINER.get(), windowId);
        this.stack = playerInventory.player.getItemInHand(hand);
        playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.hand = hand;
        this.crowList = Lists.newArrayList();

        FluteData fluteData = stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);

        if(player.level().isClientSide) {
            for (FluteData.CrowIds crowIds : fluteData.crowList()) {
                Entity entity = (player.level()).getEntity(crowIds.id());
                if (entity instanceof CrowEntity)
                    this.crowList.add(entity);
            }
        }

    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return null;
    }

    @Override
    public void clicked(int p_150400_, int p_150401_, ClickType p_150402_, Player p_150403_) {
        super.clicked(p_150400_, p_150401_, p_150402_, p_150403_);


    }




    public int getCommand() {
        return stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandSelected();
    }

    public void setCommand(int value) {
         FluteData fluteData = stack.get(ModDataComponents.FLUTE);
         if (fluteData != null) {
             fluteData = new FluteData(value, fluteData.helpCommandSelected(), fluteData.commandMode(), fluteData.crowList(), fluteData.dyeColor1(), fluteData.dyeColor2());
             stack.set(ModDataComponents.FLUTE, fluteData);
         }
        ((CrowFluteItem)stack.getItem()).setCommand(value, stack, playerEntity, hand);
    }

    public int getHelpCommand() {
        return stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).helpCommandSelected();
    }

    public void setHelpCommand(int value) {
        FluteData fluteData = stack.get(ModDataComponents.FLUTE);
        if (fluteData != null) {
            fluteData = new FluteData(fluteData.commandSelected(), value, fluteData.commandMode(), fluteData.crowList(), fluteData.dyeColor1(), fluteData.dyeColor2());
            stack.set(ModDataComponents.FLUTE, fluteData);
        }
        ((CrowFluteItem)stack.getItem()).setHelpCommand(value, stack, playerEntity, hand);
    }

    public void setCommandMode(int value) {
        FluteData fluteData = stack.get(ModDataComponents.FLUTE);
        if (fluteData != null) {
            fluteData = new FluteData(fluteData.commandSelected(), fluteData.commandSelected(), value, fluteData.crowList(), fluteData.dyeColor1(), fluteData.dyeColor2());
            stack.set(ModDataComponents.FLUTE, fluteData);
        }
        ((CrowFluteItem)stack.getItem()).setCommandMode(value, stack, playerEntity, hand);
    }

    public int getCommandMode() {
        return stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandMode();
    }

    public void clearCrowList() {

        FluteData fluteData = stack.get(ModDataComponents.FLUTE);
        if (fluteData != null) {
            fluteData = new FluteData(fluteData.commandSelected(), fluteData.commandSelected(), fluteData.commandMode(), new ArrayList<>(), fluteData.dyeColor1(), fluteData.dyeColor2());
            stack.set(ModDataComponents.FLUTE, fluteData);
        }
        ((CrowFluteItem)stack.getItem()).clearCrowList(stack, playerEntity, hand);
        this.crowList.clear();
    }

    public void clearCrowPerch() {
        ((CrowFluteItem)stack.getItem()).clearCrowPerch(stack, playerEntity, hand);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return playerIn.getMainHandItem() == stack;
    }

}
