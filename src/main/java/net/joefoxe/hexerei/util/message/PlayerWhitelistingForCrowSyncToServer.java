package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.events.CrowWhitelistEvent;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PlayerWhitelistingForCrowSyncToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerWhitelistingForCrowSyncToServer> CODEC  = StreamCodec.ofMember(PlayerWhitelistingForCrowSyncToServer::encode, PlayerWhitelistingForCrowSyncToServer::new);
    public static final Type<PlayerWhitelistingForCrowSyncToServer> TYPE = new Type<>(HexereiUtil.getResource("player_whitelisting_crow_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    boolean whitelisting;

    public PlayerWhitelistingForCrowSyncToServer(boolean whitelisting) {
        this.whitelisting = whitelisting;
    }
    public PlayerWhitelistingForCrowSyncToServer(RegistryFriendlyByteBuf buf) {
        this.whitelisting = buf.readBoolean();
    }

    public static void encode(PlayerWhitelistingForCrowSyncToServer object, FriendlyByteBuf buffer) {
        buffer.writeBoolean(object.whitelisting);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if(whitelisting)
            CrowWhitelistEvent.playersActivelyWhitelisting.add(player);
        else
            CrowWhitelistEvent.playersActivelyWhitelisting.remove(player);
    }
}