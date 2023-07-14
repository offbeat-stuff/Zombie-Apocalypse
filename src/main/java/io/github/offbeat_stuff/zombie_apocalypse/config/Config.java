package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.*;
import static io.github.offbeat_stuff.zombie_apocalypse.config.Common.*;

import io.github.offbeat_stuff.zombie_apocalypse.ArmorTrimHandler;
import io.github.offbeat_stuff.zombie_apocalypse.ArmorTrimHandler.RawTrimHandler;
import io.github.offbeat_stuff.zombie_apocalypse.PotionEffectHandler.RawStatusEffectHandler;
import io.github.offbeat_stuff.zombie_apocalypse.config.SpawnHandler.SpawnConfig;
import java.util.List;

public class Config {
  public boolean zombiesBurnInSunlight = false;
  public boolean doScream = true;

  public float frostZombieChance = 0.01f;
  public float flameZombieChance = 0.01f;

  public SpawnConfig spawn = new SpawnConfig();

  public ArmorHandler.ArmorConfig Armor = new ArmorHandler.ArmorConfig(
      List.of("netherite", "diamond", "iron", "gold", "chainmail", "leather",
              "turtle"),
      List.of("netherite", "diamond", "iron", "gold", "chainmail", "leather"),
      List.of("netherite", "diamond", "iron", "gold", "chainmail", "leather"),
      List.of("netherite", "diamond", "iron", "gold", "chainmail", "leather"),
      new ChanceList(List.of(0.1f, 0.1f, 0.1f, 0.1f), 0.1f),
      new WeightList(List.of(1, 10, 100, 50, 50, 100), 10));

  public WeaponHandler.WeaponConfig Weapon =
      new WeaponHandler.WeaponConfig(
          List.of("netherite", "diamond", "iron", "gold", "stone", "wood"),
          List.of("netherite", "diamond", "iron", "gold", "stone", "wood"),
          List.of("netherite", "diamond", "iron", "gold", "stone", "wood"),
          List.of("netherite", "diamond", "iron", "gold", "stone", "wood"),
          List.of("netherite", "diamond", "iron", "gold", "stone", "wood"),
          new WeightList(List.of(100, 20, 20, 75, 1), 100),
          new WeightList(List.of(1, 10, 100, 50, 50, 100), 100), 0.1f);

  public RawTrimHandler ArmorTrims = new RawTrimHandler(
      ArmorTrimHandler.vanillaPatterns, ArmorTrimHandler.vanillaMaterials,
      new WeightList(List.of(), 110),
      new WeightList(List.of(100, 100, 1, 50, 100, 100, 100, 10, 100, 100),
                     100),
      true, 0.1f);

  // Enchantment levels for armor and weapons
  public Range enchantmentLevelRange = new Range(5, 40);

  // Status effects for Zombie
  public RawStatusEffectHandler statusEffects = new RawStatusEffectHandler(
      List.of("speed", "haste", "strength", "jump_boost", "regeneration",
              "resistance", "fire_resistance", "water_breathing",
              "invisibility", "health_boost", "absorption", "saturation",
              "slow_falling", "conduit_power", "dolphins_grace"),
      -1, List.of(0.1f, 0.5f, 0.8f, 0.9f), 5);
}
