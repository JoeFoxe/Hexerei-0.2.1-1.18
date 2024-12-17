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

public class BrowAnimPacket {
    int sourceId;
    OwlEntity.BrowAnim browAnim;
    int duration;
    boolean happyAnim;

    public BrowAnimPacket(Entity entity, OwlEntity.BrowAnim browAnim, int duration) {
        this.sourceId = entity.getId();
        this.browAnim = browAnim;
        this.duration = duration;
        this.happyAnim = false;

    }

    public BrowAnimPacket(Entity entity, OwlEntity.BrowAnim browAnim, int duration, boolean happyAnim) {
        this.sourceId = entity.getId();
        this.browAnim = browAnim;
        this.duration = duration;
        this.happyAnim = happyAnim;

    }
    public BrowAnimPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.browAnim = buf.readEnum(OwlEntity.BrowAnim.class);
        this.duration = buf.readInt();
        this.happyAnim = buf.readBoolean();
    }

    public static void encode(BrowAnimPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeEnum(object.browAnim);
        buffer.writeInt(object.duration);
        buffer.writeBoolean(object.happyAnim);
    }

    public static BrowAnimPacket decode(FriendlyByteBuf buffer) {
        return new BrowAnimPacket(buffer);
    }

    public static void consume(BrowAnimPacket packet, Supplier<NetworkEvent.Context> ctx) {
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
                    if (packet.happyAnim) {
                        owl.browHappyAnimation.start();
                        owl.browHappyAnimation.activeTimer = packet.duration;
                        owl.browHappyAnimation.setBrowAnim(packet.browAnim);
                    } else {
                        owl.browAnimation.start();
                        owl.browAnimation.activeTimer = packet.duration;
                        owl.browAnimation.setBrowAnim(packet.browAnim);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}