package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

public class BookChapter {
    public ArrayList<BookPageEntry> pages;
    public String name;
    public int startPage;
    public int endPage;
    public int chapter;

    BookChapter(ArrayList<BookPageEntry> pages, String name, int startPage, int endPage, int chapter){
        this.pages = pages;
        this.name = name;
        this.startPage = startPage;
        this.endPage = endPage;
        this.chapter = chapter;
    }

    public static BookChapter deserialize(int chapter, JsonObject object, int numOfPages) {

        JsonArray yourJson = GsonHelper.getAsJsonArray(object,"pages");

        ArrayList<BookPageEntry> yourList = new ArrayList<>();
        for (int i = 0; i < yourJson.size(); i++) {
            JsonObject extraItemObject = yourJson.get(i).getAsJsonObject();
            yourList.add(BookPageEntry.deserialize(extraItemObject, numOfPages, i, chapter));
        }
        String string = GsonHelper.getAsString(object, "name", "empty");
        return new BookChapter(yourList, string, numOfPages, numOfPages + yourJson.size(), chapter);
    }
}
