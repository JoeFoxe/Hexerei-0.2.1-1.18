package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.owl.ClientOwlCourierDepotData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundOwlCourierDepotDataPacket {
    CompoundTag tag;

    public CompoundTag getTag() {
        return tag;
    }

    public ClientboundOwlCourierDepotDataPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public static void encode(ClientboundOwlCourierDepotDataPacket object, FriendlyByteBuf buffer) {
        buffer.writeNbt(object.tag);
    }

    public static ClientboundOwlCourierDepotDataPacket decode(FriendlyByteBuf buffer) {
        return new ClientboundOwlCourierDepotDataPacket(buffer.readNbt());
    }

    public static void consume(ClientboundOwlCourierDepotDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level();
            }

            ClientOwlCourierDepotData.update(packet);

        });
        ctx.get().setPacketHandled(true);
    }
}