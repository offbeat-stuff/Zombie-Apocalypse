package io.github.offbeat_stuff.zombie_apocalypse;

public class Config {
    public boolean zombiesBurnInSunlight = false;

    // 0.01 is 1% chance
    // NETHERITE DIAMOND IRON LEATHER
    public float[] armorPieceChances = { 0.0003f, 0.0025f, 0.025f, 0.15f };
    // NETHERITE DIAMOND IRON GOLD STONE WOODEN
    public float[] weaponChances = { 0.001f, 0.0075f, 0.01f, 0.02f, 0.05f, 0.1f };

    // Chance that a zombie spawns in a single axis of player each tick
    public float axisSpawnChance = 1f;
    public int axisRangeMin = 24;
    public int axisRangeMax = 48;

    // Chance that a zombie spawns in a box around player but not inside the smaller
    // box each tick
    public float boxSpawnChance = 1f;
    public int boxSpawnMin = 32;
    public int boxSpawnMax = 64;

    // Time based Spawning in ticks - currently set to 0 to 1 am
    // each hour in minecraft represents 50 seconds or 1000 ticks
    public int minTime = 0 * 1000;
    public int maxTime = 1 * 1000;

    // Enchantment levels for armor and weapons
    public int minEnchantmentLevel = 5;
    public int maxEnchantmentLevel = 40;

    // minimum distance from player
    public float minPlayerDistance = 24f;
}
