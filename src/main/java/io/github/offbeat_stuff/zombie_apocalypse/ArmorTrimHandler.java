package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.Utils.*;

import io.github.offbeat_stuff.zombie_apocalypse.config.Config.TrimConfig;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class ArmorTrimHandler {

  public static final ObjectList<String> vanillaMaterials =
      ObjectList.of("quartz", "iron", "netherite", "redstone", "copper", "gold",
                    "emerald", "diamond", "lapis", "amethyst");

  public static final ObjectList<String> vanillaPatterns =
      ObjectList.of("sentry", "dune", "coast", "wild", "ward", "eye", "vex",
                    "tide", "snout", "rib", "spire");

  public static WeightedList<Identifier> materials;
  public static WeightedList<Identifier> patterns;

  public static double chance;

  public static void applyRandomArmorTrim(ServerWorld world, ItemStack stack) {
    if (!(stack.getItem() instanceof ArmorItem)) {
      return;
    }

    if (!roll(chance)) {
      return;
    }

    VersionDependent.applyArmorTrim(world, stack, materials.spit(),
                                    patterns.spit());
  }

  public static void load(TrimConfig conf) {
    materials = new WeightedList<Identifier>(identified(conf.materials),
                                             conf.materialWeights);
    patterns = new WeightedList<Identifier>(identified(conf.patterns),
                                            conf.patternWeights);

    chance = conf.chance;
  }
}
