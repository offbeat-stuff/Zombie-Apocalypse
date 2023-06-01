package io.github.offbeat_stuff.zombie_apocalypse.config;
import static io.github.offbeat_stuff.zombie_apocalypse.config.Common.*;

import java.util.List;

public class Config {
  public boolean zombiesBurnInSunlight = false;

  // 0.01 is 1% chance
  // NETHERITE DIAMOND IRON LEATHER
  public float armorChance = 0.1f;
  public ArmorList armorList = new ArmorList(
      List.of("netherite", "diamond", "iron", "gold", "chainmail", "leather",
              "turtle"),
      List.of("netherite", "diamond", "iron", "gold", "chainmail", "leather"),
      List.of("netherite", "diamond", "iron", "gold", "chainmail", "leather"),
      List.of("netherite", "diamond", "iron", "gold", "chainmail", "leather"));
  public int extraWeights = 100;
  public List<Integer> armorPieceChances = List.of(1, 10, 100, 50, 50, 250);
  // NETHERITE DIAMOND IRON GOLD STONE WOODEN
  public float weaponChance = 0.5f;
  public float axeChance = 0.3f;
  public List<Float> weaponChances = List.of(0.001f, 0.0075f, 0.01f);

  // Chance that a zombie spawns in a single axis of player each tick
  public SpawnParameters axisSpawnParameters = new SpawnParameters(1f, 24, 48);

  // Chance that a zombie spawns in a single plane of player each tick
  public SpawnParameters planeSpawnParameters = new SpawnParameters(1f, 24, 48);

  // Chance that a zombie spawns in a box around player but not inside the
  // smaller box each tick
  public SpawnParameters boxSpawnParameters = new SpawnParameters(1f, 32, 64);

  // Time based Spawning in ticks - currently set to 0 to 1 am
  // each hour in minecraft represents 50 seconds or 1000 ticks
  public Range timeRange = new Range(1000, 13000);

  // Enchantment levels for armor and weapons
  public Range enchantmentLevelRange = new Range(5, 40);

  // minimum distance from player
  public float minPlayerDistance = 24f;

  // Max zombie count per player
  public int maxZombieCount = 150;

  // Status effects for Zombie
  public float firstChance = 0.05f;
  public float secondChance = 0.1f;
  public int maxPotionTimeInTicks = 12000;
  // range 1 - 255
  public int maxAmplifier = 2;

  public List<String> allowedDimensions =
      List.of("overworld", "ther_nether", "the_end");
}
