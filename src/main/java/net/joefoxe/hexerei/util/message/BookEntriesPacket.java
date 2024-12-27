package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.books.BookEntries;
import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class BookEntriesPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BookEntriesPacket> CODEC  = StreamCodec.ofMember(BookEntriesPacket::encode, BookEntriesPacket::new);
    public static final Type<BookEntriesPacket> TYPE = new Type<>(HexereiUtil.getResource("book_entries"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    protected BookEntries bookEntries;

    public BookEntriesPacket(final BookEntries bookEntries) {
        this.bookEntries = bookEntries;
    }
    public BookEntriesPacket(RegistryFriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        if (tag != null) {
            this.bookEntries = BookEntries.loadFromTag(tag);
        }
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeNbt(BookEntries.saveToTag(bookEntries));
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        BookManager.clearBookEntries();
        BookManager.addBookEntries(bookEntries);
    }
}