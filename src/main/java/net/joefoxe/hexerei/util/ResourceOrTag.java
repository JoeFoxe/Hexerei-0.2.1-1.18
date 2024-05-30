package net.joefoxe.hexerei.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.function.Predicate;

public class ResourceOrTag<K> {
    private final Either<ResourceKey<K>, TagKey<K>> either;

    private ResourceOrTag(Either<ResourceKey<K>, TagKey<K>> either) {
        this.either = either;
    }

    public static <K> ResourceOrTag<K> get(String string, ResourceKey<Registry<K>> registry) {

        if (string.startsWith("#")) {
            // if string is a tag key remove pound
            String str = string.substring(1);
            ResourceLocation loc = new ResourceLocation(str);
            return new ResourceOrTag<>(Either.right(TagKey.create(registry, loc)));
        }

        ResourceLocation loc = new ResourceLocation(string);
        return new ResourceOrTag<>(Either.left(ResourceKey.create(registry, loc)));
    }

    public Predicate<Holder<K>> holderPredicate() {
        return either.map(resourceKey -> holder -> holder.is(resourceKey), tagKey -> holder -> holder.is(tagKey));
    }
}
