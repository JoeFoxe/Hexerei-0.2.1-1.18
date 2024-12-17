package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HeadShakePacket {
    int sourceId;
    int duration;
    public HeadShakePacket(Entity entity) {
        this.sourceId = entity.getId();
        this.duration = 15;
    }
    public HeadShakePacket(Entity entity, int duration) {
        this.sourceId = entity.getId();
        this.duration = duration;
    }
    public HeadShakePacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.duration = buf.readInt();
    }

    public static void encode(HeadShakePacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeInt(object.duration);
    }

    public static HeadShakePacket decode(FriendlyByteBuf buffer) {
        return new HeadShakePacket(buffer);
    }

    public static void consume(HeadShakePacket packet, Supplier<NetworkEvent.Context> ctx) {
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
                   owl.headShakeAnimation.start();
                   owl.headShakeAnimation.activeTimer = packet.duration;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}