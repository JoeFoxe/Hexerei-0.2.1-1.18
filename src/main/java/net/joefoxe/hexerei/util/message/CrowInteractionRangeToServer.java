package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class CrowInteractionRangeToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, CrowInteractionRangeToServer> CODEC  = StreamCodec.ofMember(CrowInteractionRangeToServer::encode, CrowInteractionRangeToServer::new);
    public static final Type<CrowInteractionRangeToServer> TYPE = new Type<>(HexereiUtil.getResource("crow_interaction_range_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    int range;

    public CrowInteractionRangeToServer(CrowEntity entity, int newRange) {
        this.sourceId = entity.getId();
        this.range = newRange;
        entity.interactionRange = newRange;
    }
    public CrowInteractionRangeToServer(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.range = buf.readInt();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeInt(range);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {

        if(player.level().getEntity(sourceId) instanceof CrowEntity crowEntity)
            crowEntity.interactionRange = range;
    }
}