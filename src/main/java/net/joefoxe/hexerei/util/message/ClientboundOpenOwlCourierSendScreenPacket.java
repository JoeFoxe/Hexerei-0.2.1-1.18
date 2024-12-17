package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundOpenOwlCourierSendScreenPacket {
    int owlId;
    InteractionHand hand;
    int selected;

    public ClientboundOpenOwlCourierSendScreenPacket(int owlId, InteractionHand hand, int selected) {
        this.owlId = owlId;
        this.hand = hand;
        this.selected = selected;
    }

    public static void encode(ClientboundOpenOwlCourierSendScreenPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.owlId);
        buffer.writeBoolean(object.hand == InteractionHand.MAIN_HAND);
        buffer.writeInt(object.selected);
    }

    public static ClientboundOpenOwlCourierSendScreenPacket decode(FriendlyByteBuf buffer) {
        return new ClientboundOpenOwlCourierSendScreenPacket(buffer.readInt(), buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, buffer.readInt());
    }

    public static void consume(ClientboundOpenOwlCourierSendScreenPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level();
            }

            if (world.getEntity(packet.owlId) instanceof OwlEntity owl) {
                DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
                    setScreen(owl, packet.hand, packet.selected);
                });

            }

        });
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static void setScreen(OwlEntity owl, InteractionHand hand, int selected) {
        Minecraft.getInstance().setScreen(new net.joefoxe.hexerei.screen.OwlCourierSendScreen(owl, hand, selected));

    }

}