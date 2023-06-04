package io.github.offbeat_stuff.zombie_apocalypse;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import io.github.offbeat_stuff.zombie_apocalypse.config.Config;
import io.github.offbeat_stuff.zombie_apocalypse.config.ConfigHandler;
import io.github.offbeat_stuff.zombie_apocalypse.config.ZombieArmorHandler;
import io.github.offbeat_stuff.zombie_apocalypse.config.ZombieWeaponHandler;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZombieMod implements ModInitializer {
  public static final Logger LOGGER =
      LoggerFactory.getLogger("zombie_apocalypse");
  public static Xoroshiro128PlusPlusRandom XRANDOM =
      new Xoroshiro128PlusPlusRandom(new Random().nextLong());

  private final File settingsFile =
      new File("config", "zombie_apocalypse.toml");

  private void handleConfig() {
    Config config = null;
    if (settingsFile.exists()) {
      try {
        config = new Toml().read(settingsFile).to(Config.class);
      } catch (Exception ex) {
        System.out.println("Error while loading config! Creating a new one!");
        ex.printStackTrace();
      }
    }

    if (config == null) {
      config = new Config();
    }
    ConfigHandler.handleConfig(config);
    if (settingsFile.exists())
      settingsFile.delete();
    try {
      new TomlWriter().write(config, settingsFile);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  private void trySpawnZombieAt(ServerWorld world, BlockPos spawnPos) {
    if (world.isPlayerInRange(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(),
                              ConfigHandler.minPlayerDistance)) {
      return;
    }
    var zombie = EntityType.ZOMBIE.spawn(world, spawnPos, SpawnReason.NATURAL);
    PotionEffectHandler.applyRandomPotionEffects(zombie);
    if (!world.isSpaceEmpty(zombie) ||
        !world.doesNotIntersectEntities(zombie)) {
      zombie.setRemoved(RemovalReason.DISCARDED);
      return;
    }

    if (ProbabilityHandler.tryChance(ConfigHandler.frostZombieChance)) {
      ((ZombieEntityInterface)zombie).setZombieType("frost");
    } else if (ProbabilityHandler.tryChance(ConfigHandler.fireZombieChance)) {
      ((ZombieEntityInterface)zombie).setZombieType("fire");
    }

    ZombieArmorHandler.handleZombie(world, zombie);
    ZombieWeaponHandler.handleZombie(world, zombie);
  }

  private boolean isSpawnableForZombie(ServerWorld world, BlockPos pos) {
    var box = EntityType.ZOMBIE.createSimpleBoundingBox(pos.getX() + 0.5,
                                                        pos.getY(), pos.getZ());
    return world.getWorldBorder().contains(pos) &&
        MobEntity.canMobSpawn(EntityType.ZOMBIE, world, SpawnReason.NATURAL,
                              pos, world.getRandom()) &&
        world.isSpaceEmpty(box) && !world.containsFluid(box);
  }

  private void zombieSpawnAttempt(ServerWorld world, BlockPos pos) {
    if (isSpawnableForZombie(world, pos))
      trySpawnZombieAt(world, pos);
    for (var bpos : BlockPos.iterate(pos.add(-4, -4, -4), pos.add(4, 4, 4))) {
      if (isSpawnableForZombie(world, pos))
        trySpawnZombieAt(world, bpos);
    }
  }

  private void spawnZombiesInWorld(ServerWorld world) {
    if (!(ConfigHandler.allowedDimensions.contains(
            world.getRegistryKey().getValue())))
      return;
    int zombieCount =
        world.getChunkManager().getSpawnInfo().getGroupToCount().getInt(
            SpawnGroup.MONSTER);
    if (zombieCount > ConfigHandler.maxZombieCount)
      return;

    var time = world.getTimeOfDay() % 24000;
    if (!ConfigHandler.isTimeRight.test((int)time))
      return;

    final int count = ConfigHandler.spawnInstantly
                          ? ConfigHandler.maxZombieCount - zombieCount
                          : 1;

    world.getPlayers().forEach(player -> {
      for (int i = 0; i < count; i += 3) {
        ConfigHandler.generateSpawnPosition(player.getBlockPos())
            .forEach(b -> zombieSpawnAttempt(world, b));
      }
    });
  }

  @Override
  public void onInitialize() {
    handleConfig();
    ServerTickEvents.END_SERVER_TICK.register(
        server -> server.getWorlds().forEach(this::spawnZombiesInWorld));
  }
}
