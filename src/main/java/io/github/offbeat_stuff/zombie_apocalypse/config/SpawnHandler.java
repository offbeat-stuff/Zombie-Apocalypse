package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;
import static net.minecraft.util.math.Direction.Axis.VALUES;
import static net.minecraft.util.math.MathHelper.clamp;

import io.github.offbeat_stuff.zombie_apocalypse.PotionEffectHandler;
import io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler;
import io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.WeightList;
import io.github.offbeat_stuff.zombie_apocalypse.ZombieEntityInterface;
import io.github.offbeat_stuff.zombie_apocalypse.config.Common.Range;
import io.github.offbeat_stuff.zombie_apocalypse.config.Common.SpawnParameters;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.Registries;
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

  private static float minPlayerDistance;
  private static int maxZombieCount;

  private static List<EntityType<? extends LivingEntity>> mobs;
  private static List<Float> chances;

  private static boolean spawnInstantly;
  private static boolean vanillaSpawnRestrictionOnFoot;
  private static boolean checkIfBlockBelowAllowsSpawning;
  private static int lightLevel;

  private static SpawnParameters axis;
  private static SpawnParameters plane;
  private static SpawnParameters box;

  private static int maxSpawnAttemptsPerTick;
  private static int maxSpawnsPerTick;

  @SuppressWarnings("unchecked")
  public static List<EntityType<? extends LivingEntity>>
  getMobs(List<String> mobs) {
    var r = new ArrayList<EntityType<? extends LivingEntity>>();

    for (var id : mobs) {
      var entry = Registries.ENTITY_TYPE.getOrEmpty(new Identifier(id));
      if (!entry.isPresent()) {
        continue;
      }
      var entity = entry.get();

      if (entity != null) {
        try {
          r.add((EntityType<? extends LivingEntity>)entity);
        } catch (Exception e) {
          continue;
        }
      }
    }

    return r.stream().toList();
  }

  public static void handle(SpawnConfig raw) {
    raw.minPlayerDistance = Math.max(0, raw.minPlayerDistance);
    raw.maxZombieCount = Math.max(0, raw.maxZombieCount);
    raw.axisSpawnParameters.fixup((int)raw.minPlayerDistance);
    raw.planeSpawnParameters.fixup((int)raw.minPlayerDistance);
    raw.boxSpawnParameters.fixup((int)raw.minPlayerDistance);

    raw.timeRange.min = Math.max(raw.timeRange.min, 0) % 24000;
    raw.timeRange.max = Math.max(raw.timeRange.max, 0) % 24000;

    raw.instantSpawning.maxSpawnAttemptsPerTick =
        Math.max(raw.instantSpawning.maxSpawnAttemptsPerTick, 0);
    raw.instantSpawning.maxSpawnsPerTick =
        Math.max(raw.instantSpawning.maxSpawnsPerTick, 0);

    raw.lightLevel = clamp(raw.lightLevel, 0, 15);

    raw.mobIds =
        raw.mobIds.stream()
            .map(f -> f.trim().toLowerCase())
            .filter(f -> Registries.ENTITY_TYPE.containsId(new Identifier(f)))
            .toList();

    mobs = getMobs(raw.mobIds);
    chances = raw.mobWeights.getChances(mobs.size());

    spawnInstantly = raw.spawnInstantly;
    vanillaSpawnRestrictionOnFoot = raw.vanillaSpawnRestrictionOnFoot;
    checkIfBlockBelowAllowsSpawning = raw.checkIfBlockBelowAllowsSpawning;
    lightLevel = raw.lightLevel;

    minPlayerDistance = raw.minPlayerDistance;
    maxZombieCount = raw.maxZombieCount;
    spawnInstantly = raw.spawnInstantly;

    axis = raw.axisSpawnParameters;
    plane = raw.planeSpawnParameters;
    box = raw.boxSpawnParameters;

    maxSpawnAttemptsPerTick = raw.instantSpawning.maxSpawnAttemptsPerTick;
    maxSpawnsPerTick = raw.instantSpawning.maxSpawnsPerTick;
  }

  private static boolean isBlockedAtFoot(ServerWorld world, BlockPos pos,
                                         BlockState state) {
    return state.emitsRedstonePower() ||
        state.isIn(BlockTags.PREVENT_MOB_SPAWNING_INSIDE) ||
        state.isIn(BlockTags.INVALID_SPAWN_INSIDE);
  }

  private static boolean
  isSpawnableForEntity(ServerWorld world, BlockPos pos, LivingEntity entity,
                       EntityType<? extends LivingEntity> entityType) {
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

    var entityType = ProbabilityHandler.chooseRandom(mobs, chances);
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

    PotionEffectHandler.applyRandomPotionEffects(entity);

    var chance = XRANDOM.nextFloat() - ConfigHandler.frostZombieChance;

    if (entity instanceof ZombieEntityInterface zombie) {
      if (chance < 0) {
        zombie.setZombieType("frost");
      } else if (chance < ConfigHandler.fireZombieChance) {
        zombie.setZombieType("fire");
      }
    }

    ArmorHandler.handleZombie(world, entity);
    WeaponHandler.handleZombie(world, entity);

    return true;
  }

  private static void handleSlowSpawning(ServerWorld world,
                                         ServerPlayerEntity player, int count,
                                         BlockPos pos) {
    if (ProbabilityHandler.tryChance(axis.chance)) {
      spawnAttempt(world, randomAxisPos(pos));
    }

    if (count > 1 && ProbabilityHandler.tryChance(plane.chance)) {
      spawnAttempt(world, randomPlanePos(pos));
    }

    if (count > 2 && ProbabilityHandler.tryChance(box.chance)) {
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
                                            axis.generateExclusive()));
  }

  private static BlockPos randomPlanePos(BlockPos start) {
    var r = XRANDOM.nextInt(3);
    var s = (r + XRANDOM.nextInt(2)) % 3;

    return start.add(
        BlockPos.ORIGIN.offset(VALUES[r], plane.generateExclusive())
            .offset(VALUES[s], plane.generateInclusive()));
  }

  private static BlockPos randomBoxPos(BlockPos start) {
    var r = XRANDOM.nextInt(3);
    return start.add(BlockPos.ORIGIN.offset(VALUES[r], box.generateExclusive())
                         .offset(VALUES[(r + 1) % 3], box.generateInclusive())
                         .offset(VALUES[(r + 2) % 3], box.generateInclusive()));
  }

  public static class SpawnConfig {
    public boolean spawnInstantly = false;
    public boolean vanillaSpawnRestrictionOnFoot = true;
    public boolean checkIfBlockBelowAllowsSpawning = true;
    public InstantSpawning instantSpawning = new InstantSpawning();
    public int lightLevel = 15;

    public List<String> mobIds = List.of("zombie", "zombie_villager");
    public WeightList mobWeights = new WeightList(List.of(100, 5), 100);

    // minimum distance from player
    public float minPlayerDistance = 16f;

    // Max zombie count per player
    public int maxZombieCount = 150;

    // Chance that a zombie spawns in a single axis of player each tick
    public SpawnParameters axisSpawnParameters =
        new SpawnParameters(0.1f, 16, 48);

    // Chance that a zombie spawns in a single plane of player each tick
    public SpawnParameters planeSpawnParameters =
        new SpawnParameters(0.1f, 16, 48);

    // Chance that a zombie spawns in a box around player but not inside the
    // smaller box each tick
    public SpawnParameters boxSpawnParameters =
        new SpawnParameters(0.1f, 24, 64);

    // Time based Spawning in ticks - currently set to 0 to 1 am
    // each hour in minecraft represents 50 seconds or 1000 ticks
    public Range timeRange = new Range(1000, 13000);

    public List<String> allowedDimensions =
        List.of("overworld", "the_nether", "the_end");
  }

  public static class InstantSpawning {
    public int maxSpawnAttemptsPerTick = 100;
    public int maxSpawnsPerTick = 10;
  }
}
