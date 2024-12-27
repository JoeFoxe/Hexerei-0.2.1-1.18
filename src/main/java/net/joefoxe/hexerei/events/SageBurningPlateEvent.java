package net.joefoxe.hexerei.events;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.SageBurningPlate;
import net.joefoxe.hexerei.config.HexConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class SageBurningPlateEvent {

    @SubscribeEvent
    public void onEntityJoin(MobSpawnEvent.SpawnPlacementCheck e) {
        Level world = e.getLevel().isClientSide() ? null : e.getLevel() instanceof Level ? (Level)e.getLevel() : null;

        if (world == null) {
            return;
        }


        if(e.getSpawnType() != MobSpawnType.NATURAL)
            return;

        if(HexConfig.SAGE_BURNING_PLATE_RANGE.get() == 0)
            return;

        boolean isHostile = e.getEntityType().getCategory().equals(MobCategory.MONSTER);

        if (!e.getEntityType().getCategory().equals(MobCategory.MONSTER))
            return;

//        if (entity.getTags().contains(Hexerei.MOD_ID + ".checked" )) {
//
//            return;
//        }
//        entity.addTag(Hexerei.MOD_ID + ".checked");
//
//        if (!HexereiUtil.entityIsHostile(entity)) {
//            return;
//        }

        List<BlockPos> nonSagePlatesInList = new ArrayList<>();

        if (Hexerei.sageBurningPlateTileList.isEmpty())
            return;

        BlockPos burning_plate = null;
        for (BlockPos nearbySageBurningPlate : Hexerei.sageBurningPlateTileList) {
            float dist = (float) Math.sqrt(e.getPos().distToCenterSqr(nearbySageBurningPlate.getCenter()));
            if (dist < HexConfig.SAGE_BURNING_PLATE_RANGE.get() + 1) {
                BlockState burning_platestate = world.getBlockState(nearbySageBurningPlate);
                Block block = burning_platestate.getBlock();

                if (!(block instanceof SageBurningPlate)) {
                    nonSagePlatesInList.add(nearbySageBurningPlate);
                    continue;
                }

                if (!burning_platestate.getValue(SageBurningPlate.LIT)) {
                    continue;
                }

                burning_plate = nearbySageBurningPlate.immutable();
                break;
            }
        }
        for(BlockPos nonSageBurninPlate : nonSagePlatesInList){
            Hexerei.sageBurningPlateTileList.remove(nonSageBurninPlate);
        }

        if (burning_plate == null) {
            return;
        }

//        List<Entity> passengers = entity.getPassengers();
//        if (passengers.size() > 0) {
//            for (Entity passenger : passengers) {
//                passenger.remove(RemovalReason.DISCARDED);
//            }
//        }

        e.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
    }

}