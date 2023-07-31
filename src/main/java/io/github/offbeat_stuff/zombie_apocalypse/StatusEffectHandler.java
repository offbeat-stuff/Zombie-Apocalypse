package io.github.offbeat_stuff.zombie_apocalypse;

import io.github.offbeat_stuff.zombie_apocalypse.config.Config.StatusEffectConfig;
import it.unimi.dsi.fastutil.doubles.DoubleImmutableList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

public class StatusEffectHandler {

  private static ObjectList<StatusEffect> effects;
  private static DoubleList chances;
  private static int maxTimeInTicks;
  private static int maxAmplifier;

  public static void applyRandomPotionEffects(LivingEntity entity) {
    for (var chance : chances) {
      if (!(Utils.roll(chance))) {
        return;
      }
      applyRandomPotionEffect(entity);
    }
  }

  private static void applyRandomPotionEffect(LivingEntity entity) {
    var index = ZombieMod.XRANDOM.nextInt(effects.size());
    var effect = effects.get(index);
    var time = -1;
    if (maxTimeInTicks > -1) {
      maxTimeInTicks = ZombieMod.XRANDOM.nextInt(maxTimeInTicks);
    }
    entity.method_2654(new StatusEffectInstance(
        effect, time, ZombieMod.XRANDOM.nextInt(maxAmplifier) + 1));
  }

  public static void load(StatusEffectConfig conf) {
    effects = conf.ids.stream()
                  .map(VersionDependent::getStatusEffect)
                  .filter(f -> f != null)
                  .collect(ObjectImmutableList.toList());
    chances = new DoubleImmutableList(conf.incrementalChances);
    maxTimeInTicks = conf.maxTimeInTicks;
    maxAmplifier = conf.maxAmplifier;
  }
}
