package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public class BookPageEntry {
    public String location;
    public int pageNum;
    public int chapterNum;
    public int chapterPageNum;

    BookPageEntry(String location, int pageNum, int chapterNum, int chapterPageNum){
        this.location = location;
        this.pageNum = pageNum;
        this.chapterNum = chapterNum;
        this.chapterPageNum = chapterPageNum;
    }

    public static BookPageEntry deserialize(JsonObject object, int pageNumStartOfChapter, int chapterPageNum, int chapterNum) {

        String string = GsonHelper.getAsString(object, "page_location", "hexerei:book/book_pages/gui_page_1");
        return new BookPageEntry(string, pageNumStartOfChapter + chapterPageNum, chapterNum, chapterPageNum);
    }
}
