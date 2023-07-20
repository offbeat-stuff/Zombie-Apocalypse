package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;

import io.github.offbeat_stuff.zombie_apocalypse.config.Common;
import io.github.offbeat_stuff.zombie_apocalypse.config.WeaponConfig;
import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class WeaponHandler {

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

  public static void handleWeaponConfig(WeaponConfig raw) {
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

  public static void handleZombie(ServerWorld world, LivingEntity entity) {
    if (!ProbabilityHandler.tryChance(chance)) {
      return;
    }

    entity.equipStack(EquipmentSlot.MAINHAND, randomTool());
  }
}
