package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.*;

import io.github.offbeat_stuff.zombie_apocalypse.ArmorTrimHandler;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectList;

public class Config {
  public boolean zombiesBurnInSunlight = false;
  public boolean doScream = true;

  public SpawnConfig Spawning = new SpawnConfig();

  public EquipmentConfig Equipment = new EquipmentConfig();

  public TrimConfig ArmorTrims = new TrimConfig();

  // Enchantment levels for armor and weapons
  public Range enchantmentLevel = new Range(5, 40);

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
    public double minPlayerDistance = 16;

    // Max zombie count per player
    public int maxZombieCountPerPlayer = 150;

    // Chance that a zombie spawns in a single axis of player each tick
    public SpawnConfig axisSpawn = new SpawnConfig(0.1, 16, 48);

    // Chance that a zombie spawns in a single plane of player each tick
    public SpawnConfig planeSpawn = new SpawnConfig(0.1, 16, 48);

    // Chance that a zombie spawns in a box around player but not inside the
    // smaller box each tick
    public SpawnConfig boxSpawn = new SpawnConfig(0.1, 24, 64);

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
    public DoubleList armorChances = DoubleList.of(0.1, 0.1, 0.1, 0.1);
    public IntList armorMaterialWeights =
        IntList.of(1, 10, 100, 50, 50, 100, 10);

    public IntList weaponTypeWeights = IntList.of(100, 20, 20, 75, 1);
    public IntList weaponMaterialWeights = IntList.of(1, 10, 100, 50, 50, 100);
    public double weaponChance = 0.1;
  }

  public static class TrimConfig {
    public ObjectList<String> patterns = ArmorTrimHandler.vanillaPatterns;
    public IntList patternWeights =
        IntList.of(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);

    public ObjectList<String> materials = ArmorTrimHandler.vanillaMaterials;
    public IntList materialWeights =
        IntList.of(100, 100, 1, 50, 100, 100, 100, 10, 100, 100);

    public boolean vanillaOnly = true;
    public double chance = 0.1;
  }

  public static class StatusEffectConfig {
    public ObjectList<String> ids = ObjectList.of(
        "speed", "haste", "strength", "jump_boost", "regeneration",
        "resistance", "fire_resistance", "water_breathing", "invisibility",
        "health_boost", "absorption", "saturation", "slow_falling",
        "conduit_power", "dolphins_grace");

    public int maxTimeInTicks = -1;
    public DoubleList incrementalChances = DoubleList.of(0.1, 0.5, 0.8, 0.9);
    public int maxAmplifier = 5;
  }

  public record Range(int min, int max) {}

  public record SpawnRange(double chance, int min, int max) {}
}
