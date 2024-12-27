package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.data.books.BookPage;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class BookPagesPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BookPagesPacket> CODEC  = StreamCodec.ofMember(BookPagesPacket::encode, BookPagesPacket::new);
    public static final Type<BookPagesPacket> TYPE = new Type<>(HexereiUtil.getResource("book_pages"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    protected final Map<ResourceLocation, BookPage> bookPages;

    public BookPagesPacket(final Map<ResourceLocation, BookPage> bookPages) {
        this.bookPages = bookPages;
    }
    public BookPagesPacket(RegistryFriendlyByteBuf buf) {
        int size = buf.readInt();
        this.bookPages = new HashMap<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation name = buf.readResourceLocation();
            CompoundTag tag = buf.readNbt();
            if (tag != null) {
                BookPage bookPage = BookPage.loadFromTag(tag);
                bookPages.put(name, bookPage);
            }
        }
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(bookPages.size());
        for (var entry : bookPages.entrySet()) {
            buffer.writeResourceLocation(entry.getKey());
            buffer.writeNbt(BookPage.saveToTag(entry.getValue()));
        }
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        BookManager.clearBookPages();
        bookPages.keySet().forEach(k -> {
            BookPage bookPage = bookPages.get(k);
            BookManager.addBookPage(k, bookPage);
        });
    }
}