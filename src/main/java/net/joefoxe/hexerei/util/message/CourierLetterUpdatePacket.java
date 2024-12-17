package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.OwlCourierDepot;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotSavedData;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.CourierLetterItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CourierLetterUpdatePacket {

    final CompoundTag lines;
    int slotIndex;
    boolean sealed;

    public CourierLetterUpdatePacket(int slotIndex, CompoundTag lines, boolean sealed) {
        this.slotIndex = slotIndex;
        this.lines = lines;
        this.sealed = sealed;

    }
    public CourierLetterUpdatePacket(FriendlyByteBuf buf) {
        this.slotIndex = buf.readInt();
        this.lines = buf.readNbt();
        this.sealed = buf.readBoolean();
    }

    public static void encode(CourierLetterUpdatePacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.slotIndex);
        buffer.writeNbt(object.lines);
        buffer.writeBoolean(object.sealed);
    }

    public static CourierLetterUpdatePacket decode(FriendlyByteBuf buffer) {
        return new CourierLetterUpdatePacket(buffer);
    }

    public static void consume(CourierLetterUpdatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level();
            }

            ServerPlayer player = ctx.get().getSender();

            if (player != null){
                if (Inventory.isHotbarSlot(packet.slotIndex)) {
                    if (packet.slotIndex == player.getInventory().selected) {
                        ItemStack stack = player.getMainHandItem();
                        if (stack.getItem() instanceof CourierLetterItem courierLetterItem) {

                            CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
                            tag.put("Message", packet.lines);
                            if (packet.sealed) {
                                tag.putBoolean("Sealed", true);
                            }
                        }

                    }
                } else {
                    //offhand
                    ItemStack stack = player.getOffhandItem();
                    if (stack.getItem() instanceof CourierLetterItem courierLetterItem) {

                        CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
                        tag.put("Message", packet.lines);
                        if (packet.sealed) {
                            tag.putBoolean("Sealed", true);
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}