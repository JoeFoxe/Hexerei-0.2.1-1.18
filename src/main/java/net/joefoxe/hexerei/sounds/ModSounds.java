package net.joefoxe.hexerei.sounds;

import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Hexerei.MOD_ID);

    public static DeferredHolder<SoundEvent, SoundEvent> OWL_HOOT = registerSoundEvent("owl_hoot");
    public static DeferredHolder<SoundEvent, SoundEvent> CROW_CAW = registerSoundEvent("crow_caw");

    public static DeferredHolder<SoundEvent, SoundEvent> CROW_FLUTE = registerSoundEvent("crow_flute");
    public static DeferredHolder<SoundEvent, SoundEvent> CROW_FLUTE_SELECT = registerSoundEvent("crow_flute_select");
    public static DeferredHolder<SoundEvent, SoundEvent> CROW_FLUTE_DESELECT = registerSoundEvent("crow_flute_deselect");

    public static DeferredHolder<SoundEvent, SoundEvent> BROOM_WHISTLE = registerSoundEvent("whistle");
    public static DeferredHolder<SoundEvent, SoundEvent> HOOTSIFER = registerSoundEvent("hootsifer");

    public static DeferredHolder<SoundEvent, SoundEvent> BOOK_TURN_PAGE_SLOW = registerSoundEvent("book_turn_page_slow");
    public static DeferredHolder<SoundEvent, SoundEvent> BOOK_TURN_PAGE_FAST = registerSoundEvent("book_turn_page_fast");
    public static DeferredHolder<SoundEvent, SoundEvent> BOOKMARK_BUTTON = registerSoundEvent("bookmark_button");
    public static DeferredHolder<SoundEvent, SoundEvent> BOOKMARK_SWAP = registerSoundEvent("bookmark_swap");
    public static DeferredHolder<SoundEvent, SoundEvent> BOOKMARK_DELETE = registerSoundEvent("bookmark_delete");

    public static DeferredHolder<SoundEvent, SoundEvent> BOOK_CLOSE = registerSoundEvent("book_close");
    public static DeferredHolder<SoundEvent, SoundEvent> BOOK_OPENING = registerSoundEvent("book_opening");


    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Hexerei.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}