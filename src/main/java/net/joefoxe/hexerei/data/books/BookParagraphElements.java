package net.joefoxe.hexerei.data.books;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public class BookParagraphElements {
    public float x;
    public float y;
    public float height;
    public float width;
    public String verticalAlign;

    BookParagraphElements(float x, float y, float height, float width, String align){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.verticalAlign = align;
    }

    public static BookParagraphElements deserialize(JsonObject object) {
        float x = GsonHelper.getAsFloat(object, "x", 0);
        float y = GsonHelper.getAsFloat(object, "y", 0);
        float height = GsonHelper.getAsFloat(object, "height", 0);
        float width = GsonHelper.getAsFloat(object, "width", 0);
        String align = GsonHelper.getAsString(object, "verticalAlign", "top");
        return new BookParagraphElements(x, y, height, width, align);
    }


}
