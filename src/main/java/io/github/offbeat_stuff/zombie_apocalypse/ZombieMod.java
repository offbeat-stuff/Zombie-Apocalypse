package io.github.offbeat_stuff.zombie_apocalypse;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
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
  public static Config config = null;

  private static final List<Item> helmets =
      List.of(Items.NETHERITE_HELMET, Items.DIAMOND_HELMET, Items.IRON_HELMET,
              Items.LEATHER_HELMET);
  private static final List<Item> chestplates =
      List.of(Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE,
              Items.IRON_CHESTPLATE, Items.LEATHER_CHESTPLATE);
  private static final List<Item> leggings =
      List.of(Items.NETHERITE_LEGGINGS, Items.DIAMOND_LEGGINGS,
              Items.IRON_LEGGINGS, Items.LEATHER_LEGGINGS);
  private static final List<Item> boots =
      List.of(Items.NETHERITE_BOOTS, Items.DIAMOND_BOOTS, Items.IRON_BOOTS,
              Items.LEATHER_BOOTS);

  private static final List<Item> axes =
      List.of(Items.NETHERITE_AXE, Items.DIAMOND_AXE, Items.IRON_AXE,
              Items.GOLDEN_AXE, Items.STONE_AXE, Items.WOODEN_AXE);
  private static final List<Item> swords =
      List.of(Items.NETHERITE_SWORD, Items.DIAMOND_SWORD, Items.IRON_SWORD,
              Items.GOLDEN_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD);

  private static List<Float> armorChances = new ArrayList<Float>();
  private static List<Float> weaponChances = new ArrayList<Float>();

  private void writeConfig() {
    if (settingsFile.exists())
      settingsFile.delete();
    try {
      new TomlWriter().write(config, settingsFile);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  private void handleConfig() {
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
    armorChances =
        new ArrayList<Float>(Arrays.asList(config.armorPieceChances));
    weaponChances = new ArrayList<Float>(Arrays.asList(config.weaponChances));
    writeConfig();
  }

  private ItemStack randomEnchanctedItemStack(List<Item> items,
                                              List<Float> chances) {
    Item item = ProbabilityHandler.chooseRandom(items, chances);
    if (item == null) {
      return ItemStack.EMPTY;
    }
    return EnchantmentHelper.enchant(
        XRANDOM, item.getDefaultStack(),
        XRANDOM.nextBetween(config.minEnchantmentLevel,
                            config.maxEnchantmentLevel),
        true);
  }

  private ItemStack randomArmor(ServerWorld world, List<Item> items,
                                List<Float> chances) {
    var r = randomEnchanctedItemStack(items, chances);
    ArmorTrimHander.applyRandomArmorTrim(world, r);
    return r;
  }

  private void trySpawnZombieAt(ServerWorld world, BlockPos spawnPos) {
    if (world.isPlayerInRange(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(),
                              config.minPlayerDistance)) {
      return;
    }
    var zombie = EntityType.ZOMBIE.spawn(world, spawnPos, SpawnReason.NATURAL);
    PotionEffectHandler.applyRandomPotionEffects(zombie);
    if (!world.isSpaceEmpty(zombie) ||
        !world.doesNotIntersectEntities(zombie)) {
      zombie.setRemoved(RemovalReason.DISCARDED);
      return;
    }

    if (XRANDOM.nextFloat() < config.armorChance) {
      zombie.equipStack(EquipmentSlot.HEAD,
                        randomArmor(world, helmets, armorChances));
    }
    if (XRANDOM.nextFloat() < config.armorChance) {
      zombie.equipStack(EquipmentSlot.CHEST,
                        randomArmor(world, chestplates, armorChances));
    }
    if (XRANDOM.nextFloat() < config.armorChance) {
      zombie.equipStack(EquipmentSlot.LEGS,
                        randomArmor(world, leggings, armorChances));
    }
    if (XRANDOM.nextFloat() < config.armorChance) {
      zombie.equipStack(EquipmentSlot.FEET,
                        randomArmor(world, boots, armorChances));
    }

    if (XRANDOM.nextFloat() > config.weaponChance) {
      return;
    }
    if (XRANDOM.nextFloat() < config.axeChance) {
      zombie.equipStack(EquipmentSlot.MAINHAND,
                        randomEnchanctedItemStack(axes, weaponChances));
    } else {
      zombie.equipStack(EquipmentSlot.MAINHAND,
                        randomEnchanctedItemStack(swords, weaponChances));
    }
  }

  private boolean isSpawnableForZombie(ServerWorld world, BlockPos pos) {
    var box = EntityType.ZOMBIE.createSimpleBoundingBox(pos.getX() + 0.5,
                                                        pos.getY(), pos.getZ());
    return world.getWorldBorder().contains(pos) &&
        MobEntity.canMobSpawn(EntityType.ZOMBIE, world, SpawnReason.NATURAL,
                              pos, world.getRandom()) &&
        world.isSpaceEmpty(box) && !world.containsFluid(box);
  }

  private BlockPos findSpawnablePosNear(ServerWorld world, BlockPos spawnPos) {
    if (isSpawnableForZombie(world, spawnPos)) {
      return spawnPos;
    }
    for (var pos : BlockPos.iterate(spawnPos.getX() - 4, spawnPos.getY() - 4,
                                    spawnPos.getZ() - 4, spawnPos.getX() + 4,
                                    spawnPos.getY() + 4, spawnPos.getZ() + 4)) {
      if (isSpawnableForZombie(world, pos)) {
        return pos;
      }
    }
    return null;
  }

  private int randomCutout(int max, int min) {
    int fullRange = (max - min);
    int r = XRANDOM.nextBetween(-fullRange, fullRange);
    if (r < 0) {
      r -= min;
    } else {
      r += min;
    }
    return r;
  }

  private int[] randomBoxPos() {
    int[] r = {0, 0, 0};
    r[0] = XRANDOM.nextBetween(-config.boxSpawnMax, config.boxSpawnMax);
    r[1] = XRANDOM.nextBetween(-config.boxSpawnMax, config.boxSpawnMax);
    r[2] = XRANDOM.nextBetween(-config.boxSpawnMax, config.boxSpawnMax);

    r[XRANDOM.nextInt(3)] =
        randomCutout(config.boxSpawnMax, config.boxSpawnMin);

    return r;
  }

  private int[] randomAxisPos() {
    int[] result = {0, 0, 0};
    result[XRANDOM.nextInt(3)] =
        randomCutout(config.axisRangeMax, config.axisRangeMin);
    return result;
  }

  private int[] randomPlanePos() {
    int[] result = {0, 0, 0};
    int r = XRANDOM.nextInt(3);
    result[r] =
        XRANDOM.nextBetween(-config.planeRangeMax, config.planeRangeMax);
    r += XRANDOM.nextInt(2);
    r = r % 3;
    result[r] = randomCutout(config.planeRangeMax, config.planeRangeMin);
    return result;
  }

  private void spawnAttemptForPlayer(ServerPlayerEntity player, int[] pos) {
    if (pos.length != 3) {
      return;
    }
    ServerWorld world = player.getWorld();
    BlockPos playerPos = player.getBlockPos();

    BlockPos spawnPos =
        new BlockPos(playerPos.getX() + pos[0], playerPos.getY() + pos[1],
                     playerPos.getZ() + pos[2]);
    spawnPos = findSpawnablePosNear(world, spawnPos);
    if (spawnPos != null) {
      trySpawnZombieAt(world, spawnPos);
    }
  }

  private boolean isTimeRight(long time) {
    if (config.maxTime < config.minTime) {
      return (time > 0 && time < config.maxTime) ||
          (time < 24000 && time > config.minTime);
    } else {
      return time < config.maxTime && time > config.minTime;
    }
  }

  private void spawnZombiesInWorld(ServerWorld world) {
    if (world.getChunkManager().getSpawnInfo().getGroupToCount().getInt(
            SpawnGroup.MONSTER) > config.maxZombieCount) {
      return;
    }
    world.getPlayers().forEach(player -> {
      if (isTimeRight(player.world.getTimeOfDay() % 24000)) {
        if (XRANDOM.nextFloat() < config.boxSpawnChance) {
          spawnAttemptForPlayer(player, randomBoxPos());
        }
        if (XRANDOM.nextFloat() < config.planeSpawnChance) {
          spawnAttemptForPlayer(player, randomPlanePos());
        }
        if (XRANDOM.nextFloat() < config.axisSpawnChance) {
          spawnAttemptForPlayer(player, randomAxisPos());
        }
      }
    });
  }

  @Override
  public void onInitialize() {
    handleConfig();
    ServerTickEvents.END_SERVER_TICK.register(
        server -> { server.getWorlds().forEach(this::spawnZombiesInWorld); });
  }
}
