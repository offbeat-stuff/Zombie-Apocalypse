package io.github.offbeat_stuff.zombie_apocalypse;

import io.github.offbeat_stuff.zombie_apocalypse.config.Common;
import io.github.offbeat_stuff.zombie_apocalypse.config.Config.EquipmentConfig;
import io.github.offbeat_stuff.zombie_apocalypse.config.Config.Range;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
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

  private static Range enchanctmentLevel;

  public static void load(EquipmentConfig conf) {}

  private static ItemStack randomTool() {
    var chance = XRANDOM.nextDouble();
    chance -= CHANCES.get(0);
    if (chance < 0) {
      return Common.randomEnchanctedItemStack(SWORDS, SWORDS_CHANCES);
    }
    chance -= CHANCES.get(1);
    if (chance < 0) {
      return Common.randomEnchanctedItemStack(SHOVELS, SHOVELS_CHANCES);
    }
    chance -= CHANCES.get(2);
    if (chance < 0) {
      return Common.randomEnchanctedItemStack(PICKAXES, PICKAXES_CHANCES);
    }
    chance -= CHANCES.get(3);
    if (chance < 0) {
      return Common.randomEnchanctedItemStack(AXES, AXES_CHANCES);
    }
    return Common.randomEnchanctedItemStack(HOES, HOES_CHANCES);
  }

  private static ItemStack randomArmor(ServerWorld world, List<Item> items,
                                       List<Double> chances) {
    var r = Common.randomEnchanctedItemStack(items, chances);
    ArmorTrimHandler.applyRandomArmorTrim(world, r);
    return r;
  }

  public static void handleZombie(ServerWorld world, LivingEntity entity) {
    if (tryChance(chances.get(0))) {
      entity.equipStack(EquipmentSlot.HEAD,
                        randomArmor(world, HELMETS, HELMETS_CHANCES));
    }
    if (tryChance(chances.get(1))) {
      entity.equipStack(EquipmentSlot.CHEST,
                        randomArmor(world, CHESTPLATES, CHESTPLATES_CHANCES));
    }
    if (tryChance(chances.get(2))) {
      entity.equipStack(EquipmentSlot.LEGS,
                        randomArmor(world, LEGGINGS, LEGGINGS_CHANCES));
    }
    if (tryChance(chances.get(3))) {
      entity.equipStack(EquipmentSlot.FEET,
                        randomArmor(world, BOOTS, BOOTS_CHANCES));
    }

    if (!ProbabilityHandler.tryChance(chance)) {
      return;
    }

    entity.equipStack(EquipmentSlot.MAINHAND, randomTool());
  }
}
