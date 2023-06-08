package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.tryChance;

import java.util.List;

import io.github.offbeat_stuff.zombie_apocalypse.config.ConfigHandler;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.registry.Registries;

import static net.minecraft.util.math.MathHelper.clamp;

public class PotionEffectHandler {

  private static final StatusEffect[] statusEffectChoices = {
      StatusEffects.SPEED,           StatusEffects.HASTE,
      StatusEffects.STRENGTH,        StatusEffects.JUMP_BOOST,
      StatusEffects.REGENERATION,    StatusEffects.RESISTANCE,
      StatusEffects.FIRE_RESISTANCE, StatusEffects.WATER_BREATHING,
      StatusEffects.INVISIBILITY,    StatusEffects.HEALTH_BOOST,
      StatusEffects.ABSORPTION,      StatusEffects.SATURATION,
      StatusEffects.GLOWING,         StatusEffects.LEVITATION,
      StatusEffects.SLOW_FALLING,    StatusEffects.CONDUIT_POWER,
      StatusEffects.DOLPHINS_GRACE};

  public static void applyRandomPotionEffects(ZombieEntity entity) {
    if (!tryChance(ConfigHandler.firstChance)) {
      return;
    }
    applyRandomPotionEffect(entity);
    if (!tryChance(ConfigHandler.secondChance)) {
      return;
    }
    applyRandomPotionEffect(entity);
  }

  private static void applyRandomPotionEffect(ZombieEntity entity) {
    var effect = statusEffectChoices[ZombieMod.XRANDOM.nextInt(
        statusEffectChoices.length)];
    entity.addStatusEffect(new StatusEffectInstance(
        effect, ZombieMod.XRANDOM.nextInt(ConfigHandler.maxPotionTimeInTicks),
        ZombieMod.XRANDOM.nextInt(ConfigHandler.maxAmplifier) + 1));
  }

  public static class RawStatusEffectHandler {
    public List<String> effects = List.of();
    public int maxTimeInTicks;
    public List<Float> incrementalChances;

    public RawStatusEffectHandler(List<String> effects, int maxTimeInTicks,List<Float> incrementalChances) {
      this.effects = effects;
      this.maxTimeInTicks = maxTimeInTicks;
      this.incrementalChances = incrementalChances;
    }

    private String fixStatusEffect(List<String> IDS,String effect) {
      effect = effect.trim().toLowerCase();
      for (int i = 0;i < IDS.size();i++) {
        if (effect.startsWith(IDS.get(i)) {
          return IDS.get(i);
        }
      }
      return "";
    }

    public void fixAll() {
      var IDS = Registries.STATUS_EFFECT.getIds().stream().map(f -> f.getPath()).toList();
      this.effects = this.effects.stream().map(f -> fixStatusEffect(IDS,f)).filter(f -> !f.equals("")).toList();
      this.maxTimeInTicks = Math.max(this.maxTimeInTicks, -1);
      this.incrementalChances = this.incrementalChances.stream().map(f -> clamp(f, 0f, 1f)).toList();
    }
  }}
