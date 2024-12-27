package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class CrowSyncCommandToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, CrowSyncCommandToServer> CODEC  = StreamCodec.ofMember(CrowSyncCommandToServer::encode, CrowSyncCommandToServer::new);
    public static final Type<CrowSyncCommandToServer> TYPE = new Type<>(HexereiUtil.getResource("crow_sync_command_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    int command;

    public CrowSyncCommandToServer(Entity entity, int tag) {
        this.sourceId = entity.getId();
        this.command = tag;
    }
    public CrowSyncCommandToServer(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.command = buf.readInt();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeInt(command);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if(player.level().getEntity(sourceId) instanceof CrowEntity crowEntity)
            crowEntity.setCommand(command);
    }
}