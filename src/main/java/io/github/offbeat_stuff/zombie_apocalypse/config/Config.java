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

  public static class SpawnConfig {
    public boolean spawnInstantly = false;
    public boolean vanillaSpawnRestrictionOnFoot = true;
    public boolean checkIfBlockBelowAllowsSpawning = true;
    public InstantSpawning instantSpawning = new InstantSpawning();
    public int lightLevel = 15;

    public ObjectList<String> mobIds = List.of("zombie", "zombie_villager");
    public IntList mobWeights = IntList.of(95, 5);
    public Variants variants = new Variants();

    // minimum distance from player
    public float minPlayerDistance = 16f;

    // Max zombie count per player
    public int maxZombieCount = 150;

    // Chance that a zombie spawns in a single axis of player each tick
    public SpawnParameters axisSpawnParameters =
        new SpawnParameters(0.1f, 16, 48);

    // Chance that a zombie spawns in a single plane of player each tick
    public SpawnParameters planeSpawnParameters =
        new SpawnParameters(0.1f, 16, 48);

    // Chance that a zombie spawns in a box around player but not inside the
    // smaller box each tick
    public SpawnParameters boxSpawnParameters =
        new SpawnParameters(0.1f, 24, 64);

    // Time based Spawning in ticks - currently set to 0 to 1 am
    // each hour in minecraft represents 50 seconds or 1000 ticks
    public Range timeRange = new Range(1000, 13000);

    public List<String> allowedDimensions =
        List.of("overworld", "the_nether", "the_end");
  }

  public static class Variants {
    public float chance = 0.01f;
    public int frostWeight = 1;
    public int flameWeight = 1;
  }

  public static class InstantSpawning {
    public int maxSpawnAttemptsPerTick = 100;
    public int maxSpawnsPerTick = 10;
  }

  public static class EquipmentConfig {
    public FloatList armorChances = FloatList.of(0.1f, 0.1f, 0.1f, 0.1f);
    public IntList armorMaterialWeights =
        IntList.of(1, 10, 100, 50, 50, 100, 10);

    public IntList weaponTypeWeights = IntList.of(100, 20, 20, 75, 1);
    public IntList weaponMaterialWeights = IntList.of(1, 10, 100, 50, 50, 100);
    public float weaponChance = 0.1f;
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
  }

  public static class Range {
    public int min;
    public int max;

    public Range(int min, int max) {
      this.min = min;
      this.max = max;
    }
  }

  public static class SpawnRange {
    public float chance;
    public int min;
    public int max;

    public SpawnRange(float chance, int min, int max) {
      this.chance = chance;
      this.min = min;
      this.max = max;
    }
  }
}
