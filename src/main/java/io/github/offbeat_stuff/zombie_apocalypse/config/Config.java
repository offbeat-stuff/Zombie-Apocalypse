package io.github.offbeat_stuff.zombie_apocalypse.config;

import io.github.offbeat_stuff.zombie_apocalypse.ArmorTrimHandler;
import java.util.List;

public class Config {
  public boolean zombiesBurnInSunlight = false;
  public boolean doScream = true;

  public SpawnConfig Spawning = new SpawnConfig();

  public EquipmentConfig Equipment = new EquipmentConfig();

  public TrimConfig ArmorTrims = new TrimConfig();

  // Status effects for Zombie
  public StatusEffectConfig statusEffects = new StatusEffectConfig();

  public static class SpawnConfig {
    public boolean spawnInstantly = false;
    public boolean vanillaSpawnRestrictionOnFoot = true;
    public boolean checkIfBlockBelowAllowsSpawning = true;
    public InstantSpawning instantSpawning = new InstantSpawning();
    public int lightLevel = 15;

    public List<String> mobIds = List.of("zombie", "zombie_villager");
    public List<Integer> mobWeights = List.of(95, 5);
    public Variants variants = new Variants();

    // minimum distance from player
    public int minPlayerDistance = 16;

    // Max zombie count per player
    public int maxZombieCountPerPlayer = 150;

    // Chance that a zombie spawns in a single axis of player each tick
    public SpawnRange axisSpawn = new SpawnRange(0.1, 16, 48);

    // Chance that a zombie spawns in a single plane of player each tick
    public SpawnRange planeSpawn = new SpawnRange(0.1, 16, 48);

    // Chance that a zombie spawns in a box around player but not inside the
    // smaller box each tick
    public SpawnRange boxSpawn = new SpawnRange(0.1, 24, 64);

    // Time based Spawning in ticks - currently set to 0 to 1 am
    // each hour in minecraft represents 50 seconds or 1000 ticks
    public Range timeRange = new Range(1000, 13000);

    public List<String> allowedDimensions =
        List.of("overworld", "the_nether", "the_end");
  }

  public static class Variants {
    public double chance = 0.01;
    public int frostWeight = 1;
    public int flameWeight = 1;
  }

  public static class InstantSpawning {
    public int maxSpawnAttemptsPerTick = 100;
    public int maxSpawnsPerTick = 10;
  }

  public static class EquipmentConfig {
    public List<Double> armorChances = List.of(0.1, 0.1, 0.1, 0.1);
    public List<Integer> armorMaterialWeights =
        List.of(1, 10, 100, 50, 50, 100, 10);

    public List<Integer> weaponTypeWeights = List.of(100, 20, 20, 75, 1);
    public List<Integer> weaponMaterialWeights =
        List.of(1, 10, 100, 50, 50, 100);
    public double weaponChance = 0.1;

    // Enchantment levels for armor and weapons
    public Range enchantmentLevel = new Range(5, 40);
    public boolean treasureAllowed = true;
  }

  public static class TrimConfig {
    public List<String> materials = ArmorTrimHandler.vanillaMaterials;
    public List<Integer> materialWeights =
        List.of(100, 100, 1, 50, 100, 100, 100, 10, 100, 100);

    public List<String> patterns = ArmorTrimHandler.vanillaPatterns;
    public List<Integer> patternWeights =
        List.of(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);

    public boolean vanillaOnly = true;
    public double chance = 0.1;
  }

  public static class StatusEffectConfig {
    public List<String> ids =
        List.of("speed", "haste", "strength", "jump_boost", "regeneration",
                "resistance", "fire_resistance", "water_breathing",
                "invisibility", "health_boost", "absorption", "saturation",
                "slow_falling", "conduit_power", "dolphins_grace");

    public int maxTimeInTicks = -1;
    public List<Double> incrementalChances = List.of(0.1, 0.5, 0.8, 0.9);
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
    public double chance;
    public int min;
    public int max;

    public SpawnRange(double chance, int min, int max) {
      this.chance = chance;
      this.min = min;
      this.max = max;
    }
  }
}
