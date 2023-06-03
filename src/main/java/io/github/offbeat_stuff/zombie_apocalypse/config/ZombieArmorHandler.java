package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.tryChance;

import io.github.offbeat_stuff.zombie_apocalypse.ArmorTrimHander;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class ZombieArmorHandler {
  private static List<Item> HELMETS;
  private static List<Item> CHESTPLATES;
  private static List<Item> LEGGINGS;
  private static List<Item> BOOTS;

  private static List<Float> chances;
  private static List<Float> HELMETS_CHANCES;
  private static List<Float> CHESTPLATES_CHANCES;
  private static List<Float> LEGGINGS_CHANCES;
  private static List<Float> BOOTS_CHANCES;

  public static void handleRawArmorHandler(RawArmorHandler raw) {
    raw.fixAll();
    HELMETS = getList(raw.helmets, "helmet");
    CHESTPLATES = getList(raw.chestplates, "chestplate");
    LEGGINGS = getList(raw.leggings, "leggings");
    BOOTS = getList(raw.boots, "boots");

    chances = raw.chancesPerSlot;
    HELMETS_CHANCES = ConfigHandler.generateChances(
        HELMETS.size(), raw.materialWeights, raw.weightForExtraMaterials);
    CHESTPLATES_CHANCES = ConfigHandler.generateChances(
        CHESTPLATES.size(), raw.materialWeights, raw.weightForExtraMaterials);
    LEGGINGS_CHANCES = ConfigHandler.generateChances(
        LEGGINGS.size(), raw.materialWeights, raw.weightForExtraMaterials);
    BOOTS_CHANCES = ConfigHandler.generateChances(
        BOOTS.size(), raw.materialWeights, raw.weightForExtraMaterials);
  }

  private static ItemStack randomArmor(ServerWorld world, List<Item> items,
                                       List<Float> chances) {
    var r = Common.randomEnchanctedItemStack(items, chances);
    ArmorTrimHander.applyRandomArmorTrim(world, r);
    return r;
  }

  public static void handleZombie(ServerWorld world, ZombieEntity entity) {
    if (tryChance(chances.get(0))) {
      entity.equipStack(EquipmentSlot.HEAD,
                        randomArmor(world, HELMETS, HELMETS_CHANCES));
    }
    if (tryChance(chances.get(1))) {
      entity.equipStack(EquipmentSlot.HEAD,
                        randomArmor(world, CHESTPLATES, CHESTPLATES_CHANCES));
    }
    if (tryChance(chances.get(2))) {
      entity.equipStack(EquipmentSlot.HEAD,
                        randomArmor(world, LEGGINGS, LEGGINGS_CHANCES));
    }
    if (tryChance(chances.get(3))) {
      entity.equipStack(EquipmentSlot.HEAD,
                        randomArmor(world, BOOTS, BOOTS_CHANCES));
    }
  }

  private static String append(String prefix, String suffix) {
    if (prefix.equals("gold")) {
      prefix = "golden";
    }

    return prefix + "_" + suffix;
  }

  private static List<Item> getList(List<String> list, String suffix) {
    return list.stream()
        .map(prefix -> append(prefix, suffix))
        .map(Common::getItem)
        .filter(f -> f != null)
        .toList();
  }

  public static class RawArmorHandler {
    public static final List<String> allowedMaterials =
        List.of("netherite", "diamond", "iron", "gold", "chainmail", "leather",
                "turtle");
    public List<String> helmets;
    public List<String> chestplates;
    public List<String> leggings;
    public List<String> boots;

    public float defaultChance;
    public List<Float> chancesPerSlot;
    public int weightForExtraMaterials;
    public List<Integer> materialWeights;

    public RawArmorHandler(List<String> helmets, List<String> chestplates,
                           List<String> leggings, List<String> boots,
                           float defaultChance, List<Float> chances,
                           int extraWeights, List<Integer> weights) {
      this.helmets = helmets;
      this.chestplates = chestplates;
      this.leggings = leggings;
      this.boots = boots;

      this.defaultChance = defaultChance;
      this.chancesPerSlot = chances;
      this.weightForExtraMaterials = extraWeights;
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
          .map(RawArmorHandler::fixPrefix)
          .filter(f -> f != "")
          .toList();
    }

    private static float clamp(float r, float min, float max) {
      return Math.max(min, Math.min(r, max));
    }

    private float getChanceAt(int i) {
      return i < this.chancesPerSlot.size()
          ? clamp(this.chancesPerSlot.get(i), 0f, 1f)
          : this.defaultChance;
    }

    private void fixAll() {
      this.helmets = fixIt(this.helmets);
      this.chestplates = fixIt(this.chestplates);
      this.leggings = fixIt(this.leggings);
      this.boots = fixIt(this.boots);

      this.defaultChance = clamp(this.defaultChance, 0f, 1f);

      this.chancesPerSlot =
          IntStream.range(0, 4).mapToObj(this::getChanceAt).toList();

      this.weightForExtraMaterials = Math.abs(this.weightForExtraMaterials);
      this.materialWeights =
          this.materialWeights.stream().map(Math::abs).toList();
    }
  }
}