package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class TailFanPacket {
    int sourceId;
    int duration;

    public TailFanPacket(Entity entity, int duration) {
        this.sourceId = entity.getId();
        this.duration = duration;
    }
    public TailFanPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.duration = buf.readInt();
    }

    public static void encode(TailFanPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeInt(object.duration);
    }

    public static TailFanPacket decode(FriendlyByteBuf buffer) {
        return new TailFanPacket(buffer);
    }

    public static void consume(TailFanPacket packet, Supplier<NetworkEvent.Context> ctx) {
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
                if((world.getEntity(packet.sourceId)) instanceof CrowEntity crow) {
                    crow.tailFan = true;
                    crow.tailFanTimer = 15;
                    crow.tailFanTiltAngle = 20 + new Random().nextInt(20);
                }
                if((world.getEntity(packet.sourceId)) instanceof OwlEntity owl) {
                    owl.tailFanAnimation.start();
                    owl.tailFanAnimation.activeTimer = packet.duration;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}