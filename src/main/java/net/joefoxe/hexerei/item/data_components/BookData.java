package net.joefoxe.hexerei.item.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BookData {

    public static final BookData EMPTY = new BookData(0, 0, false, new Bookmarks(IntStream.range(0, 20) .mapToObj(index -> new Bookmarks.Slot("", DyeColor.WHITE, index)).collect(Collectors.toList())));

    int chapter;
    int page;
    boolean opened;
    Bookmarks bookmarks;

    public BookData(int chapter, int page, boolean opened, Bookmarks bookmarks) {
        this.chapter = chapter;
        this.page = page;
        this.opened = opened;
        this.bookmarks = bookmarks;
    }

    public static final Codec<BookData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                            Codec.INT.fieldOf("chapter").forGetter(BookData::getChapter),
                            Codec.INT.fieldOf("page").forGetter(BookData::getPage),
                            Codec.BOOL.fieldOf("opened").forGetter(BookData::isOpened),
                            Bookmarks.CODEC.fieldOf("bookmarks").forGetter(BookData::getBookmarks)
                    ).apply(instance, BookData::new)
    );


    public static final StreamCodec<ByteBuf, BookData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BookData::getChapter,
            ByteBufCodecs.INT, BookData::getPage,
            ByteBufCodecs.BOOL, BookData::isOpened,
            Bookmarks.STREAM_CODEC, BookData::getBookmarks,
            BookData::new
    );

    public int getChapter() {
        return chapter;
    }

    public int getPage() {
        return page;
    }

    public boolean isOpened() {
        return opened;
    }

    public Bookmarks getBookmarks() {
        return bookmarks;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public static class Bookmarks {

        List<Slot> slots;

        public static final Codec<Bookmarks> CODEC = Slot.CODEC.listOf().xmap(Bookmarks::new, contents -> contents.slots);

        public static final StreamCodec<ByteBuf, Bookmarks> STREAM_CODEC = Slot.STREAM_CODEC
                .apply(ByteBufCodecs.list())
                .map(Bookmarks::new, contents -> contents.slots);

        Bookmarks(List<Slot> slots) {
            this.slots = slots;
        }

        public List<Slot> getSlots() {
            return slots;
        }

        public Slot getSlot(int index) {
            return this.slots.get(index);
        }

        public void setSlots(List<Slot> slots) {
            this.slots = slots;
        }

        public void setSlot(int index, Slot slot) {
            this.slots.set(index, slot);
        }


        public static class Slot {

            String id;
            DyeColor color;
            int index;

            public static final Codec<Slot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                            Codec.STRING.fieldOf("id").forGetter(Slot::getId),
                            DyeColor.CODEC.fieldOf("color").forGetter(Slot::getColor),
                            Codec.INT.fieldOf("index").forGetter(Slot::getIndex)
                    ).apply(instance, Slot::new)
            );

            public Slot(String id, DyeColor color, int index) {
                this.id = id;
                this.color = color;
                this.index = index;
            }

            public String getId() {
                return id;
            }

            public DyeColor getColor() {
                return color;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setColor(DyeColor color) {
                this.color = color;
            }

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public static StreamCodec<ByteBuf, Slot> STREAM_CODEC = ByteBufCodecs.fromCodec(Slot.CODEC);

            public Slot copy(){
                return new Slot(this.id, this.color, this.index);
            }

            public Slot copyWithIndex(int index){
                return new Slot(this.id, this.color, index);
            }



        }

    }
}