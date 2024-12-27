package net.joefoxe.hexerei.item.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record FluteData(int commandSelected, int helpCommandSelected, int commandMode, List<CrowIds> crowList, int dyeColor1, int dyeColor2) {

    public static final FluteData EMPTY = new FluteData(0, 0, 0, new ArrayList<>(), 0, 0);

    public static FluteData empty() {
        return new FluteData(0, 0, 0, new ArrayList<>(), 0, 0);
    }

    public static final Codec<FluteData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                            Codec.INT.fieldOf("commandSelected").forGetter(FluteData::commandSelected),
                            Codec.INT.fieldOf("helpCommandSelected").forGetter(FluteData::helpCommandSelected),
                            Codec.INT.fieldOf("commandMode").forGetter(FluteData::commandMode),
                            CrowIds.CODEC.listOf().fieldOf("crowList").forGetter(FluteData::crowList),
                            Codec.INT.fieldOf("dyeColor1").forGetter(FluteData::dyeColor1),
                            Codec.INT.fieldOf("dyeColor2").forGetter(FluteData::dyeColor2)
                    ).apply(instance, FluteData::new)
    );

    public static StreamCodec<ByteBuf, FluteData> STREAM_CODEC = ByteBufCodecs.fromCodec(FluteData.CODEC);

    public record CrowIds(UUID uuid, int id) {

        public static final Codec<CrowIds> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        UUIDUtil.CODEC.fieldOf("uuid").forGetter(CrowIds::uuid),
                        Codec.INT.fieldOf("id").forGetter(CrowIds::id)
                ).apply(instance, CrowIds::new)
        );
    }
}