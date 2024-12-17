package net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl;

import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.nbt.CompoundTag;

// Quirk interface
public interface Quirk {
    default void tick(OwlEntity owl){
        if (owl.level().isClientSide)
            clientTick(owl);
        else
            serverTick(owl);
    }
    void clientTick(OwlEntity owl);
    void serverTick(OwlEntity owl);
    String getName();
    void write(CompoundTag compound);
    void read(CompoundTag compound);
}
