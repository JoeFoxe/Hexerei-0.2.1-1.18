package net.joefoxe.hexerei.data.owl;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;

@EventBusSubscriber(modid = Hexerei.MOD_ID)
public class OwlLoadedChunksSavedData extends SavedData {
    protected static final String DATA_NAME = Hexerei.MOD_ID + "_owl_loaded_chunks";

    Map<ResourceKey<Level>, Map<ChunkPos, List<UUID>>> chunkData = new HashMap<>();
    Map<ResourceKey<Level>, Set<ChunkPos>> selfLoadedChunks = new HashMap<>();

    public OwlLoadedChunksSavedData addOwlLoading(ServerLevel level, OwlEntity owl, Set<ChunkPos> newChunks){
        Map<ResourceKey<Level>, Set<ChunkPos>> lastChunksMap = owl.messagingController.getLastCheckedChunks();
        if (!chunkData.containsKey(level.dimension()))
            chunkData.put(level.dimension(), new HashMap<>());
        for (ChunkPos chunkPos : newChunks) {
            List<UUID> list;
            if (chunkData.get(level.dimension()).containsKey(chunkPos)) {
                list = chunkData.get(level.dimension()).get(chunkPos);
            } else {
                list = new ArrayList<>();
            }
            if (!list.contains(owl.getUUID())) {
                list.add(owl.getUUID());
                chunkData.get(level.dimension()).put(chunkPos, list);
            }
        }
        lastChunksMap.forEach((key, lastChunks) -> {
            for(ChunkPos chunkPos : lastChunks) {
                if (!newChunks.contains(chunkPos) && key.equals(level.dimension())) {
                    //remove the uuid of this chunk, if its the last uuid then unload the chunk if we loaded it.
                    if(chunkData.get(key).containsKey(chunkPos)) {
                        List<UUID> list = chunkData.get(key).get(chunkPos);
                        list.remove(owl.getUUID());
                        if(list.isEmpty()) {
                            chunkData.get(key).remove(chunkPos);
                            //UN FORCELOAD CHUNK HERE
                            if (!selfLoadedChunks.containsKey(key))
                                selfLoadedChunks.put(key, new HashSet<>());
                            if (selfLoadedChunks.get(key).contains(chunkPos)) {
                                level.setChunkForced(chunkPos.x, chunkPos.z, false);
                                selfLoadedChunks.get(key).remove(chunkPos);
//                                System.out.println("Remove chunk loading at " + chunkPos + ", dimension: " + key.location());
                            }
                        }

                    }
                }
            }
        });

        this.setDirty();
        return this;
    }


    public void clearOwl(ServerLevel serverLevel, OwlEntity owl) {

        Map<ResourceKey<?>, Set<ChunkPos>> _to_remove = new HashMap<>();
        chunkData.forEach(((dimension, data) -> data.forEach(((chunkPos, uuids) -> {

            List<UUID> list = data.get(chunkPos);
            list.remove(owl.getUUID());
            if (list.isEmpty()) {
                if (!_to_remove.containsKey(dimension))
                    _to_remove.put(dimension, new HashSet<>());
                _to_remove.get(dimension).add(chunkPos);
                //UN FORCELOAD CHUNK HERE
                if (!selfLoadedChunks.containsKey(dimension))
                    selfLoadedChunks.put(dimension, new HashSet<>());
                if (selfLoadedChunks.get(dimension).contains(chunkPos)) {
                    ServerLevel level = serverLevel.getServer().getLevel(dimension);
                    if (level != null) {
                        level.setChunkForced(chunkPos.x, chunkPos.z, false);
//                        System.out.println("Remove2 chunk loading at " + chunkPos + ", dimension: " + level.dimension().location());
                    }
                    selfLoadedChunks.get(dimension).remove(chunkPos);
                }
            }
        }))));

        _to_remove.forEach((dim, posSet) -> {
            for(ChunkPos pos : posSet) {
                if (chunkData.containsKey(dim))
                    chunkData.get(dim).remove(pos);
            }
        });
        this.setDirty();

    }

    public void tick(ServerLevel serverLevel) {
        chunkData.forEach(((dimension, data) -> data.forEach(((chunkPos, uuids) -> {
            ServerLevel level = serverLevel.getServer().getLevel(dimension);
            if (level != null) {

                LongSet forcedChunks = level.getForcedChunks();
                if (!selfLoadedChunks.containsKey(dimension))
                    selfLoadedChunks.put(dimension, new HashSet<>());
                if (!selfLoadedChunks.get(dimension).contains(chunkPos) && !forcedChunks.contains(chunkPos.toLong())) {
                    level.setChunkForced(chunkPos.x, chunkPos.z, true);
                    this.selfLoadedChunks.get(dimension).add(chunkPos);
                    this.setDirty();
//                    System.out.println("Add chunk loading at " + chunkPos + ", dimension: " + dimension.location());
                }
            }
        }))));

//        System.out.println("ticking");
    }


    @SubscribeEvent
    public static void serverTickEvent(ServerTickEvent.Pre event) {
        OwlLoadedChunksSavedData.get(event.getServer().overworld()).tick(event.getServer().overworld());
    }

    private static OwlLoadedChunksSavedData create(CompoundTag tag, HolderLookup.Provider registries) {
        OwlLoadedChunksSavedData data = new OwlLoadedChunksSavedData();
        data.load(tag, registries);
        return data;
    }

    public void load(CompoundTag pCompoundTag, HolderLookup.Provider registries) {


        this.selfLoadedChunks.clear();
        if (pCompoundTag.contains("selfLoadedChunks")) {

            CompoundTag selfLoadedChunksTag = pCompoundTag.getCompound("selfLoadedChunks");
            for (String key : selfLoadedChunksTag.getAllKeys()) {
                ListTag chunkListTag = selfLoadedChunksTag.getList(key, 10);
                ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(key));
                Set<ChunkPos> chunkPosSet = new HashSet<>();

                for (int i = 0; i < chunkListTag.size(); i++) {
                    CompoundTag chunkTag = chunkListTag.getCompound(i);
                    int x = chunkTag.getInt("x");
                    int z = chunkTag.getInt("z");
                    chunkPosSet.add(new ChunkPos(x, z));
                }

                this.selfLoadedChunks.put(levelKey, chunkPosSet);
            }
        }

        CompoundTag dataTag = pCompoundTag.getCompound("chunkData");
        dataTag.getAllKeys().forEach(resourceKeyString -> {
            ResourceKey<Level> resourceKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(resourceKeyString));
            CompoundTag resourceTag = dataTag.getCompound(resourceKeyString);

            Map<ChunkPos, List<UUID>> chunkMap = new HashMap<>();
            resourceTag.getAllKeys().forEach(chunkPosString -> {
                CompoundTag chunkTag = resourceTag.getCompound(chunkPosString);
                int x = chunkTag.getInt("x");
                int z = chunkTag.getInt("z");
                ChunkPos chunkPos = new ChunkPos(x, z);

                ListTag uuidTagList = chunkTag.getList("UUIDs", 8);
                List<UUID> uuidList = new ArrayList<>();
                uuidTagList.forEach(uuidTag -> uuidList.add(UUID.fromString(uuidTag.getAsString())));

                chunkMap.put(chunkPos, uuidList);
            });

            chunkData.put(resourceKey, chunkMap);
        });
    }

    @Override
    public CompoundTag save(CompoundTag pCompoundTag, HolderLookup.Provider registries) {

        CompoundTag compoundTag = new CompoundTag();
        selfLoadedChunks.forEach((resourceKey, chunkSet) -> {
            ListTag chunkListTag = new ListTag();
            chunkSet.forEach(chunkPos -> {
                CompoundTag chunkNBT = new CompoundTag();
                chunkNBT.putInt("x", chunkPos.x);
                chunkNBT.putInt("z", chunkPos.z);
                chunkListTag.add(chunkNBT);
            });
            compoundTag.put(resourceKey.location().toString(), chunkListTag);
        });
        pCompoundTag.put("selfLoadedChunks", compoundTag);


        CompoundTag dataTag = new CompoundTag();
        chunkData.forEach((resourceKey, chunkMap) -> {
            CompoundTag resourceTag = new CompoundTag();
            chunkMap.forEach((chunkPos, uuidList) -> {
                CompoundTag chunkTag = new CompoundTag();
                chunkTag.putInt("x", chunkPos.x);
                chunkTag.putInt("z", chunkPos.z);

                ListTag uuidTagList = new ListTag();
                uuidList.forEach(uuid -> uuidTagList.add(StringTag.valueOf(uuid.toString())));
                chunkTag.put("UUIDs", uuidTagList);

                resourceTag.put(chunkPos.x + "," + chunkPos.z, chunkTag);
            });
            dataTag.put(resourceKey.location().toString(), resourceTag);
        });

        pCompoundTag.put("chunkData", dataTag);
        return pCompoundTag;
    }

    public static SavedData.Factory<OwlLoadedChunksSavedData> factory() {
        return new SavedData.Factory<>(OwlLoadedChunksSavedData::new, OwlLoadedChunksSavedData::create, null);
    }

    public static OwlLoadedChunksSavedData get(ServerLevel world) {
        return world.getServer().overworld()
                .getDataStorage().computeIfAbsent(factory(), DATA_NAME);
    }
    public static OwlLoadedChunksSavedData get() {
        return ServerLifecycleHooks.getCurrentServer().overworld()
                .getDataStorage().computeIfAbsent(factory(), DATA_NAME);
    }
}
