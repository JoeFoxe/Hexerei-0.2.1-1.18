package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AskForSyncPacket {
    int sourceId;

    public AskForSyncPacket(Entity entity) {
        this.sourceId = entity.getId();
    }
    public AskForSyncPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
    }

    public static void encode(AskForSyncPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
    }

    public static AskForSyncPacket decode(FriendlyByteBuf buffer) {
        return new AskForSyncPacket(buffer);
    }

    public static void consume(AskForSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level();
            }

            if(world.getEntity(packet.sourceId) instanceof CrowEntity crow) {
                crow.sync();
            }

            if(world.getEntity(packet.sourceId) instanceof OwlEntity crow) {
                crow.sync();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}