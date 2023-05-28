package io.github.offbeat_stuff.zombie_apocalypse;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZombieMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("zombie_apocalypse");
	public static Xoroshiro128PlusPlusRandom XRANDOM = new Xoroshiro128PlusPlusRandom(new Random().nextLong());

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
				XRANDOM.nextBetween(Config.minEnchantmentLevel, Config.maxEnchantmentLevel), true);
	}

	private ItemStack randomArmor(ServerWorld world, Item[] items, float[] chances) {
		var r = randomEnchanctedItemStack(items, chances);
		ArmorTrimHander.applyRandomArmorTrim(world, r);
		return r;
	}

	private void trySpawnZombieAt(ServerWorld world, BlockPos spawnPos) {
		if (world.isPlayerInRange(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), Config.minPlayerDistance)) {
			return;
		}
		var zombie = EntityType.ZOMBIE.spawn(world, spawnPos, SpawnReason.NATURAL);
		if (!world.isSpaceEmpty(zombie)) {
			zombie.setRemoved(RemovalReason.DISCARDED);
			return;
		}
		Item[] helmets = { Items.NETHERITE_HELMET, Items.DIAMOND_HELMET, Items.IRON_HELMET, Items.LEATHER_HELMET };
		Item[] chestplates = { Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.IRON_CHESTPLATE,
				Items.LEATHER_CHESTPLATE };
		Item[] leggings = { Items.NETHERITE_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.IRON_LEGGINGS,
				Items.LEATHER_LEGGINGS };
		Item[] boots = { Items.NETHERITE_BOOTS, Items.DIAMOND_BOOTS, Items.IRON_BOOTS, Items.LEATHER_BOOTS };

		zombie.equipStack(EquipmentSlot.HEAD, randomArmor(world, helmets, Config.armorPieceChances));
		zombie.equipStack(EquipmentSlot.CHEST, randomArmor(world, chestplates, Config.armorPieceChances));
		zombie.equipStack(EquipmentSlot.LEGS, randomArmor(world, leggings, Config.armorPieceChances));
		zombie.equipStack(EquipmentSlot.FEET, randomArmor(world, boots, Config.armorPieceChances));
		if (XRANDOM.nextBoolean()) {
			zombie.equipStack(EquipmentSlot.MAINHAND, randomEnchanctedItemStack(
					new Item[] { Items.NETHERITE_AXE, Items.DIAMOND_AXE, Items.IRON_AXE, Items.GOLDEN_AXE,
							Items.STONE_AXE, Items.WOODEN_AXE },
					Config.weaponChances));
		} else {
			zombie.equipStack(EquipmentSlot.MAINHAND, randomEnchanctedItemStack(
					new Item[] { Items.NETHERITE_SWORD, Items.DIAMOND_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD,
							Items.STONE_SWORD, Items.WOODEN_SWORD },
					Config.weaponChances));
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
		r[0] = XRANDOM.nextBetween(-Config.boxSpawnMax, Config.boxSpawnMax);
		r[1] = XRANDOM.nextBetween(-Config.boxSpawnMax, Config.boxSpawnMax);
		r[2] = XRANDOM.nextBetween(-Config.boxSpawnMax, Config.boxSpawnMax);

		r[XRANDOM.nextInt(3)] = randomCutout(Config.boxSpawnMax, Config.boxSpawnMin);

		return r;
	}

	private int[] randomAxisPos() {
		int[] result = { 0, 0, 0 };
		result[XRANDOM.nextInt(3)] = randomCutout(Config.axisRangeMax, Config.axisRangeMin);
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

	@Override
	public void onInitialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			server.getPlayerManager().getPlayerList().forEach(player -> {
				if (player.world.getTimeOfDay() > Config.minTime && player.world.getTimeOfDay() < Config.maxTime) {
					if (XRANDOM.nextFloat() < Config.boxSpawnChance) {
						spawnAttemptForPlayer(player, randomBoxPos());
					}
					if (XRANDOM.nextFloat() < Config.axisSpawnChance) {
						spawnAttemptForPlayer(player, randomAxisPos());
					}
				}
			});
		});
	}
}
