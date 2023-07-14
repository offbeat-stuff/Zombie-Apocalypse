package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.config.Common.*;
import static net.minecraft.util.math.MathHelper.clamp;

import io.github.offbeat_stuff.zombie_apocalypse.ArmorTrimHandler;
import io.github.offbeat_stuff.zombie_apocalypse.PotionEffectHandler;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;

public class ConfigHandler {

  public static boolean zombiesBurnInSunlight;

  public static float frostZombieChance = 0.01f;
  public static float flameZombieChance = 0.01f;

  public static Predicate<Integer> isTimeRight;

  // Enchantment levels for armor and weapons
  public static Range enchantmentLevelRange = new Range(5, 40);

  public static List<Identifier> allowedDimensions;

  public static void handleConfig(Config config) {
    zombiesBurnInSunlight = config.zombiesBurnInSunlight;
    ScreamHandler.handle(config.doScream);

    frostZombieChance = clamp(config.frostZombieChance, 0f, 1f);
    flameZombieChance = clamp(config.flameZombieChance, 0f, 1f);

    ArmorHandler.handleRawArmorHandler(config.Armor);
    WeaponHandler.handleRawWeaponHandler(config.Weapon);
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
}
