package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class BookBookmarkDeleteToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BookBookmarkDeleteToServer> CODEC  = StreamCodec.ofMember(BookBookmarkDeleteToServer::encode, BookBookmarkDeleteToServer::new);
    public static final Type<BookBookmarkDeleteToServer> TYPE = new Type<>(HexereiUtil.getResource("book_bookmark_delete_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    BlockPos bookAltar;
    int slot;

    public BookBookmarkDeleteToServer(BookOfShadowsAltarTile bookAltar, int slot) {
        this.bookAltar = bookAltar.getBlockPos();
        this.slot = slot;
    }
    public BookBookmarkDeleteToServer(RegistryFriendlyByteBuf buf) {
        this.bookAltar = buf.readBlockPos();
        this.slot = buf.readInt();

    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(bookAltar);
        buffer.writeInt(slot);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if (player.level().getBlockEntity(bookAltar) instanceof  BookOfShadowsAltarTile book)
            book.deleteBookmark(slot);
    }

}