package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;

import io.github.offbeat_stuff.zombie_apocalypse.config.Config.Range;
import io.github.offbeat_stuff.zombie_apocalypse.config.Config.SpawnConfig;
import io.github.offbeat_stuff.zombie_apocalypse.config.Config.SpawnRange;
import io.github.offbeat_stuff.zombie_apocalypse.config.ConfigHandler;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.world.Difficulty;

public class SpawnHandler {

  private static double minPlayerDistance;
  private static int maxZombieCountPerPlayer;

  private static WeightedList<EntityType> mobs;

  private static double variant;
  private static WeightedList<ZombieKind> variants;

  private static boolean spawnInstantly;
  private static boolean vanillaSpawnRestrictionOnFoot;
  private static boolean checkIfBlockBelowAllowsSpawning;
  private static int lightLevel;

  private static SpawnRange axis;
  private static SpawnRange plane;
  private static SpawnRange box;

  private static int maxSpawnAttemptsPerTick;
  private static int maxSpawnsPerTick;

  private static Range time;
  private static List<Identifier> dimensions;

  public static List<Identifier> getMobs(List<String> mobs) {
    return mobs.stream()
        .map(VersionDependent::getZombie)
        .filter(f -> f != null)
        .collect(ObjectImmutableList.toList());
  }

  public static boolean checkWorld(ServerWorld world) {
    // if (!(dimensions.contains(world.getRegistryKey().getValue())))
    //   return false;

    var ctime = world.getTimeOfDay() % 24000;
    if (time.min < time.max) {
      return ctime > time.min && ctime < time.max;
    }

    return ctime < time.min || ctime > time.max;
  }

  @SuppressWarnings("unchecked")
  public static boolean isPartOfApocalypse(ZombieEntity entity) {
    return mobs.contains(
        EntityType.REGISTRY.getIdentifier(entity.getClass()).toString());
  }

  public static void load(SpawnConfig conf) {
    spawnInstantly = conf.spawnInstantly;
    vanillaSpawnRestrictionOnFoot = conf.vanillaSpawnRestrictionOnFoot;
    checkIfBlockBelowAllowsSpawning = conf.checkIfBlockBelowAllowsSpawning;

    lightLevel = conf.lightLevel;
    mobs = new WeightedList<EntityType>(getMobs(conf.mobIds), conf.mobWeights);

    variant = conf.variants.chance;
    variants = new WeightedList<ZombieKind>(
        List.of(ZombieKind.Frost, ZombieKind.Flame),
        List.of(conf.variants.frostWeight, conf.variants.flameWeight));

    maxSpawnsPerTick = conf.instantSpawning.maxSpawnsPerTick;
    maxSpawnAttemptsPerTick = conf.instantSpawning.maxSpawnAttemptsPerTick;

    minPlayerDistance = (double)conf.minPlayerDistance;
    maxZombieCountPerPlayer = conf.maxZombieCountPerPlayer;

    axis = conf.axisSpawn;
    plane = conf.planeSpawn;
    box = conf.boxSpawn;

    time = conf.timeRange;
    dimensions = Utils.identified(conf.allowedDimensions);
  }

  private static boolean isBlockedAtFoot(ServerWorld world, BlockPos pos,
                                         BlockState state) {
    return state.emitsRedstonePower();
  }

  private static boolean
  isSpawnableForEntity(ServerWorld world, BlockPos pos, ZombieEntity entity,
                       EntityType<? extends ZombieEntity> entityType) {
    if (!world.method_8524().contains(pos)) {
      return false;
    }

    if (world.method_16358(pos) > lightLevel) {
      return false;
    }

    if (ConfigHandler.zombiesBurnInSunlight && world.isDay() &&
        world.method_8555(pos)) {
      return false;
    }

    var state = world.getBlockState(pos.down());

    if (!Block.isFaceFullSquare(state.getCollisionShape(world, pos),
                                Direction.UP)) {
      return false;
    }

    if (checkIfBlockBelowAllowsSpawning && !state.method_16907()) {
      return false;
    }

    if (vanillaSpawnRestrictionOnFoot && isBlockedAtFoot(world, pos, state)) {
      return false;
    }

    entity.updatePosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

    return world.method_16382(entity, entity.getBoundingBox()) &&
        world.method_16387(entity, entity.getBoundingBox()) &&
        !world.method_16388(entity.getBoundingBox());
  }

  private static boolean spawnAttempt(ServerWorld world, BlockPos pos) {

    var entityType = mobs.spit();
    var entity =
        entityType.method_15627(world, null, null, null, pos, false, false);

    if (entity == null) {
      return false;
    }

    var spawnable = isSpawnableForEntity(world, pos, entity, entityType);
    if (!spawnable) {
      for (var bpos : BlockPos.iterate(pos.add(-4, -4, -4), pos.add(4, 4, 4))) {
        if (isSpawnableForEntity(world, pos, entity, entityType)) {
          spawnable = true;
          pos = bpos;
          break;
        }
      }
    }

    if (!spawnable) {
      return false;
    }

    if (world.isPlayerInRange(pos.getX(), pos.getY(), pos.getZ(),
                              minPlayerDistance)) {
      return false;
    }

    world.method_3686(entity);

    StatusEffectHandler.applyRandomPotionEffects(entity);

    if (Utils.roll(variant) && entity instanceof ZombieEntityInterface zombie) {
      zombie.setKind(variants.spit());
    }

    return true;
  }

  private static void handleSlowSpawning(ServerWorld world,
                                         ServerPlayerEntity player, int count,
                                         BlockPos pos) {
    if (Utils.roll(axis.chance)) {
      spawnAttempt(world, randomAxisPos(pos));
    }

    if (count > 1 && Utils.roll(plane.chance)) {
      spawnAttempt(world, randomPlanePos(pos));
    }

    if (count > 2 && Utils.roll(box.chance)) {
      spawnAttempt(world, randomBoxPos(pos));
    }
  }

  private static void handleFastSpawning(ServerWorld world,
                                         ServerPlayerEntity player, int count,
                                         BlockPos pos) {
    var success = 0;
    for (int i = 0; i < maxSpawnAttemptsPerTick; i++) {
      var spawnPos =
          i % 3 == 0 ? randomAxisPos(pos)
                     : (i % 3 == 1 ? randomPlanePos(pos) : randomBoxPos(pos));

      success += spawnAttempt(world, spawnPos) ? 1 : 0;

      if (success >= maxSpawnsPerTick) {
        return;
      }

      if (success >= count) {
        return;
      }
    }
  }

  public static void spawnZombiesInWorld(ServerWorld world) {
    if (world.method_16346().equals(Difficulty.PEACEFUL)) {
      return;
    }

    var maxZombieCount = maxZombieCountPerPlayer * world.playerEntities.size();

    int zombieCount = world.method_16324(ZombieEntity.class, maxZombieCount);
    if (zombieCount > maxZombieCount)
      return;

    world.playerEntities.forEach(player -> {
      if (!(player instanceof ServerPlayerEntity serverPlayer)) {
        return;
      }
      var pos = new BlockPos(player);
      if (spawnInstantly) {
        handleFastSpawning(world, serverPlayer, maxZombieCount - zombieCount,
                           pos);
      } else {
        handleSlowSpawning(world, serverPlayer, maxZombieCount - zombieCount,
                           pos);
      }
    });
  }

  private static BlockPos randomAxisPos(BlockPos start) {
    var dir = Direction.get(AxisDirection.POSITIVE,
                            Axis.values()[XRANDOM.nextInt(3)]);
    return start.add(BlockPos.ORIGIN.offset(dir, generateExclusive(axis)));
  }

  private static BlockPos randomPlanePos(BlockPos start) {
    var r = XRANDOM.nextInt(3);
    var s = (r + XRANDOM.nextInt(2)) % 3;

    var dirR = Direction.get(AxisDirection.POSITIVE, Axis.values()[r]);
    var dirS = Direction.get(AxisDirection.POSITIVE, Axis.values()[s]);

    return start.add(BlockPos.ORIGIN.offset(dirR, generateExclusive(plane))
                         .offset(dirS, generateInclusive(plane)));
  }

  private static BlockPos randomBoxPos(BlockPos start) {
    var r = XRANDOM.nextInt(3);

    var dirR = Direction.get(AxisDirection.POSITIVE, Axis.values()[r]);
    var dirR1 =
        Direction.get(AxisDirection.POSITIVE, Axis.values()[(r + 1) % 3]);
    var dirR2 =
        Direction.get(AxisDirection.POSITIVE, Axis.values()[(r + 2) % 3]);

    return start.add(BlockPos.ORIGIN.offset(dirR, generateExclusive(box))
                         .offset(dirR1, generateInclusive(box))
                         .offset(dirR2, generateInclusive(box)));
  }

  private static int generateExclusive(SpawnRange range) {
    return XRANDOM.nextInt(range.min, range.max) *
        (XRANDOM.nextInt(2) == 0 ? -1 : 1);
  }

  private static int generateInclusive(SpawnRange range) {
    return XRANDOM.nextInt(-range.max, range.max);
  }
}
