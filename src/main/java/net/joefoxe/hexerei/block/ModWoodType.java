package net.joefoxe.hexerei.block;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.List;

public class ModWoodType {
    public static WoodType WILLOW = WoodType.register(new WoodType("hexerei_willow", BlockSetType.OAK));
    public static WoodType POLISHED_WILLOW = WoodType.register(new WoodType("hexerei_polished_willow", BlockSetType.OAK));
    public static WoodType WITCH_HAZEL = WoodType.register(new WoodType("hexerei_witch_hazel", BlockSetType.OAK));
    public static WoodType POLISHED_WITCH_HAZEL = WoodType.register(new WoodType("hexerei_polished_witch_hazel", BlockSetType.OAK));
    public static WoodType MAHOGANY = WoodType.register(new WoodType("hexerei_mahogany", BlockSetType.OAK));
    public static WoodType POLISHED_MAHOGANY = WoodType.register(new WoodType("hexerei_polished_mahogany", BlockSetType.OAK));
    public static List<WoodType> woodTypes = List.of(WILLOW, MAHOGANY, WITCH_HAZEL, POLISHED_WILLOW, POLISHED_MAHOGANY, POLISHED_WITCH_HAZEL);
}