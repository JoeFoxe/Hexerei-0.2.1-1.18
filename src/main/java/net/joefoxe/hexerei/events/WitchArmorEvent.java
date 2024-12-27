package net.joefoxe.hexerei.events;

import net.joefoxe.hexerei.item.custom.WitchArmorItem;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

//@EventBusSubscriber
public class WitchArmorEvent {

    @SubscribeEvent
    public void onLivingSetAttackTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Witch witch) {
            if (isEquippedBy(event.getNewAboutToBeSetTarget(), 2)) {
                event.setNewAboutToBeSetTarget(null);
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof Witch witch) {
            if (isEquippedBy(witch.getLastHurtByMob(), 2))
                witch.setLastHurtByMob(null);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingDamageEvent.Pre event) {

        if (event.getSource().is(DamageTypes.MAGIC)) {
            if (event.getSource().getEntity() instanceof LivingEntity livingEntity && isEquippedBy(livingEntity, 3)) {
                //increase magic damage dealt by 25%
                event.setNewDamage(event.getOriginalDamage() * 1.25f);

                //heal for 15% of magic damage dealt
                livingEntity.heal(event.getOriginalDamage() * 0.15f);
            }
            if (isEquippedBy(event.getEntity(), 2))

                //decrease magic damage taken by 50%
                event.setNewDamage(event.getOriginalDamage() / 2);
        }
    }

    private boolean isEquippedBy(LivingEntity entity, int numEquipCheck) {
        int numEquip = 0;
        if (entity == null)
            return false;
        for (var armorStack : entity.getArmorSlots()) {
            if (armorStack.getItem() instanceof WitchArmorItem)
                numEquip++;
        }

        return numEquip >= numEquipCheck;
    }
}
