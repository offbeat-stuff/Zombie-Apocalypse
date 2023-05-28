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
		return EnchantmentHelper.enchant(XRANDOM, item.getDefaultStack(), XRANDOM.nextBetween(5, 40), true);
	}

	private ItemStack randomArmor(ServerWorld world,Item[] items,float[] chances) {
		var r = randomEnchanctedItemStack(items, chances);
		ArmorTrimHander.applyRandomArmorTrim(world, r);
		return r;
	}

	private void trySpawnZombieAt(ServerWorld world, BlockPos spawnPos) {
		if (world.isPlayerInRange(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 24)) {
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

		float[] chances = { 0.0003f, 0.0025f, 0.025f, 0.15f };
		float[] weaponChances = { 0.001f, 0.0075f, 0.01f, 0.02f,0.05f,0.1f };
		zombie.equipStack(EquipmentSlot.HEAD, randomArmor(world,helmets, chances));
		zombie.equipStack(EquipmentSlot.CHEST,randomArmor(world,chestplates, chances));
		zombie.equipStack(EquipmentSlot.LEGS, randomArmor(world,leggings, chances));
		zombie.equipStack(EquipmentSlot.FEET, randomArmor(world,boots, chances));
		if (XRANDOM.nextBoolean()) {
			zombie.equipStack(EquipmentSlot.MAINHAND, randomEnchanctedItemStack(
					new Item[] { Items.NETHERITE_AXE, Items.DIAMOND_AXE, Items.IRON_AXE, Items.GOLDEN_AXE,
							Items.STONE_AXE, Items.WOODEN_AXE },
					weaponChances));
		} else {
			zombie.equipStack(EquipmentSlot.MAINHAND, randomEnchanctedItemStack(
					new Item[] { Items.NETHERITE_SWORD, Items.DIAMOND_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD,
							Items.STONE_SWORD, Items.WOODEN_SWORD },
					weaponChances));
		}
	}

	private boolean isSpawnableForZombie(ServerWorld world, BlockPos pos) {
		return world.getWorldBorder().contains(pos)
				&& MobEntity.canMobSpawn(EntityType.ZOMBIE, world, SpawnReason.NATURAL, pos,
						world.getRandom())
				&& world.isSpaceEmpty(new Box(pos, pos.up().add(1, 1, 1)));
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

	private int randomCutout() {
		int r = XRANDOM.nextInt(64);
		if (r < 32) {
			r -= 32;
		}
		return r;
	}

	private int[] randomBoxPos() {
		int[] r = {0,0,0};
		r[0] = XRANDOM.nextBetween(-64,64);
		r[1] = XRANDOM.nextBetween(-64,64);
		r[2] = XRANDOM.nextBetween(-64,64);

		r[XRANDOM.nextInt(3)] = randomCutout();

		return r;
	}

	private int[] randomAxisPos() {
		int a = XRANDOM.nextInt(48);
		if (a < 24) {
			a -= 48;
		}
		int[] result = {0,0,0};
		result[XRANDOM.nextInt(3)] = a;
		return result;
	}

	private void spawnAttemptForPlayer(ServerPlayerEntity player,int[] pos) {
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
				for (int i = 0; i < 5; i++) {
					spawnAttemptForPlayer(player,randomBoxPos());
				}
				spawnAttemptForPlayer(player,randomAxisPos());
			});
		});
	}
}
