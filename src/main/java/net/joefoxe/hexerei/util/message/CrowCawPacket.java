package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CrowCawPacket {
    int sourceId;

    public CrowCawPacket(Entity entity) {
        this.sourceId = entity.getId();
    }
    public CrowCawPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
    }

    public static void encode(CrowCawPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
    }

    public static CrowCawPacket decode(FriendlyByteBuf buffer) {
        return new CrowCawPacket(buffer);
    }

    public static void consume(CrowCawPacket packet, Supplier<NetworkEvent.Context> ctx) {
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
                    crow.caw = true;
                    crow.cawTimer = 15;
                    crow.cawTiltAngle = 80;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}