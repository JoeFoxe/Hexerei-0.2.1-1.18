package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EntitySyncAdditionalDataPacket {
    int sourceId;
    CompoundTag tag;

    public EntitySyncAdditionalDataPacket(Entity entity, CompoundTag tag) {
        this.sourceId = entity.getId();
        this.tag = tag;
    }
    public EntitySyncAdditionalDataPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.tag = buf.readNbt();
    }

    public static void encode(EntitySyncAdditionalDataPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeNbt(object.tag);
    }

    public static EntitySyncAdditionalDataPacket decode(FriendlyByteBuf buffer) {
        return new EntitySyncAdditionalDataPacket(buffer);
    }

    public static void consume(EntitySyncAdditionalDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
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
                crow.readAdditionalSaveDataNoSuper(packet.tag);
            }
            if(world.getEntity(packet.sourceId) instanceof OwlEntity owl) {
                owl.readAdditionalSaveDataNoSuper(packet.tag);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}