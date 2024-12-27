package net.joefoxe.hexerei.item;

import com.mojang.serialization.Codec;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.item.data_components.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ModDataComponents {


    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Hexerei.MOD_ID);


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>> CANDLE_DATA = register("candle_data", builder ->
            builder.persistent(CustomData.CODEC).networkSynchronized(CustomData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluteData>> FLUTE = register("flute", builder ->
            builder.persistent(FluteData.CODEC).networkSynchronized(FluteData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BookData>> BOOK = register("book", builder ->
            builder.persistent(BookData.CODEC).networkSynchronized(BookData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BookColorData>> BOOK_COLORS = register("book_colors", builder ->
            builder.persistent(BookColorData.CODEC).networkSynchronized(BookColorData.STREAM_CODEC));


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DyeColorData>> DYE_COLOR = register("dye_color", builder ->
            builder.persistent(DyeColorData.CODEC).networkSynchronized(DyeColorData.STREAM_CODEC));


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PotionBottleTypeData>> POTION_BOTTLE_TYPE = register("potion_bottle_type", builder ->
            builder.persistent(PotionBottleTypeData.CODEC).networkSynchronized(PotionBottleTypeData.STREAM_CODEC));


    static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return COMPONENTS.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }

    static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, Codec<T> codec) {
        return COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(ByteBufCodecs.fromCodec(codec)).build());
    }
}
