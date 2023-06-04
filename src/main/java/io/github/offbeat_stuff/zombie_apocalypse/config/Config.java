package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.*;
import static io.github.offbeat_stuff.zombie_apocalypse.config.Common.*;

import java.util.List;

public class Config {
  public boolean zombiesBurnInSunlight = false;
  public boolean spawnInstantly = false;

  public float frostZombieChance = 0.01f;
  public float fireZombieChance = 0.01f;

  public ZombieArmorHandler
      .RawArmorHandler Armor = new ZombieArmorHandler.RawArmorHandler(
      List.of("netherite", "diamond", "iron", "gold", "chainmail", "leather",
              "turtle"),
      List.of("netherite", "diamond", "iron", "gold", "chainmail", "leather"),
      List.of("netherite", "diamond", "iron", "gold", "chainmail", "leather"),
      List.of("netherite", "diamond", "iron", "gold", "chainmail", "leather"),
      new ChanceList(List.of(0.1f, 0.1f, 0.1f, 0.1f), 0.1f),
      new WeightList(List.of(1, 10, 100, 50, 50, 100), 10));

  public ZombieWeaponHandler.RawWeaponHander Weapon =
      new ZombieWeaponHandler.RawWeaponHander(
          List.of("netherite", "diamond", "iron", "gold", "stone", "wood"),
          List.of("netherite", "diamond", "iron", "gold", "stone", "wood"),
          List.of("netherite", "diamond", "iron", "gold", "stone", "wood"),
          List.of("netherite", "diamond", "iron", "gold", "stone", "wood"),
          List.of("netherite", "diamond", "iron", "gold", "stone", "wood"),
          new WeightList(List.of(100, 20, 20, 75, 1), 100),
          new WeightList(List.of(1, 10, 100, 50, 50, 100), 100), 0.1f);

  // Chance that a zombie spawns in a single axis of player each tick
  public SpawnParameters axisSpawnParameters =
      new SpawnParameters(0.1f, 16, 48);

  // Chance that a zombie spawns in a single plane of player each tick
  public SpawnParameters planeSpawnParameters =
      new SpawnParameters(0.1f, 16, 48);

  // Chance that a zombie spawns in a box around player but not inside the
  // smaller box each tick
  public SpawnParameters boxSpawnParameters = new SpawnParameters(0.1f, 24, 64);

  // Time based Spawning in ticks - currently set to 0 to 1 am
  // each hour in minecraft represents 50 seconds or 1000 ticks
  public Range timeRange = new Range(1000, 13000);

  // Enchantment levels for armor and weapons
  public Range enchantmentLevelRange = new Range(5, 40);

  // minimum distance from player
  public float minPlayerDistance = 16f;

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
