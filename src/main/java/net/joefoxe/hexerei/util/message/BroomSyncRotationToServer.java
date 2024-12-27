package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class BroomSyncRotationToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BroomSyncRotationToServer> CODEC  = StreamCodec.ofMember(BroomSyncRotationToServer::encode, BroomSyncRotationToServer::new);
    public static final CustomPacketPayload.Type<BroomSyncRotationToServer> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("broom_sync_rot_server"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    float rotation;

    public BroomSyncRotationToServer(int id, float rotation) {
        this.sourceId = id;
        this.rotation = rotation;
    }

    public BroomSyncRotationToServer(RegistryFriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readFloat());
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeFloat(rotation);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if(player.level().getEntity(sourceId) instanceof BroomEntity broom) {
            broom.setRotation(rotation);
        }
    }
}