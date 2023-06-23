package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.tryChance;
import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;
import static io.github.offbeat_stuff.zombie_apocalypse.config.Common.*;
import static net.minecraft.util.math.MathHelper.clamp;

import io.github.offbeat_stuff.zombie_apocalypse.ArmorTrimHandler;
import io.github.offbeat_stuff.zombie_apocalypse.PotionEffectHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import io.github.offbeat_stuff.zombie_apocalypse.config.ScreamHandler;

public class ConfigHandler {

  public static boolean zombiesBurnInSunlight;
  public static boolean spawnInstantly;

  public static float frostZombieChance = 0.01f;
  public static float fireZombieChance = 0.01f;

  private static BlockPos toBlockPos(BlockPos start, int x, int y, int z) {
    return start.add(x, y, z);
  }

  public static SpawnParameters axisSpawnParameters =
      new SpawnParameters(1f, 24, 48);

  private static BlockPos randomAxisPos(BlockPos start) {
    int[] r = {0, 0, 0};
    r[XRANDOM.nextInt(3)] = axisSpawnParameters.generateExclusive();
    return toBlockPos(start, r[0], r[1], r[2]);
  }

  public static SpawnParameters planeSpawnParameters =
      new SpawnParameters(1f, 24, 48);

  private static BlockPos randomPlanePos(BlockPos start) {
    int[] r = {0, 0, 0};
    int s = XRANDOM.nextInt(3);
    r[s] = planeSpawnParameters.generateExclusive();
    s = (s + XRANDOM.nextInt(2)) % 3;
    r[s] = planeSpawnParameters.generateInclusive();
    return toBlockPos(start, r[0], r[1], r[2]);
  }

  public static SpawnParameters boxSpawnParameters =
      new SpawnParameters(1f, 32, 64);

  private static BlockPos randomBoxPos(BlockPos start) {
    int[] r = {0, 0, 0};
    int s = XRANDOM.nextInt(3);
    r[s] = boxSpawnParameters.generateExclusive();
    r[(s + 1) % 3] = boxSpawnParameters.generateInclusive();
    r[(s + 2) % 3] = boxSpawnParameters.generateInclusive();
    return toBlockPos(start, r[0], r[1], r[2]);
  }

  public static List<BlockPos> generateSpawnPosition(BlockPos start) {
    var r = new ArrayList<BlockPos>();
    if (tryChance(axisSpawnParameters.chance))
      r.add(randomAxisPos(start));
    if (tryChance(planeSpawnParameters.chance))
      r.add(randomPlanePos(start));
    if (tryChance(boxSpawnParameters.chance))
      r.add(randomBoxPos(start));
    return r;
  }

  public static Predicate<Integer> isTimeRight;

  // Enchantment levels for armor and weapons
  public static Range enchantmentLevelRange = new Range(5, 40);

  // minimum distance from player
  public static float minPlayerDistance = 24f;

  // Max zombie count per player
  public static int maxZombieCount = 150;

  public static List<Identifier> allowedDimensions;

  public static void handleConfig(Config config) {
    zombiesBurnInSunlight = config.zombiesBurnInSunlight;
    ScreamHandler.handle(config.doScream);
    spawnInstantly = config.spawnInstantly;

    frostZombieChance = clamp(config.frostZombieChance, 0f, 1f);
    fireZombieChance = clamp(config.fireZombieChance, 0f, 1f);

    ZombieArmorHandler.handleRawArmorHandler(config.Armor);
    ZombieWeaponHandler.handleRawWeaponHandler(config.Weapon);
    PotionEffectHandler.handleRaw(config.statusEffects);
    ArmorTrimHandler.handleRawTrimHandler(config.ArmorTrims);

    axisSpawnParameters = config.axisSpawnParameters;

    planeSpawnParameters = config.planeSpawnParameters;

    boxSpawnParameters = config.boxSpawnParameters;

    config.timeRange.min %= 24000;
    config.timeRange.max %= 24000;

    isTimeRight = config.timeRange.toModPredicate(24000);

    config.enchantmentLevelRange.min =
        Math.max(1, config.enchantmentLevelRange.min);
    config.enchantmentLevelRange.max =
        Math.max(1, config.enchantmentLevelRange.max);
    enchantmentLevelRange = config.enchantmentLevelRange;

    minPlayerDistance = Math.max(config.minPlayerDistance, 0f);
    config.minPlayerDistance = minPlayerDistance;

    maxZombieCount = Math.abs(config.maxZombieCount);

    allowedDimensions =
        config.allowedDimensions.stream().map(f -> new Identifier(f)).toList();
  }
}
