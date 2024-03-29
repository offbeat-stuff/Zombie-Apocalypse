package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;
import static net.minecraft.util.math.Direction.Axis.VALUES;

import io.github.offbeat_stuff.zombie_apocalypse.config.Config.Range;
import io.github.offbeat_stuff.zombie_apocalypse.config.Config.SpawnConfig;
import io.github.offbeat_stuff.zombie_apocalypse.config.Config.SpawnRange;
import io.github.offbeat_stuff.zombie_apocalypse.config.ConfigHandler;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;

public class SpawnHandler {

  private static double minPlayerDistance;
  private static int maxZombieCountPerPlayer;

  private static WeightedList<EntityType<? extends ZombieEntity>> mobs;

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

  public static List<EntityType<? extends ZombieEntity>>
  getMobs(List<String> mobs) {
    return mobs.stream()
        .map(VersionDependent::getZombie)
        .filter(f -> f != null)
        .collect(ObjectImmutableList.toList());
  }

  public static boolean checkWorld(ServerWorld world) {
    if (!(dimensions.contains(world.getRegistryKey().getValue())))
      return false;

    var ctime = world.getTimeOfDay() % 24000;
    if (time.min < time.max) {
      return ctime > time.min && ctime < time.max;
    }

    return ctime < time.min || ctime > time.max;
  }

  @SuppressWarnings("unchecked")
  public static boolean isPartOfApocalypse(ZombieEntity entity) {
    return mobs.contains((EntityType<? extends ZombieEntity>)entity.getType());
  }

  public static void load(SpawnConfig conf) {
    spawnInstantly = conf.spawnInstantly;
    vanillaSpawnRestrictionOnFoot = conf.vanillaSpawnRestrictionOnFoot;
    checkIfBlockBelowAllowsSpawning = conf.checkIfBlockBelowAllowsSpawning;

    lightLevel = conf.lightLevel;
    mobs = new WeightedList<EntityType<? extends ZombieEntity>>(
        getMobs(conf.mobIds), conf.mobWeights);

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
    return state.emitsRedstonePower() ||
        state.isIn(BlockTags.PREVENT_MOB_SPAWNING_INSIDE) ||
        state.isIn(BlockTags.INVALID_SPAWN_INSIDE);
  }

  private static boolean
  isSpawnableForEntity(ServerWorld world, BlockPos pos, ZombieEntity entity,
                       EntityType<? extends ZombieEntity> entityType) {
    if (!world.getWorldBorder().contains(pos)) {
      return false;
    }

    if (world.getLightLevel(pos) > lightLevel) {
      return false;
    }

    if (ConfigHandler.zombiesBurnInSunlight && world.isDay() &&
        world.isSkyVisible(pos)) {
      return false;
    }

    var state = world.getBlockState(pos.down());

    if (!state.isSideSolidFullSquare(world, pos.down(), Direction.UP)) {
      return false;
    }

    if (checkIfBlockBelowAllowsSpawning &&
        !state.allowsSpawning(world, pos, entityType)) {
      return false;
    }

    if (vanillaSpawnRestrictionOnFoot && isBlockedAtFoot(world, pos, state)) {
      return false;
    }

    entity.setPosition(Vec3d.add(pos, 0.5, 0.0, 0.5));

    return world.doesNotIntersectEntities(entity) &&
        world.isSpaceEmpty(entity.getBoundingBox()) &&
        !world.containsFluid(entity.getBoundingBox());
  }

  private static boolean spawnAttempt(ServerWorld world, BlockPos pos) {

    var entityType = mobs.spit();
    var entity = entityType.create(world, null, null, pos, SpawnReason.NATURAL,
                                   false, false);

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

    world.spawnEntityAndPassengers(entity);

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
    if (world.getDifficulty().equals(Difficulty.PEACEFUL)) {
      return;
    }

    var maxZombieCount = maxZombieCountPerPlayer * world.getPlayers().size();

    int zombieCount =
        world.getChunkManager().getSpawnInfo().getGroupToCount().getInt(
            SpawnGroup.MONSTER);
    if (zombieCount > maxZombieCount)
      return;

    world.getPlayers().forEach(player -> {
      if (spawnInstantly) {
        handleFastSpawning(world, player, maxZombieCount - zombieCount,
                           player.getBlockPos());
      } else {
        handleSlowSpawning(world, player, maxZombieCount - zombieCount,
                           player.getBlockPos());
      }
    });
  }

  private static BlockPos randomAxisPos(BlockPos start) {
    return start.add(BlockPos.ORIGIN.offset(Axis.pickRandomAxis(XRANDOM),
                                            generateExclusive(axis)));
  }

  private static BlockPos randomPlanePos(BlockPos start) {
    var r = XRANDOM.nextInt(3);
    var s = (r + XRANDOM.nextInt(2)) % 3;

    return start.add(BlockPos.ORIGIN.offset(VALUES[r], generateExclusive(plane))
                         .offset(VALUES[s], generateInclusive(plane)));
  }

  private static BlockPos randomBoxPos(BlockPos start) {
    var r = XRANDOM.nextInt(3);
    return start.add(BlockPos.ORIGIN.offset(VALUES[r], generateExclusive(box))
                         .offset(VALUES[(r + 1) % 3], generateInclusive(box))
                         .offset(VALUES[(r + 2) % 3], generateInclusive(box)));
  }

  private static int generateExclusive(SpawnRange range) {
    return XRANDOM.nextBetween(range.min, range.max) *
        (XRANDOM.nextInt(2) == 0 ? -1 : 1);
  }

  private static int generateInclusive(SpawnRange range) {
    return XRANDOM.nextBetween(-range.max, range.max);
  }
}
