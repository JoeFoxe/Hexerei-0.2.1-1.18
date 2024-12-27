package net.joefoxe.hexerei.event;

import net.joefoxe.hexerei.events.AnimalFatAdditionModifier;
import net.joefoxe.hexerei.events.SageSeedAdditionModifier;
import net.neoforged.bus.api.IEventBus;

public class ModLootModifiers {
    public static void init(IEventBus eventBus)
    {
        SageSeedAdditionModifier.init(eventBus);
        AnimalFatAdditionModifier.init(eventBus);
    }
}
