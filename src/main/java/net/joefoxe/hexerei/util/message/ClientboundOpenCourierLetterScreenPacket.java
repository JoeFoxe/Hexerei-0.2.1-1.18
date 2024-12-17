package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundOpenCourierLetterScreenPacket {
    int slotIndex;
    InteractionHand hand;

    public ClientboundOpenCourierLetterScreenPacket(int slotIndex, InteractionHand hand) {
        this.slotIndex = slotIndex;
        this.hand = hand;
    }

    public static void encode(ClientboundOpenCourierLetterScreenPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.slotIndex);
        buffer.writeBoolean(object.hand == InteractionHand.MAIN_HAND);
    }

    public static ClientboundOpenCourierLetterScreenPacket decode(FriendlyByteBuf buffer) {
        return new ClientboundOpenCourierLetterScreenPacket(buffer.readInt(), buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    public static void consume(ClientboundOpenCourierLetterScreenPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level();
            }

            DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
                if (Minecraft.getInstance().player != null)
                    setScreen(packet.slotIndex, packet.hand, packet.slotIndex > 0 ? Minecraft.getInstance().player.getInventory().getItem(packet.slotIndex) : Minecraft.getInstance().player.getItemInHand(InteractionHand.OFF_HAND));
            });

        });
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static void setScreen(int slotIndex, InteractionHand hand, ItemStack stack) {
        Minecraft.getInstance().setScreen(new net.joefoxe.hexerei.screen.CourierLetterScreen(slotIndex, hand, stack));

    }
}