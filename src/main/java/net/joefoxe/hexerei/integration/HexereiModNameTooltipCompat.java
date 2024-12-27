package net.joefoxe.hexerei.integration;


import net.neoforged.fml.ModList;

public class HexereiModNameTooltipCompat {
    public static boolean LOADED;

    public static void init() {
        LOADED = ModList.get().isLoaded("modnametooltip");
    }
}
