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

public class BookBookmarkPageToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BookBookmarkPageToServer> CODEC  = StreamCodec.ofMember(BookBookmarkPageToServer::encode, BookBookmarkPageToServer::new);
    public static final Type<BookBookmarkPageToServer> TYPE = new Type<>(HexereiUtil.getResource("book_bookmark_page_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    BlockPos bookAltar;
    int chapter;
    int page;

    public BookBookmarkPageToServer(BookOfShadowsAltarTile bookAltar, int chapter, int page) {
        this.bookAltar = bookAltar.getBlockPos();
        this.chapter = chapter;
        this.page = page;
    }
    public BookBookmarkPageToServer(RegistryFriendlyByteBuf buf) {
        this.bookAltar = buf.readBlockPos();
        this.chapter = buf.readInt();
        this.page = buf.readInt();

    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(bookAltar);
        buffer.writeInt(chapter);
        buffer.writeInt(page);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if (player.level().getBlockEntity(bookAltar) instanceof  BookOfShadowsAltarTile book)
            book.clickPageBookmark(chapter, page);
    }
}