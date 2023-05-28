package io.github.offbeat_stuff.zombie_apocalypse;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;

public class PotionEffectHandler {

    private static final StatusEffect[] statusEffectChoices = {
            StatusEffects.SPEED, StatusEffects.HASTE,
            StatusEffects.STRENGTH, StatusEffects.JUMP_BOOST,
            StatusEffects.REGENERATION, StatusEffects.RESISTANCE,
            StatusEffects.FIRE_RESISTANCE, StatusEffects.WATER_BREATHING,
            StatusEffects.INVISIBILITY, StatusEffects.HEALTH_BOOST,
            StatusEffects.ABSORPTION, StatusEffects.SATURATION, StatusEffects.GLOWING,
            StatusEffects.LEVITATION, StatusEffects.SLOW_FALLING, StatusEffects.CONDUIT_POWER,
            StatusEffects.DOLPHINS_GRACE
    };

    public static void applyRandomPotionEffects(ZombieEntity entity) {
        if (ZombieMod.XRANDOM.nextFloat() > ZombieMod.config.firstChance) {
            return;
        }
        applyRandomPotionEffect(entity);
        if (ZombieMod.XRANDOM.nextFloat() > ZombieMod.config.secondChance) {
            return;
        }
        applyRandomPotionEffect(entity);
    }

    private static void applyRandomPotionEffect(ZombieEntity entity) {
        var effect = statusEffectChoices[ZombieMod.XRANDOM.nextInt(statusEffectChoices.length)];
        entity.addStatusEffect(
                new StatusEffectInstance(effect, ZombieMod.XRANDOM.nextInt(ZombieMod.config.maxPotionTimeInTicks),
                        ZombieMod.XRANDOM.nextInt(ZombieMod.config.maxAmplifier) + 1));
    }
}
