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

public class TailWagPacket {
    int sourceId;
    int duration;
    public TailWagPacket(Entity entity, int duration) {
        this.sourceId = entity.getId();
        this.duration = duration;
    }
    public TailWagPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.duration = buf.readInt();
    }

    public static void encode(TailWagPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeInt(object.duration);
    }

    public static TailWagPacket decode(FriendlyByteBuf buffer) {
        return new TailWagPacket(buffer);
    }

    public static void consume(TailWagPacket packet, Supplier<NetworkEvent.Context> ctx) {
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
                    crow.tailWag = true;
                    crow.tailWagTimer = 15;
                }
                if((world.getEntity(packet.sourceId)) instanceof OwlEntity owl) {
                    owl.tailWagAnimation.start();
                    owl.tailWagAnimation.activeTimer = packet.duration;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}