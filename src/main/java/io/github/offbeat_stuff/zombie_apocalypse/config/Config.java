package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.*;
import static io.github.offbeat_stuff.zombie_apocalypse.Utils.*;
import static io.github.offbeat_stuff.zombie_apocalypse.config.Common.*;

import io.github.offbeat_stuff.zombie_apocalypse.ArmorTrimHandler;
import io.github.offbeat_stuff.zombie_apocalypse.ArmorTrimHandler.RawTrimHandler;
import io.github.offbeat_stuff.zombie_apocalypse.PotionEffectHandler.RawStatusEffectHandler;
import io.github.offbeat_stuff.zombie_apocalypse.config.SpawnHandler.SpawnConfig;
import it.unimi.dsi.fastutil.floats.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;

public class Config {
  public boolean zombiesBurnInSunlight = false;
  public boolean doScream = true;

  public SpawnConfig Spawn = new SpawnConfig();

  public EquipmentConfig Equipment = new EquipmentConfig();

  public TrimConfig ArmorTrims = new TrimConfig();

  // Enchantment levels for armor and weapons
  public RangeConfig enchantmentLevel = new RangeConfig(5, 40);

  // Status effects for Zombie
  public StatusEffectConfig statusEffects = new StatusEffectConfig();

  public static class EquipmentConfig {
    public FloatList armorChances = FloatList.of(0.1f, 0.1f, 0.1f, 0.1f);
    public IntList armorMaterialWeights =
        IntList.of(1, 10, 100, 50, 50, 100, 10);

    public IntList weaponTypeWeights = IntList.of(100, 20, 20, 75, 1);
    public IntList weaponMaterialWeights = IntList.of(1, 10, 100, 50, 50, 100);
    public float weaponChance = 0.1f;

    public void validate() {
      armorChances = validate(armorChances);
      armorMaterialWeights = validate(armorMaterialWeights, 7);
      weaponTypeWeights = validate(weaponTypeWeights, 5);
      weaponMaterialWeights = validate(weaponMaterialWeights, 6);
      weaponChance = validate(weaponChance);
    }
  }

  public static class TrimConfig {
    public ObjectList<String> patterns = ArmorTrimHandler.vanillaPatterns;
    public IntList patternWeights =
        IntList.of(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);

    public ObjectList<String> materials = ArmorTrimHandler.vanillaMaterials;
    public IntList materialWeights =
        IntList.of(100, 100, 1, 50, 100, 100, 100, 10, 100, 100);

    public boolean vanillaOnly = true;
    public float chance = 0.1f;

    public void validate() {
      chance = validate(chance);

      if (vanillaOnly) {
        patterns = ObjectImmutableList.toList(patterns.stream().filter(
            f -> ArmorTrimHandler.vanillaPatterns.contains(f)));
        materials = ObjectImmutableList.toList(materials.stream().filter(
            f -> ArmorTrimHandler.vanillaMaterials.contains(f)));
      }

      patternWeights = validate(patternWeights, patterns.size());
      materialWeights = validate(materialWeights, materialWeights.size());
    }
  }

  public static class StatusEffectConfig {
    public ObjectList<String> ids = ObjectList.of(
        "speed", "haste", "strength", "jump_boost", "regeneration",
        "resistance", "fire_resistance", "water_breathing", "invisibility",
        "health_boost", "absorption", "saturation", "slow_falling",
        "conduit_power", "dolphins_grace");

    public int maxTimeInTicks = -1;
    public FloatList incrementalChances = FloatList.of(0.1f, 0.5f, 0.8f, 0.9f);
    public int maxAmplifier = 5;

    public void validate() {
      maxTimeInTicks = validate(maxTimeInTicks, -1);
      incrementalChances = validate(incrementalChances);
      maxAmplifier = validate(maxAmplifier);
    }
  }

  public static class RangeConfig {
    public int min;
    public int max;

    public Range(int min, int max) {
      this.min = min;
      this.max = max;
    }
  }
}
