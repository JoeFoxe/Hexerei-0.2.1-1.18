package net.joefoxe.hexerei.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static net.joefoxe.hexerei.util.HexereiUtil.moveTo;


public class CrystalBallTile extends BlockEntity {

    public float degreesSpun;
    public float degreesSpunOld;
    public float orbOffset;
//    public float smallRingOffset;
//    public float largeRingOffset;
//    public float largeRingOffsetO;
//    public float largeRingOffsetIncrement;
    public float moonAlpha;
    public float centerYaw;
    public float centerYawO;
    public float centerYawIncrement;
    public float centerPitch;
    public float centerPitchO;
    public float centerPitchIncrement;
    public long lastInteractedWith;
    public Player nearestPlayer;
    public long lastLocatedNearestPlayer;

    public CrystalBallTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);

        orbOffset = 0;
        degreesSpun = 0;
        degreesSpunOld = 0;
        moonAlpha = 0;
        centerYaw = 0;
        centerYawO = 0;
        centerYawIncrement = 0;
        centerPitch = 0;
        centerPitchO = 0;
        centerPitchIncrement = 0;
        lastInteractedWith = 0;
        nearestPlayer = null;
    }

    public CrystalBallTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.CRYSTAL_BALL_TILE.get(),blockPos, blockState);
    }

    public static double getDistanceToEntity(Entity entity, BlockPos pos) {
        double deltaX = entity.position().x - pos.getX();
        double deltaY = entity.position().y - pos.getY();
        double deltaZ = entity.position().z - pos.getZ();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }

    public float updateIncrement(float currentAngle, float targetAngle, float lastIncrement) {
        // Normalize angles to the range -270 to 90
        targetAngle = normalizeAngle(targetAngle);
        currentAngle = normalizeAngle(currentAngle);

        float angleDifference = targetAngle - currentAngle;

        // Calculate the shortest direction
        if (angleDifference > 180) {
            angleDifference -= 360;
        } else if (angleDifference < -180) {
            angleDifference += 360;
        }
        float distance = Math.abs(angleDifference);

        if (Mth.abs(lastIncrement) < 0.3f && distance < 1f)
            return 0;

        return (lastIncrement + ((distance / 180f) * (distance / 180f) + 0.125f) * (angleDifference > 0 ? 1 : -1)) * 0.96f;
    }

    public float updateAngle(float currentAngle, float maxIncrement) {
        // Normalize angles to the range -270 to 90
        currentAngle = normalizeAngle(currentAngle);

        currentAngle += maxIncrement;

        return normalizeAngle(currentAngle);
    }

    private float normalizeAngle(float angle) {
        while (angle > 90) {
            angle -= 360;
        }
        while (angle < -270) {
            angle += 360;
        }
        return angle;
    }

    private float normalize(float angle) {
        while (angle > 360) {
            angle -= 360;
        }
        while (angle < 0) {
            angle += 360;
        }
        return angle;
    }

//    @Override
    public void tick() {
        this.degreesSpunOld = this.degreesSpun;
        centerPitchO = centerPitch;
        centerYawO = centerYaw;
        float currentTime = level.getGameTime();

        if (level.getGameTime() - lastLocatedNearestPlayer > 20L) {
            lastLocatedNearestPlayer = level.getGameTime();
            nearestPlayer = this.level.getNearestPlayer(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), 4D, false);
        }

        if (nearestPlayer != null && getDistanceToEntity(nearestPlayer, worldPosition) < 4.0f) {
            degreesSpun = normalize(degreesSpun + 0.5f);
            orbOffset = moveTo(orbOffset, (float) Math.sin(Math.PI * (currentTime) / 30) / 4f, 0.25f);

            moonAlpha = moveTo(moonAlpha, 1, 0.05f);

            Vec3 playerPos = nearestPlayer.position();
            double dx = playerPos.x - getBlockPos().getX() - 0.5f;
            double dy = (playerPos.y + nearestPlayer.getEyeHeight()) - getBlockPos().getY() - 0.5f;
            double dz = playerPos.z - getBlockPos().getZ() - 0.5f;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            float yaw = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90F; // Calculate yaw
            float pitch = (float) (-(Math.atan2(dy, distance) * (180 / Math.PI))); // Calculate pitch

            if (level.getGameTime() - lastInteractedWith > 20L) {
                centerYawIncrement = updateIncrement(centerYaw, yaw, centerYawIncrement);
                centerPitchIncrement = updateIncrement(centerPitch, pitch, centerPitchIncrement);
            }

            centerYaw = updateAngle(centerYaw, centerYawIncrement);
            centerPitch = updateAngle(centerPitch, centerPitchIncrement);

            centerPitch = moveTo(centerPitch, 0, Math.abs(centerYawIncrement / 10f));

        } else {

            orbOffset = moveTo(orbOffset, -1.0f, 0.1f);
            moonAlpha = moveTo(moonAlpha, 0, 0.05f);
        }

    }

    @Override
    public boolean triggerEvent(int pId, int pType) {
        if (pId == 1) {
            this.centerYawIncrement = Mth.clamp(this.centerYawIncrement + (this.centerYawIncrement > 0 ? 1 : -1) + (this.centerYawIncrement / 10), -100, 100);
            this.lastInteractedWith = this.level.getGameTime();
            return true;
        }
        return super.triggerEvent(pId, pType);
    }
}
