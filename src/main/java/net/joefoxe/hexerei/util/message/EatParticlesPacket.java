package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EatParticlesPacket {
    int sourceId;
    ItemStack stack;

    public EatParticlesPacket(Entity entity, ItemStack stack) {
        this.sourceId = entity.getId();
        this.stack = stack.copy();
    }
    public EatParticlesPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.stack = buf.readItem().copy();
    }

    public static void encode(EatParticlesPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
        buffer.writeItemStack(object.stack, false);
    }

    public static EatParticlesPacket decode(FriendlyByteBuf buffer) {
        return new EatParticlesPacket(buffer);
    }

    public static void consume(EatParticlesPacket packet, Supplier<NetworkEvent.Context> ctx) {
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
                    crow.eatParticles(packet.stack);
                }
                if((world.getEntity(packet.sourceId)) instanceof OwlEntity owl) {
                    owl.eatParticles(packet.stack);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}