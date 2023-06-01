package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;
import static io.github.offbeat_stuff.zombie_apocalypse.config.Common.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ConfigHandler {

  public static boolean zombiesBurnInSunlight = false;

  public static List<Item> HELMETS;
  public static List<Item> CHESTPLATES;
  public static List<Item> LEGGINGS;
  public static List<Item> BOOTS;
  public static float armorChance = 0.5f;
  public static List<Float> HELMETS_CHANCES;
  public static List<Float> CHESTPLATES_CHANCES;
  public static List<Float> LEGGINGS_CHANCES;
  public static List<Float> BOOTS_CHANCES;

  public static final List<Item> AXES =
      List.of(Items.NETHERITE_AXE, Items.DIAMOND_AXE, Items.IRON_AXE,
              Items.GOLDEN_AXE, Items.STONE_AXE, Items.WOODEN_AXE);
  public static final List<Item> SWORDS =
      List.of(Items.NETHERITE_SWORD, Items.DIAMOND_SWORD, Items.IRON_SWORD,
              Items.GOLDEN_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD);
  public static float weaponChance = 0.5f;
  public static float axeChance = 0.3f;
  public static List<Float> SWORD_CHANCES;
  public static List<Float> AXE_CHANCES;

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
    if (XRANDOM.nextFloat() < axisSpawnParameters.chance)
      r.add(randomAxisPos(start));
    if (XRANDOM.nextFloat() < planeSpawnParameters.chance)
      r.add(randomPlanePos(start));
    if (XRANDOM.nextFloat() < boxSpawnParameters.chance)
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

  // Status effects for Zombie
  public static float firstChance = 0.05f;
  public static float secondChance = 0.1f;
  public static int maxPotionTimeInTicks = 12000;
  // range 1 - 255
  public static int maxAmplifier = 2;

  public static List<Identifier> allowedDimensions;
  private static float clamp(float r, float min, float max) {
    return Math.max(min, Math.min(r, max));
  }

  private static List<Float> generateChances(List<Integer> weights) {
    var sum = weights.stream().mapToInt(Integer::intValue).sum();
    return weights.stream().map(f -> (float)f / sum).toList();
  }

  private static List<Float> generateChances(int len, List<Integer> weights,
                                             int extraWeights) {
    var extra = len - weights.size();
    if (extra == 0) {
      return generateChances(weights);
    }
    var finalWeights = new ArrayList<Integer>(weights);
    var rem = extraWeights;
    for (int i = 0; i < extra - 1; i++) {
      finalWeights.add(extraWeights / extra);
      rem -= extraWeights / extra;
    }
    finalWeights.add(rem);
    return generateChances(finalWeights);
  }

  public static void handleConfig(Config config) {

    HELMETS = config.armorList.getHelmets();
    CHESTPLATES = config.armorList.getChestplates();
    LEGGINGS = config.armorList.getLeggings();
    BOOTS = config.armorList.getBoots();

    armorChance = clamp(config.armorChance, 0f, 1f);
    config.armorChance = armorChance;

    config.armorPieceChances =
        config.armorPieceChances.stream().map(f -> Math.abs(f)).toList();
    config.extraWeights = Math.abs(config.extraWeights);

    HELMETS_CHANCES = generateChances(HELMETS.size(), config.armorPieceChances,
                                      config.extraWeights);
    CHESTPLATES_CHANCES = generateChances(
        CHESTPLATES.size(), config.armorPieceChances, config.extraWeights);
    LEGGINGS_CHANCES = generateChances(
        LEGGINGS.size(), config.armorPieceChances, config.extraWeights);
    BOOTS_CHANCES = generateChances(BOOTS.size(), config.armorPieceChances,
                                    config.extraWeights);

    weaponChance = clamp(config.weaponChance, 0f, 1f);
    config.weaponChance = weaponChance;

    config.axeChance = clamp(config.axeChance,0f,1f);
    axeChance = config.axeChance;

    config.weaponChances =
        config.weaponChances.stream().map(f -> Math.abs(f)).toList();
    config.extraWeaponWeights = Math.abs(config.extraWeaponWeights);

    SWORD_CHANCES = generateChances(SWORDS.size(), config.weaponChances,
                                    config.extraWeaponWeights);
    AXE_CHANCES = generateChances(AXES.size(), config.weaponChances,
                                  config.extraWeaponWeights);

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

    maxZombieCount = config.maxZombieCount;
    firstChance = config.firstChance;
    secondChance = config.secondChance;
    maxPotionTimeInTicks = config.maxPotionTimeInTicks;
    maxAmplifier = config.maxAmplifier;

    allowedDimensions =
        config.allowedDimensions.stream().map(f -> new Identifier(f)).toList();
  }
}
