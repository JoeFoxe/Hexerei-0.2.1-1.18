package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class CofferSyncCrowButtonToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, CofferSyncCrowButtonToServer> CODEC  = StreamCodec.ofMember(CofferSyncCrowButtonToServer::encode, CofferSyncCrowButtonToServer::new);
    public static final Type<CofferSyncCrowButtonToServer> TYPE = new Type<>(HexereiUtil.getResource("coffer_sync_crow_button"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    BlockPos cofferTile;
    int toggled;

    public CofferSyncCrowButtonToServer(CofferTile cofferTile, int toggled) {
        this.cofferTile = cofferTile.getBlockPos();
        this.toggled = toggled;
    }
    public CofferSyncCrowButtonToServer(RegistryFriendlyByteBuf buf) {
        this.cofferTile = buf.readBlockPos();
        this.toggled = buf.readInt();

    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(cofferTile);
        buffer.writeInt(toggled);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if (player.level().getBlockEntity(cofferTile) instanceof CofferTile coffer)
            coffer.setButtonToggled(toggled);
    }
}