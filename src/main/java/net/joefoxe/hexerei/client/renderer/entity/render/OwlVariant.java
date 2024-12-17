package net.joefoxe.hexerei.client.renderer.entity.render;

import java.util.Arrays;
import java.util.Comparator;

public enum OwlVariant {
    GREAT_HORNED(0),
    BARN(1),
    BARRED(2),
    SNOWY(3);

    private static final OwlVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(OwlVariant::getId)).toArray(OwlVariant[]::new);
    private final int id;

    private OwlVariant(int pId) {
        this.id = pId;
    }

    public int getId() {
        return this.id;
    }

    public static OwlVariant byId(int pId) {
        return BY_ID[pId % BY_ID.length];
    }
}
