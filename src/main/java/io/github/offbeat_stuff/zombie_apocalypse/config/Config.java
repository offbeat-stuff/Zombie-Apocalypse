package io.github.offbeat_stuff.zombie_apocalypse.config;

public class Config {
  public boolean zombiesBurnInSunlight = false;

  // 0.01 is 1% chance
  // NETHERITE DIAMOND IRON LEATHER
  public float armorChance = 0.5f;
  public Float[] armorPieceChances = {0.0005f, 0.0025f};
  // NETHERITE DIAMOND IRON GOLD STONE WOODEN
  public float weaponChance = 0.5f;
  public float axeChance = 0.3f;
  public Float[] weaponChances = {0.001f, 0.0075f, 0.01f};

  // Chance that a zombie spawns in a single axis of player each tick
  public float axisSpawnChance = 1f;
  public int axisRangeMin = 24;
  public int axisRangeMax = 48;

  // Chance that a zombie spawns in a single plane of player each tick
  public float planeSpawnChance = 1f;
  public int planeRangeMin = 24;
  public int planeRangeMax = 48;

  // Chance that a zombie spawns in a box around player but not inside the
  // smaller box each tick
  public float boxSpawnChance = 1f;
  public int boxSpawnMin = 32;
  public int boxSpawnMax = 64;

  // Time based Spawning in ticks - currently set to 0 to 1 am
  // each hour in minecraft represents 50 seconds or 1000 ticks
  public int minTime = 1 * 1000;
  public int maxTime = 13 * 1000;

  // Enchantment levels for armor and weapons
  public int minEnchantmentLevel = 5;
  public int maxEnchantmentLevel = 40;

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
}
