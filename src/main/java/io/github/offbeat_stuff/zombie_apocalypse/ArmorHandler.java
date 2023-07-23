package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.tryChance;

import io.github.offbeat_stuff.zombie_apocalypse.config.ArmorConfig;
import io.github.offbeat_stuff.zombie_apocalypse.config.Common;
import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class ArmorHandler {
  public static final List<String> allowedMaterials = List.of(
      "netherite", "diamond", "iron", "gold", "chainmail", "leather", "turtle");
  public static final List<String> allowedSlots =
      List.of("helmet", "chestplate", "leggings", "boots");

  private static List<Item> HELMETS;
  private static List<Item> CHESTPLATES;
  private static List<Item> LEGGINGS;
  private static List<Item> BOOTS;

  private static List<Double> chances;
  private static List<Double> HELMETS_CHANCES;
  private static List<Double> CHESTPLATES_CHANCES;
  private static List<Double> LEGGINGS_CHANCES;
  private static List<Double> BOOTS_CHANCES;

  public static void handleArmorConfig(ArmorConfig config) {
    config.fixAll();
    HELMETS = Common.getList(config.helmets, allowedSlots.get(0));
    CHESTPLATES = Common.getList(config.chestplates, allowedSlots.get(1));
    LEGGINGS = Common.getList(config.leggings, allowedSlots.get(2));
    BOOTS = Common.getList(config.boots, allowedSlots.get(3));

    chances = config.chancesPerSlot.getChances(4);
    HELMETS_CHANCES = config.materialWeights.getChances(HELMETS.size());
    CHESTPLATES_CHANCES = config.materialWeights.getChances(CHESTPLATES.size());
    LEGGINGS_CHANCES = config.materialWeights.getChances(LEGGINGS.size());
    BOOTS_CHANCES = config.materialWeights.getChances(BOOTS.size());
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
  }
}
