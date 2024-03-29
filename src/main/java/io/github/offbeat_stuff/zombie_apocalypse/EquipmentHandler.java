package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.Utils.*;
import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;

import io.github.offbeat_stuff.zombie_apocalypse.config.Config.EnchantmentConfig;
import io.github.offbeat_stuff.zombie_apocalypse.config.Config.EquipmentConfig;
import it.unimi.dsi.fastutil.doubles.DoubleImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

public class EquipmentHandler {

  private static final ObjectList<Item> HELMETS_LIST = ObjectList.of(
      Items.NETHERITE_HELMET, Items.DIAMOND_HELMET, Items.IRON_HELMET,
      Items.GOLDEN_HELMET, Items.CHAINMAIL_HELMET, Items.LEATHER_HELMET,
      Items.TURTLE_HELMET);

  private static final ObjectList<Item> CHESTPLATE_LIST =
      ObjectList.of(Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE,
                    Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE,
                    Items.CHAINMAIL_CHESTPLATE, Items.LEATHER_CHESTPLATE);

  private static final ObjectList<Item> LEGGINGS_LIST = ObjectList.of(
      Items.NETHERITE_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.IRON_LEGGINGS,
      Items.GOLDEN_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.LEATHER_LEGGINGS);

  private static final ObjectList<Item> BOOTS_LIST = ObjectList.of(
      Items.NETHERITE_BOOTS, Items.DIAMOND_BOOTS, Items.IRON_BOOTS,
      Items.GOLDEN_BOOTS, Items.CHAINMAIL_BOOTS, Items.LEATHER_BOOTS);

  private static final ObjectList<Item> SWORDS_LIST = ObjectList.of(
      Items.NETHERITE_SWORD, Items.DIAMOND_SWORD, Items.IRON_SWORD,
      Items.GOLDEN_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD);

  private static final ObjectList<Item> SHOVELS_LIST = ObjectList.of(
      Items.NETHERITE_SHOVEL, Items.DIAMOND_SHOVEL, Items.IRON_SHOVEL,
      Items.GOLDEN_SHOVEL, Items.STONE_SHOVEL, Items.WOODEN_SHOVEL);

  private static final ObjectList<Item> PICKAXES_LIST = ObjectList.of(
      Items.NETHERITE_PICKAXE, Items.DIAMOND_PICKAXE, Items.IRON_PICKAXE,
      Items.GOLDEN_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE);

  private static final ObjectList<Item> AXES_LIST =
      ObjectList.of(Items.NETHERITE_AXE, Items.DIAMOND_AXE, Items.IRON_AXE,
                    Items.GOLDEN_AXE, Items.STONE_AXE, Items.WOODEN_AXE);

  private static final ObjectList<Item> HOES_LIST =
      ObjectList.of(Items.NETHERITE_HOE, Items.DIAMOND_HOE, Items.IRON_HOE,
                    Items.GOLDEN_HOE, Items.STONE_HOE, Items.WOODEN_HOE);

  private static WeightedList<Item> helmets;
  private static WeightedList<Item> chestplates;
  private static WeightedList<Item> leggings;
  private static WeightedList<Item> boots;
  private static WeightedList<Item> weapons;

  private static double[] armor;
  private static double weapon;

  private static EnchantmentConfig enchantment;

  public static void load(EquipmentConfig conf) {
    armor = (new DoubleImmutableList(conf.armorChances)).toDoubleArray();
    helmets = new WeightedList<Item>(HELMETS_LIST, conf.armorMaterialWeights);
    chestplates =
        new WeightedList<Item>(CHESTPLATE_LIST, conf.armorMaterialWeights);
    leggings = new WeightedList<Item>(LEGGINGS_LIST, conf.armorMaterialWeights);
    boots = new WeightedList<Item>(BOOTS_LIST, conf.armorMaterialWeights);

    weapon = conf.weaponChance;
    weapons = new WeightedList<Item>(
        ObjectList.of(SWORDS_LIST, SHOVELS_LIST, PICKAXES_LIST, AXES_LIST,
                      HOES_LIST),
        conf.weaponMaterialWeights, conf.weaponTypeWeights);

    enchantment = conf.Enchantment;
  }

  public static void updateEnchantments(ServerWorld world,
                                        ZombieEntity zombie) {
    for (var slot : EquipmentSlot.values()) {
      if (zombie.getEquippedStack(slot).isEmpty()) {
        continue;
      }
      if (!roll(enchantment.chance)) {
        continue;
      }
      zombie.equipStack(slot, enchant(zombie.getEquippedStack(slot)));
    }
  }

  private static ItemStack enchant(ItemStack stack) {
    return EnchantmentHelper.enchant(
        XRANDOM, stack,
        XRANDOM.nextBetween(enchantment.level.min, enchantment.level.max),
        enchantment.treasureAllowed);
  }

  public static void initEquipment(ServerWorld world, ZombieEntity zombie) {
    if (roll(armor[0])) {
      equip(zombie, helmets.spit(), EquipmentSlot.HEAD);
    }

    if (roll(armor[1])) {
      equip(zombie, chestplates.spit(), EquipmentSlot.CHEST);
    }

    if (roll(armor[2])) {
      equip(zombie, leggings.spit(), EquipmentSlot.LEGS);
    }

    if (roll(armor[3])) {
      equip(zombie, boots.spit(), EquipmentSlot.FEET);
    }

    if (roll(weapon)) {
      equip(zombie, weapons.spit(), EquipmentSlot.MAINHAND);
    }
  }

  private static void equip(ZombieEntity zombie, Item item,
                            EquipmentSlot slot) {
    if (item == null) {
      return;
    }

    zombie.equipStack(slot, item.getDefaultStack());
  }
}
