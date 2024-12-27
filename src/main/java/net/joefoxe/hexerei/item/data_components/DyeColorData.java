package net.joefoxe.hexerei.item.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

public record DyeColorData(DyeColor color) {

    public static final Codec<DyeColorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                            DyeColor.CODEC.fieldOf("color").forGetter(DyeColorData::color)
                    ).apply(instance, DyeColorData::new)
    );

    public static StreamCodec<ByteBuf, DyeColorData> STREAM_CODEC = ByteBufCodecs.fromCodec(DyeColorData.CODEC);
}