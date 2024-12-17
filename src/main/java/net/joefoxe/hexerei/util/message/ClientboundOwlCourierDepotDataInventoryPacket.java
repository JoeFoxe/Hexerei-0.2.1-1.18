package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.data.owl.ClientOwlCourierDepotData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundOwlCourierDepotDataInventoryPacket {
    CompoundTag tag;

    public CompoundTag getTag() {
        return tag;
    }

    public ClientboundOwlCourierDepotDataInventoryPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public static void encode(ClientboundOwlCourierDepotDataInventoryPacket object, FriendlyByteBuf buffer) {
        buffer.writeNbt(object.tag);
    }

    public static ClientboundOwlCourierDepotDataInventoryPacket decode(FriendlyByteBuf buffer) {
        return new ClientboundOwlCourierDepotDataInventoryPacket(buffer.readNbt());
    }

    public static void consume(ClientboundOwlCourierDepotDataInventoryPacket packet, Supplier<NetworkEvent.Context> ctx) {
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