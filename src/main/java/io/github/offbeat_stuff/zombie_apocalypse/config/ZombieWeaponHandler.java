package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;

import io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler;
import io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.WeightList;
import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public class ZombieWeaponHandler {

  public static final List<String> allowedMaterials =
      List.of("netherite", "diamond", "iron", "gold", "stone", "wood");
  public static final List<String> allowedTools =
      List.of("sword", "shovel", "pickaxe", "axe", "hoe");

  private static List<Item> SWORDS;
  private static List<Item> SHOVELS;
  private static List<Item> PICKAXES;
  private static List<Item> AXES;
  private static List<Item> HOES;

  private static List<Float> CHANCES;
  private static List<Float> SWORDS_CHANCES;
  private static List<Float> SHOVELS_CHANCES;
  private static List<Float> PICKAXES_CHANCES;
  private static List<Float> AXES_CHANCES;
  private static List<Float> HOES_CHANCES;

  private static float chance;

  public static void handleRawWeaponHandler(RawWeaponHandler raw) {
    raw.fixAll();
    SWORDS = Common.getList(raw.swords, allowedTools.get(0));
    SHOVELS = Common.getList(raw.shovels, allowedTools.get(1));
    PICKAXES = Common.getList(raw.pickaxes, allowedTools.get(2));
    AXES = Common.getList(raw.axes, allowedTools.get(3));
    HOES = Common.getList(raw.hoes, allowedTools.get(4));

    CHANCES = raw.weightsForTools.getChances(5);
    SWORDS_CHANCES = raw.commonWeights.getChances(SWORDS.size());
    SHOVELS_CHANCES = raw.commonWeights.getChances(SHOVELS.size());
    PICKAXES_CHANCES = raw.commonWeights.getChances(PICKAXES.size());
    AXES_CHANCES = raw.commonWeights.getChances(AXES.size());
    HOES_CHANCES = raw.commonWeights.getChances(HOES.size());

    chance = raw.chance;
  }

  private static ItemStack randomTool() {
    var chance = XRANDOM.nextFloat();
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

  public static void handleZombie(ServerWorld world, ZombieEntity entity) {
    if (!ProbabilityHandler.tryChance(chance)) {
      return;
    }

    entity.equipStack(EquipmentSlot.MAINHAND, randomTool());
  }

  public static class RawWeaponHandler {

    public List<String> swords;
    public List<String> shovels;
    public List<String> pickaxes;
    public List<String> axes;
    public List<String> hoes;
    public WeightList weightsForTools;
    public WeightList commonWeights;

    public float chance;

    public RawWeaponHandler(List<String> swords, List<String> shovels,
                           List<String> pickaxes, List<String> axes,
                           List<String> hoes, WeightList weights,
                           WeightList common, float chance) {
      this.swords = swords;
      this.shovels = shovels;
      this.pickaxes = pickaxes;
      this.axes = axes;
      this.hoes = hoes;
      this.weightsForTools = weights;
      this.commonWeights = common;
      this.chance = chance;
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
          .map(RawWeaponHandler::fixPrefix)
          .filter(f -> f != "")
          .toList();
    }

    private void fixAll() {
      this.swords = fixIt(this.swords);
      this.shovels = fixIt(this.shovels);
      this.pickaxes = fixIt(this.pickaxes);
      this.axes = fixIt(this.axes);
      this.hoes = fixIt(this.hoes);

      this.chance = MathHelper.clamp(this.chance, 0f, 1f);
    }
  }
}
