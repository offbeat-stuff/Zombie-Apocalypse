package io.github.offbeat_stuff.zombie_apocalypse;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

public class ZombieMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("zombie_apocalypse");
	public static Xoroshiro128PlusPlusRandom XRANDOM = new Xoroshiro128PlusPlusRandom(new Random().nextLong());

	private final File settingsFile = new File("config", "zombie_apocalypse.toml");
	public static Config config = null;

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
		writeConfig();

	}

	private Item chooseRandomItem(Item[] items, float[] chances) {
		float chance = XRANDOM.nextFloat();
		for (int i = 0; i < chances.length; i++) {
			if (chance < chances[i]) {
				return items[i];
			}
		}
		return null;
	}

	private ItemStack randomEnchanctedItemStack(Item[] items, float[] chances) {
		Item item = chooseRandomItem(items, chances);
		if (item == null) {
			return ItemStack.EMPTY;
		}
		return EnchantmentHelper.enchant(XRANDOM, item.getDefaultStack(),
				XRANDOM.nextBetween(config.minEnchantmentLevel, config.maxEnchantmentLevel), true);
	}

	private ItemStack randomArmor(ServerWorld world, Item[] items, float[] chances) {
		var r = randomEnchanctedItemStack(items, chances);
		ArmorTrimHander.applyRandomArmorTrim(world, r);
		return r;
	}

	private void trySpawnZombieAt(ServerWorld world, BlockPos spawnPos) {
		if (world.isPlayerInRange(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), config.minPlayerDistance)) {
			return;
		}
		var zombie = EntityType.ZOMBIE.spawn(world, spawnPos, SpawnReason.NATURAL);
		PotionEffectHandler.applyRandomPotionEffects(zombie);
		if (!world.isSpaceEmpty(zombie) || !world.doesNotIntersectEntities(zombie)) {
			zombie.setRemoved(RemovalReason.DISCARDED);
			return;
		}
		Item[] helmets = { Items.NETHERITE_HELMET, Items.DIAMOND_HELMET, Items.IRON_HELMET, Items.LEATHER_HELMET };
		Item[] chestplates = { Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.IRON_CHESTPLATE,
				Items.LEATHER_CHESTPLATE };
		Item[] leggings = { Items.NETHERITE_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.IRON_LEGGINGS,
				Items.LEATHER_LEGGINGS };
		Item[] boots = { Items.NETHERITE_BOOTS, Items.DIAMOND_BOOTS, Items.IRON_BOOTS, Items.LEATHER_BOOTS };

		zombie.equipStack(EquipmentSlot.HEAD, randomArmor(world, helmets, config.armorPieceChances));
		zombie.equipStack(EquipmentSlot.CHEST, randomArmor(world, chestplates, config.armorPieceChances));
		zombie.equipStack(EquipmentSlot.LEGS, randomArmor(world, leggings, config.armorPieceChances));
		zombie.equipStack(EquipmentSlot.FEET, randomArmor(world, boots, config.armorPieceChances));
		if (XRANDOM.nextBoolean()) {
			zombie.equipStack(EquipmentSlot.MAINHAND, randomEnchanctedItemStack(
					new Item[] { Items.NETHERITE_AXE, Items.DIAMOND_AXE, Items.IRON_AXE, Items.GOLDEN_AXE,
							Items.STONE_AXE, Items.WOODEN_AXE },
					config.weaponChances));
		} else {
			zombie.equipStack(EquipmentSlot.MAINHAND, randomEnchanctedItemStack(
					new Item[] { Items.NETHERITE_SWORD, Items.DIAMOND_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD,
							Items.STONE_SWORD, Items.WOODEN_SWORD },
					config.weaponChances));
		}
	}

	private boolean isSpawnableForZombie(ServerWorld world, BlockPos pos) {
		var box = EntityType.ZOMBIE.createSimpleBoundingBox(pos.getX() + 0.5, pos.getY(), pos.getZ());
		return world.getWorldBorder().contains(pos)
				&& MobEntity.canMobSpawn(EntityType.ZOMBIE, world, SpawnReason.NATURAL, pos,
						world.getRandom())
				&& world.isSpaceEmpty(box) && !world.containsFluid(box);
	}

	private BlockPos findSpawnablePosNear(ServerWorld world, BlockPos spawnPos) {
		if (isSpawnableForZombie(world, spawnPos)) {
			return spawnPos;
		}
		for (var pos : BlockPos.iterate(spawnPos.getX() - 4, spawnPos.getY() - 4, spawnPos.getZ() - 4,
				spawnPos.getX() + 4,
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
		int[] r = { 0, 0, 0 };
		r[0] = XRANDOM.nextBetween(-config.boxSpawnMax, config.boxSpawnMax);
		r[1] = XRANDOM.nextBetween(-config.boxSpawnMax, config.boxSpawnMax);
		r[2] = XRANDOM.nextBetween(-config.boxSpawnMax, config.boxSpawnMax);

		r[XRANDOM.nextInt(3)] = randomCutout(config.boxSpawnMax, config.boxSpawnMin);

		return r;
	}

	private int[] randomAxisPos() {
		int[] result = { 0, 0, 0 };
		result[XRANDOM.nextInt(3)] = randomCutout(config.axisRangeMax, config.axisRangeMin);
		return result;
	}

	private int[] randomPlanePos() {
		int[] result = {0,0,0};
		int r = XRANDOM.nextInt(3);
		result[r] = XRANDOM.nextBetween(-config.planeRangeMax, config.planeRangeMax);
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

		BlockPos spawnPos = new BlockPos(playerPos.getX() + pos[0], playerPos.getY() + pos[1],
				playerPos.getZ() + pos[2]);
		spawnPos = findSpawnablePosNear(world, spawnPos);
		if (spawnPos != null) {
			trySpawnZombieAt(world, spawnPos);
		}
	}

	private boolean isTimeRight(long time) {
		if (config.maxTime < config.minTime) {
			return (time > 0 && time < config.maxTime) || (time < 24000 && time > config.minTime);
		} else {
			return time < config.maxTime && time > config.minTime;
		}
	}

	private void spawnZombiesInWorld(ServerWorld world) {
		if (world.getChunkManager().getSpawnInfo().getGroupToCount()
				.getInt(SpawnGroup.MONSTER) > config.maxZombieCount) {
			return;
		}
		world.getPlayers().forEach(player -> {
			if (isTimeRight(player.world.getTimeOfDay())) {
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
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			server.getWorlds().forEach(this::spawnZombiesInWorld);
		});
	}
}
