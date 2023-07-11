package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.tryChance;
import static net.minecraft.util.math.MathHelper.clamp;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class PotionEffectHandler {

  private static List<StatusEffect> effects;
  private static List<Float> chances;
  private static int maxTimeInTicks;
  private static int maxAmplifier;

  public static void applyRandomPotionEffects(LivingEntity entity) {
    for (int i = 0; i < chances.size(); i++) {
      if (!tryChance(chances.get(i))) {
        return;
      }
      applyRandomPotionEffect(entity);
    }
  }

  private static StatusEffect getStatusEffect(String effect) {
    return Registries.STATUS_EFFECT.get(new Identifier(effect));
  }

  private static void applyRandomPotionEffect(LivingEntity entity) {
    var index = ZombieMod.XRANDOM.nextInt(effects.size());
    var effect = effects.get(index);
    var time = -1;
    if (maxTimeInTicks > -1) {
      maxTimeInTicks = ZombieMod.XRANDOM.nextInt(maxTimeInTicks);
    }
    entity.addStatusEffect(new StatusEffectInstance(
        effect, time, ZombieMod.XRANDOM.nextInt(maxAmplifier) + 1));
  }

  public static void handleRaw(RawStatusEffectHandler raw) {
    raw.fixAll();
    effects =
        raw.effects.stream().map(PotionEffectHandler::getStatusEffect).toList();
    chances = raw.incrementalChances;
    maxTimeInTicks = raw.maxTimeInTicks;
    maxAmplifier = raw.maxAmplifier;
  }

  public static class RawStatusEffectHandler {
    public List<String> effects = List.of();
    public int maxTimeInTicks;
    public List<Float> incrementalChances;
    public int maxAmplifier;

    public RawStatusEffectHandler(List<String> effects, int maxTimeInTicks,
                                  List<Float> incrementalChances,
                                  int maxAmplifier) {
      this.effects = effects;
      this.maxTimeInTicks = maxTimeInTicks;
      this.incrementalChances = incrementalChances;
      this.maxAmplifier = maxAmplifier;
    }

    private String fixStatusEffect(List<String> IDS, String effect) {
      if (effect.contains(":")) {
        effect = effect.split(":")[1];
      }
      effect = effect.trim().toLowerCase();
      for (int i = 0; i < IDS.size(); i++) {
        if (effect.startsWith(IDS.get(i))) {
          return IDS.get(i);
        }
      }
      return "";
    }

    public void fixAll() {
      var IDS = Registries.STATUS_EFFECT.getIds()
                    .stream()
                    .map(f -> f.getPath())
                    .toList();
      this.effects = this.effects.stream()
                         .map(f -> fixStatusEffect(IDS, f))
                         .filter(f -> !f.equals(""))
                         .toList();
      this.maxTimeInTicks = Math.max(this.maxTimeInTicks, -1);
      this.incrementalChances =
          this.incrementalChances.stream().map(f -> clamp(f, 0f, 1f)).toList();
      this.maxAmplifier = clamp(this.maxAmplifier, 0, 255);
    }
  }
}
