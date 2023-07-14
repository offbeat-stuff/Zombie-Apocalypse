package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.tryChance;

import io.github.offbeat_stuff.zombie_apocalypse.ArmorTrimHandler;
import io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.ChanceList;
import io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.WeightList;
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

  private static List<Float> chances;
  private static List<Float> HELMETS_CHANCES;
  private static List<Float> CHESTPLATES_CHANCES;
  private static List<Float> LEGGINGS_CHANCES;
  private static List<Float> BOOTS_CHANCES;

  public static void handleArmorConfig(ArmorConfig raw) {
    raw.fixAll();
    HELMETS = Common.getList(raw.helmets, allowedSlots.get(0));
    CHESTPLATES = Common.getList(raw.chestplates, allowedSlots.get(1));
    LEGGINGS = Common.getList(raw.leggings, allowedSlots.get(2));
    BOOTS = Common.getList(raw.boots, allowedSlots.get(3));

    chances = raw.chancesPerSlot.getChances(4);
    HELMETS_CHANCES = raw.materialWeights.getChances(HELMETS.size());
    CHESTPLATES_CHANCES = raw.materialWeights.getChances(CHESTPLATES.size());
    LEGGINGS_CHANCES = raw.materialWeights.getChances(LEGGINGS.size());
    BOOTS_CHANCES = raw.materialWeights.getChances(BOOTS.size());
  }

  private static ItemStack randomArmor(ServerWorld world, List<Item> items,
                                       List<Float> chances) {
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

  public static class ArmorConfig {
    public List<String> helmets;
    public List<String> chestplates;
    public List<String> leggings;
    public List<String> boots;

    public ChanceList chancesPerSlot;
    public WeightList materialWeights;

    public ArmorConfig(List<String> helmets, List<String> chestplates,
                       List<String> leggings, List<String> boots,
                       ChanceList chances, WeightList weights) {
      this.helmets = helmets;
      this.chestplates = chestplates;
      this.leggings = leggings;
      this.boots = boots;

      this.chancesPerSlot = chances;
      this.materialWeights = weights;
    }

    private static String fixPrefix(String prefix) {
      final var temp = prefix.trim().toLowerCase();
      return allowedMaterials.stream()
          .filter(material -> temp.startsWith(material))
          .findFirst()
          .orElse("");
    }

    private static List<String> fixIt(List<String> materials) {
      return materials.stream()
          .map(ArmorConfig::fixPrefix)
          .filter(f -> f != "")
          .toList();
    }

    private void fixAll() {
      this.helmets = fixIt(this.helmets);
      this.chestplates = fixIt(this.chestplates);
      this.leggings = fixIt(this.leggings);
      this.boots = fixIt(this.boots);
    }
  }
}
