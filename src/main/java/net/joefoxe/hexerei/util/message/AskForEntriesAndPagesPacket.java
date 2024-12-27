package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class AskForEntriesAndPagesPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, AskForEntriesAndPagesPacket> CODEC  = StreamCodec.ofMember(AskForEntriesAndPagesPacket::encode, AskForEntriesAndPagesPacket::new);
    public static final Type<AskForEntriesAndPagesPacket> TYPE = new Type<>(HexereiUtil.getResource("ask_for_book_entries"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public AskForEntriesAndPagesPacket() {
    }

    public AskForEntriesAndPagesPacket(RegistryFriendlyByteBuf buf) {
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {

        BookManager.sendBookEntriesToClient(player);
        BookManager.sendBookPagesToClient(player);
    }
}