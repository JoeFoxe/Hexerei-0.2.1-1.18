package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EmotionPacket {
    int sourceId;
    int packedEmotionScales;

    public EmotionPacket(Entity entity, int packedEmotionScales) {
        this.sourceId = entity.getId();
        this.packedEmotionScales = packedEmotionScales;

    }
    public EmotionPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.packedEmotionScales = buf.readInt();
    }

    public static void encode(EmotionPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeInt(object.packedEmotionScales);
    }

    public static EmotionPacket decode(FriendlyByteBuf buffer) {
        return new EmotionPacket(buffer);
    }

    public static void consume(EmotionPacket packet, Supplier<NetworkEvent.Context> ctx) {
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
                    int packedEmotionScales = packet.packedEmotionScales;
                    int happiness = (packedEmotionScales >> 16) & 0xFF;
                    int distressed = (packedEmotionScales >> 8) & 0xFF;
                    int anger = packedEmotionScales & 0xFF;

                    owl.emotions = new OwlEntity.Emotions(anger, distressed, happiness);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}