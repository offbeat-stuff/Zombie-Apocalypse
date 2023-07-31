package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.Utils.*;

import io.github.offbeat_stuff.zombie_apocalypse.ArmorTrimHandler;
import io.github.offbeat_stuff.zombie_apocalypse.EquipmentHandler;
import io.github.offbeat_stuff.zombie_apocalypse.ScreamHandler;
import io.github.offbeat_stuff.zombie_apocalypse.SpawnHandler;
import io.github.offbeat_stuff.zombie_apocalypse.StatusEffectHandler;
import io.github.offbeat_stuff.zombie_apocalypse.VersionDependent;
import io.github.offbeat_stuff.zombie_apocalypse.config.Config.*;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import net.minecraft.util.math.MathHelper;

public class ConfigHandler {

  public static boolean zombiesBurnInSunlight;

  public static void load(Config config) {
    zombiesBurnInSunlight = config.zombiesBurnInSunlight;
    ScreamHandler.load(config.Scream);

    SpawnHandler.load(config.Spawning);
    EquipmentHandler.load(config.Equipment);
    StatusEffectHandler.load(config.statusEffects);
    ArmorTrimHandler.load(config.ArmorTrims);
  }

  public static void correct(Config config) {
    correct(config.Scream);
    correct(config.Spawning);
    correct(config.Equipment);
    correct(config.ArmorTrims);
    correct(config.statusEffects);
  }

  private static void correct(ScreamConfig conf) {
    conf.message = conf.message == null || conf.message.equals("")
                       ? "Zombies are coming"
                       : conf.message;
    conf.sound = identifier(conf.sound);
    conf.sound = VersionDependent.isSound(conf.sound) ? conf.sound
                                                      : "entity.zombie.ambient";
    conf.volume = Math.max(0, conf.volume);
    conf.pitch = Math.max(0, conf.pitch);
  }

  private static void correct(SpawnConfig conf) {
    conf.lightLevel = MathHelper.clamp(conf.lightLevel, 0, 15);
    conf.mobIds = identifiers(conf.mobIds, VersionDependent::isZombie);
    conf.mobWeights = withSizeI(conf.mobWeights, conf.mobIds.size());

    conf.minPlayerDistance = natural(conf.minPlayerDistance);
    conf.maxZombieCountPerPlayer = natural(conf.maxZombieCountPerPlayer);
    correct(conf.axisSpawn, conf.minPlayerDistance);
    correct(conf.planeSpawn, conf.minPlayerDistance);
    correct(conf.boxSpawn, conf.minPlayerDistance);

    conf.timeRange.min = MathHelper.clamp(conf.timeRange.min, 0, 24000);
    conf.timeRange.max = MathHelper.clamp(conf.timeRange.max, 0, 24000);

    conf.allowedDimensions = identifiers(conf.allowedDimensions);

    conf.variants.chance = chance(conf.variants.chance);
    conf.variants.frostWeight = natural(conf.variants.frostWeight);
    conf.variants.flameWeight = natural(conf.variants.flameWeight);

    conf.instantSpawning.maxSpawnAttemptsPerTick =
        natural(conf.instantSpawning.maxSpawnAttemptsPerTick);
    conf.instantSpawning.maxSpawnsPerTick =
        natural(conf.instantSpawning.maxSpawnsPerTick);
  }

  private static void correct(EquipmentConfig conf) {
    conf.armorChances = withSize(conf.armorChances, 4);
    conf.armorMaterialWeights = withSizeI(conf.armorMaterialWeights, 7);

    conf.weaponTypeWeights = withSizeI(conf.weaponTypeWeights, 5);
    conf.weaponMaterialWeights = withSizeI(conf.weaponMaterialWeights, 6);

    conf.weaponChance = chance(conf.weaponChance);

    correct(conf.enchantmentLevel);
  }

  private static void correct(TrimConfig conf) {
    conf.materials = identifiers(
        conf.materials,
        id
        -> conf.vanillaOnly || ArmorTrimHandler.vanillaMaterials.contains(id));
    conf.materialWeights =
        withSizeI(conf.materialWeights, conf.materials.size());

    conf.patterns = identifiers(
        conf.patterns,
        id
        -> conf.vanillaOnly || ArmorTrimHandler.vanillaPatterns.contains(id));
    conf.patternWeights = withSizeI(conf.patternWeights, conf.patterns.size());

    conf.chance = chance(conf.chance);
  }

  private static void correct(StatusEffectConfig conf) {
    conf.ids = identifiers(new ObjectImmutableList<String>(conf.ids),
                           VersionDependent::isStatusEffect);

    conf.maxTimeInTicks = Math.max(conf.maxTimeInTicks, -1);
    conf.incrementalChances = chances(conf.incrementalChances);
    conf.maxAmplifier = natural(conf.maxAmplifier);
  }

  private static void correct(SpawnRange range, int minDist) {
    range.chance = chance(range.chance);
    range.min = Math.max(range.min, minDist);
    range.max = Math.max(range.max, range.min);
  }

  private static void correct(Range range) {
    range.min = natural(range.min);
    range.max = Math.max(range.max, range.min);
  }
}
