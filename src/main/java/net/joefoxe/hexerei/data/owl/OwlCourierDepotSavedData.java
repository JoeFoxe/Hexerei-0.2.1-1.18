package net.joefoxe.hexerei.data.owl;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.ClientboundOwlCourierDepotDataInventoryPacket;
import net.joefoxe.hexerei.util.message.ClientboundOwlCourierDepotDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;

@Mod.EventBusSubscriber(modid = Hexerei.MOD_ID)
public class OwlCourierDepotSavedData extends SavedData {
    protected static final String DATA_NAME = Hexerei.MOD_ID + "_owl_courier_depot";

    Map<GlobalPos, OwlCourierDepotData> depots = new HashMap<>();

    public OwlCourierDepotSavedData addOwlCourierDepot(String name, GlobalPos pos) {
        for (Map.Entry<GlobalPos, OwlCourierDepotData> entry : depots.entrySet()) {
            if (entry.getValue().name.equals(name)) {
//                System.out.println("Already contains same name - somehow slipped past the precheck");
                return this;
            }
            if (entry.getKey().equals(pos)) {
//                System.out.println("Already contains same block pos - somehow slipped past the precheck");
                return this;
            }
        }

        depots.put(pos, new OwlCourierDepotData(name));
        syncToClient();
        this.setDirty();
        return this;
    }

    public Map<GlobalPos, OwlCourierDepotData> getDepots() {
        return depots;
    }

    public void syncToClient() {
        HexereiPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ClientboundOwlCourierDepotDataPacket(save(new CompoundTag())));
    }

    public void syncInvToClient(GlobalPos pos) {
        HexereiPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ClientboundOwlCourierDepotDataInventoryPacket(invNbt(pos, new CompoundTag())));
    }

    public void clearOwlCourierDepot(GlobalPos pos) {
        for (Map.Entry<GlobalPos, OwlCourierDepotData> entry : depots.entrySet()) {
            GlobalPos depotPos = entry.getKey();
            if (pos.equals(depotPos)) {
                depots.remove(entry.getKey());
                //send packet to remove this pos for clients instead of syncing the whole
                this.setDirty();
                syncToClient();
                break;
            }
        }
    }

    public void tick(ServerLevel serverLevel) {
        // maybe check every once in a while if the depots are still existing, maybe some mod moved them somehow
    }


    @SubscribeEvent
    public static void serverTickEvent(TickEvent.ServerTickEvent event) {
//        if (event.phase == TickEvent.Phase.START)
//            OwlCourierDepotSavedData.get(event.getServer().overworld()).tick(event.getServer().overworld());
    }

    private static OwlCourierDepotSavedData create(CompoundTag tag) {
        OwlCourierDepotSavedData data = new OwlCourierDepotSavedData();
        data.load(tag);
        return data;
    }

    public void load(CompoundTag pCompoundTag) {

        if (pCompoundTag.contains("depots")) {
            ListTag depotList = pCompoundTag.getList("depots", Tag.TAG_COMPOUND);

            for (int i = 0; i < depotList.size(); i++) {
                CompoundTag depotTag = depotList.getCompound(i);
                String depotName = depotTag.getString("DepotName");

                Optional<GlobalPos> pos = GlobalPos.CODEC.parse(NbtOps.INSTANCE, depotTag.get("Pos")).result();

                OwlCourierDepotData depotData = new OwlCourierDepotData(depotName);
                ContainerHelper.loadAllItems(depotTag, depotData.items);

                pos.ifPresent(globalPos -> depots.put(globalPos, depotData));
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {

        ListTag depotList = new ListTag();

        for (Map.Entry<GlobalPos, OwlCourierDepotData> entry : depots.entrySet()) {
            CompoundTag depotTag = new CompoundTag();
            depotTag.putString("DepotName", entry.getValue().name);

            Optional<Tag> tag = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).result();
            tag.ifPresent(value -> depotTag.put("Pos", value));


            ContainerHelper.saveAllItems(depotTag, entry.getValue().items);
            depotList.add(depotTag);
        }

        pCompoundTag.put("depots", depotList);
        return pCompoundTag;
    }

    public CompoundTag invNbt(GlobalPos pos, CompoundTag pCompoundTag) {

        Optional<Tag> tag = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).result();
        tag.ifPresent(value -> pCompoundTag.put("Pos", value));

        ContainerHelper.saveAllItems(pCompoundTag, depots.get(pos).items);

        return pCompoundTag;
    }

    public static OwlCourierDepotSavedData get(ServerLevel world) {
        return world.getServer().overworld()
                .getDataStorage().computeIfAbsent(OwlCourierDepotSavedData::create, OwlCourierDepotSavedData::new, DATA_NAME);
    }
    public static OwlCourierDepotSavedData get() {
        return ServerLifecycleHooks.getCurrentServer().overworld()
                .getDataStorage().computeIfAbsent(OwlCourierDepotSavedData::create, OwlCourierDepotSavedData::new, DATA_NAME);
    }
}
