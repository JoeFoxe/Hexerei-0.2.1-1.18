package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class BroomDamageBrushToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BroomDamageBrushToServer> CODEC  = StreamCodec.ofMember(BroomDamageBrushToServer::encode, BroomDamageBrushToServer::new);
    public static final CustomPacketPayload.Type<BroomDamageBrushToServer> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("broom_damage_brush_server"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;

    public BroomDamageBrushToServer(int id) {
        this.sourceId = id;
    }

    public BroomDamageBrushToServer(RegistryFriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if(server.overworld().getEntity(sourceId) instanceof BroomEntity broom) {
            broom.damageBrush();
        }
    }
}