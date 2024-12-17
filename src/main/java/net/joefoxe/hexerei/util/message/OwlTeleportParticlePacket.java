package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.client.renderer.entity.render.OwlVariant;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OwlTeleportParticlePacket {
    Vec3 pos;
    OwlVariant owlVariant;
    ResourceKey<Level> dimension;

    public OwlTeleportParticlePacket(ResourceKey<Level> dimension, Vec3 pos, OwlVariant owlVariant) {
        this.pos = pos;
        this.owlVariant = owlVariant;
        this.dimension = dimension;
    }

    public static void encode(OwlTeleportParticlePacket object, FriendlyByteBuf buffer) {
        buffer.writeResourceKey(object.dimension);
        buffer.writeDouble(object.pos.x);
        buffer.writeDouble(object.pos.y);
        buffer.writeDouble(object.pos.z);
        buffer.writeInt(object.owlVariant.getId());
    }

    public static OwlTeleportParticlePacket decode(FriendlyByteBuf buffer) {
        return new OwlTeleportParticlePacket(buffer.readResourceKey(Registries.DIMENSION), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), OwlVariant.byId(buffer.readInt()));
    }

    public static void consume(OwlTeleportParticlePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level();
            }

            if (world.dimension().equals(packet.dimension))
                OwlEntity.teleportParticles(world, packet.pos, packet.owlVariant);

        });
        ctx.get().setPacketHandled(true);
    }
}