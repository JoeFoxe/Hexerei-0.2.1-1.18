package net.joefoxe.hexerei.item;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.item.custom.CourierPackageItem;
import net.joefoxe.hexerei.item.custom.DowsingRodItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class ModItemProperties {

    public static double angleDifference( double angle1, double angle2 )
    {
        double diff = ( angle2 - angle1 + 180 ) % 360 - 180;
        return diff < -180 ? diff + 360 : diff;
    }

    public static void setup() {

        ItemProperties.register(ModItems.DOWSING_ROD.get(), new ResourceLocation("angle"), (ClampedItemPropertyFunction) (itemStack, level, p_174667_, p_174668_) -> {
            Entity entity = p_174667_ != null ? p_174667_ : itemStack.getEntityRepresentation();

            if (!(entity instanceof Player) || ((DowsingRodItem)itemStack.getItem()).nearestPos == null) {
                return 0.3F;
            }

            float viewRot = Mth.wrapDegrees(entity.getViewYRot(1.0f));
            float rotationFromPlayer = (float) (Math.atan2(((DowsingRodItem)itemStack.getItem()).nearestPos.getZ() - p_174667_.getZ() + 0.5f, ((DowsingRodItem)itemStack.getItem()).nearestPos.getX() - p_174667_.getX() + 0.5f) * 180 / Math.PI);
            if (Math.abs(angleDifference(Mth.wrapDegrees(viewRot + 90), rotationFromPlayer)) < 15) {
                return 0.0f;
            }else if (Math.abs(angleDifference(Mth.wrapDegrees(viewRot + 90), rotationFromPlayer)) < 45) {
                return 0.1f;
            }else if (Math.abs(angleDifference(Mth.wrapDegrees(viewRot + 90), rotationFromPlayer)) < 75) {
                return 0.2f;
            }
            return 0.3f;

        });

        ItemProperties.register(ModItems.COURIER_PACKAGE.get(), new ResourceLocation("open"), (ClampedItemPropertyFunction) (itemStack, level, p_174667_, p_174668_) -> {

            if (itemStack.hasTag()) {

                CompoundTag tag = itemStack.getOrCreateTag();
                if (tag.contains("BlockEntityTag") && tag.getCompound("BlockEntityTag").contains("Items") && !tag.getCompound("BlockEntityTag").getList("Items", Tag.TAG_COMPOUND).isEmpty()) {
                    if (tag.getCompound("BlockEntityTag").contains("Sealed") && tag.getCompound("BlockEntityTag").getBoolean("Sealed"))
                        return 0.0f;
                    return 0.5f;
                }
            }
            return 1.0f;

        });

        ItemProperties.register(ModItems.COURIER_LETTER.get(), new ResourceLocation("open"), (ClampedItemPropertyFunction) (itemStack, level, p_174667_, p_174668_) -> {

            if (itemStack.hasTag()) {
                CompoundTag tag = BlockItem.getBlockEntityData(itemStack);
                if (tag != null && tag.contains("Sealed") && tag.getBoolean("Sealed")) {
                    return 0.0f;
                }
            }
            return 1.0f;

        });
    }
}