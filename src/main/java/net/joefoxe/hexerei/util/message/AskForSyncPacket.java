package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class AskForSyncPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, AskForSyncPacket> CODEC  = StreamCodec.ofMember(AskForSyncPacket::encode, AskForSyncPacket::new);
    public static final Type<AskForSyncPacket> TYPE = new Type<>(HexereiUtil.getResource("ask_for_sync"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;

    public AskForSyncPacket(Entity entity) {
        this.sourceId = entity.getId();
    }

    public AskForSyncPacket(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if(player.level().getEntity(sourceId) instanceof CrowEntity crow) {
            crow.sync();
        }

        if(player.level().getEntity(sourceId) instanceof OwlEntity crow) {
            crow.sync();
        }
    }
}