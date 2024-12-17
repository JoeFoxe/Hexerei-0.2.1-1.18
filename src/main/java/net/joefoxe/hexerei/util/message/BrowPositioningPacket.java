package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BrowPositioningPacket {
    int sourceId;
    OwlEntity.BrowPositioning browPositioning;

    public BrowPositioningPacket(Entity entity, OwlEntity.BrowPositioning browPositioning) {
        this.sourceId = entity.getId();
        this.browPositioning = browPositioning;

    }
    public BrowPositioningPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.browPositioning = buf.readEnum(OwlEntity.BrowPositioning.class);
    }

    public static void encode(BrowPositioningPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeEnum(object.browPositioning);
    }

    public static BrowPositioningPacket decode(FriendlyByteBuf buffer) {
        return new BrowPositioningPacket(buffer);
    }

    public static void consume(BrowPositioningPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level();
            }

            if(world.getEntity(packet.sourceId) != null) {
                if((world.getEntity(packet.sourceId)) instanceof OwlEntity owl) {
                    owl.setBrowPos(packet.browPositioning);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}