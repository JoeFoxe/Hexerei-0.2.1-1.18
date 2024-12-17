package net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl;


import net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl.quirks.FavoriteBlockQuirk;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class QuirkRegistry {
    private static final Map<String, Class<? extends Quirk>> QUIRKS = new HashMap<>();

    static {
        // Register each quirk with its name and class
        QUIRKS.put("FavoriteBlockQuirk", FavoriteBlockQuirk.class);
        // Add more quirks here...
    }

    public static Quirk getQuirkByName(String name) {
        Class<? extends Quirk> quirkClass = QUIRKS.get(name);
        if (quirkClass != null) {
            try {
                // Create a new instance of the quirk using its default constructor
                return quirkClass.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                // Handle exceptions
                e.printStackTrace();
            }
        }
        return null;
    }
}