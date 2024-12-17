package net.joefoxe.hexerei.events;

import net.joefoxe.hexerei.item.custom.WitchArmorItem;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class WitchArmorEvent {

    @SubscribeEvent
    public void onLivingSetAttackTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Witch witch) {
            if (isEquippedBy(event.getNewTarget(), 2)) {
                event.setNewTarget(null);
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Witch witch) {
            if (isEquippedBy(witch.getLastHurtByMob(), 2))
                witch.setLastHurtByMob(null);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {

        if (event.getSource().is(DamageTypes.MAGIC)) {
            if (event.getSource().getEntity() instanceof LivingEntity livingEntity && isEquippedBy(livingEntity, 3)) {
                //increase magic damage dealt by 25%
                event.setAmount(event.getAmount() * 1.25f);

                //heal for 15% of magic damage dealt
                livingEntity.heal(event.getAmount() * 0.15f);
            }
            if (isEquippedBy(event.getEntity(), 2))

                //decrease magic damage taken by 50%
                event.setAmount(event.getAmount() / 2);
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
