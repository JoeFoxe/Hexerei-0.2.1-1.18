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

public class HeadTiltPacket {
    int sourceId;
    int duration;
    float xTilt;
    float zTilt;
    public HeadTiltPacket(Entity entity) {
        this.sourceId = entity.getId();
        this.duration = 15;
        this.xTilt = 0;
        this.zTilt = 0;
    }
    public HeadTiltPacket(Entity entity, int duration, float xTilt, float zTilt) {
        this.sourceId = entity.getId();
        this.duration = duration;
        this.xTilt = xTilt;
        this.zTilt = zTilt;
    }
    public HeadTiltPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.duration = buf.readInt();
        this.xTilt = buf.readFloat();
        this.zTilt = buf.readFloat();
    }

    public static void encode(HeadTiltPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeInt(object.duration);
        buffer.writeFloat(object.xTilt);
        buffer.writeFloat(object.zTilt);
    }

    public static HeadTiltPacket decode(FriendlyByteBuf buffer) {
        return new HeadTiltPacket(buffer);
    }

    public static void consume(HeadTiltPacket packet, Supplier<NetworkEvent.Context> ctx) {
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
                   crow.peck();
                }
                if((world.getEntity(packet.sourceId)) instanceof OwlEntity owl) {
                    owl.headTiltAnimation.start();
                    owl.headTiltAnimation.xTiltTarget = packet.xTilt;
                    owl.headTiltAnimation.zTiltTarget = packet.zTilt;
                    owl.headTiltAnimation.activeTimer = packet.duration;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}