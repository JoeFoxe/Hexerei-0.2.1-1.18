package net.joefoxe.hexerei.integration.jei;

import net.neoforged.fml.ModList;

public class HexereiJeiCompat {
    public static boolean LOADED;

    public static void init() {
        LOADED = ModList.get().isLoaded("jei");
    }
}