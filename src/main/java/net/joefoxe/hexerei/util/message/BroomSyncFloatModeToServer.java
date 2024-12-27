package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class BroomSyncFloatModeToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BroomSyncFloatModeToServer> CODEC  = StreamCodec.ofMember(BroomSyncFloatModeToServer::encode, BroomSyncFloatModeToServer::new);
    public static final CustomPacketPayload.Type<BroomSyncFloatModeToServer> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("broom_sync_mode"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    boolean mode;

    public BroomSyncFloatModeToServer(int id, boolean mode) {
        this.sourceId = id;
        this.mode = mode;
    }

    public BroomSyncFloatModeToServer(RegistryFriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readBoolean());
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeBoolean(mode);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if(server.overworld().getEntity(sourceId) instanceof BroomEntity broom) {
            broom.setFloatMode(mode);
        }
    }
}