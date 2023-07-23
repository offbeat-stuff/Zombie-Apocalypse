package io.github.offbeat_stuff.zombie_apocalypse.config;

import io.github.offbeat_stuff.zombie_apocalypse.ArmorHandler;
import io.github.offbeat_stuff.zombie_apocalypse.ArmorTrimHandler;
import io.github.offbeat_stuff.zombie_apocalypse.PotionEffectHandler;
import io.github.offbeat_stuff.zombie_apocalypse.WeaponHandler;
import io.github.offbeat_stuff.zombie_apocalypse.config.Common.Range;
import io.github.offbeat_stuff.zombie_apocalypse.config.Config.SpawnConfig;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ConfigHandler {

  public static boolean zombiesBurnInSunlight;

  public static Predicate<Integer> isTimeRight;

  // Enchantment levels for armor and weapons
  public static Range enchantmentLevelRange = new Range(5, 40);

  public static List<Identifier> allowedDimensions;

  public static void handleConfig(Config config) {
    zombiesBurnInSunlight = config.zombiesBurnInSunlight;
    ScreamHandler.handle(config.doScream);

    ArmorHandler.handleArmorConfig(config.Armor);
    WeaponHandler.handleWeaponConfig(config.Weapon);
    PotionEffectHandler.handleRaw(config.statusEffects);
    ArmorTrimHandler.handleRawTrimHandler(config.ArmorTrims);

    SpawnHandler.handle(config.spawn);

    isTimeRight = config.spawn.timeRange.toModPredicate(24000);

    config.enchantmentLevelRange.min =
        Math.max(1, config.enchantmentLevelRange.min);
    config.enchantmentLevelRange.max =
        Math.max(1, config.enchantmentLevelRange.max);
    enchantmentLevelRange = config.enchantmentLevelRange;

    allowedDimensions = config.spawn.allowedDimensions.stream()
                            .map(f -> new Identifier(f))
                            .toList();
  }

  public static void correct(Config config) {
    max(config.enchantmentLevel, 0);
    correct(config.Spawning);
  }

  private static void correct(SpawnConfig conf) {
    conf.lightLevel = MathHelper.clamp(conf.lightLevel, 0, 15);
  }

  private static void max(Range v, int min) {
    v.min = Math.max(v.min, min);
    v.max = Math.max(v.max, min);
  }
}
