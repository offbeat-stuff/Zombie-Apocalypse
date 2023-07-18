package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.chooseRandom;

import io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.WeightList;
import java.util.List;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ArmorTrimHandler {

  public static final List<String> vanillaMaterials =
      List.of("quartz", "iron", "netherite", "redstone", "copper", "gold",
              "emerald", "diamond", "lapis", "amethyst");

  public static final List<String> vanillaPatterns =
      List.of("sentry", "dune", "coast", "wild", "ward", "eye", "vex", "tide",
              "snout", "rib", "spire");

  public static List<Identifier> materials;
  public static List<Identifier> patterns;
  public static List<Float> materialsChances;
  public static List<Float> patternsChances;
  public static float chance;

  public static void applyRandomArmorTrim(ServerWorld world, ItemStack stack) {
    if (!(stack.getItem() instanceof ArmorItem)) {
      return;
    }

    if (!ProbabilityHandler.tryChance(chance)) {
      return;
    }

    var material = chooseRandom(materials, materialsChances);
    var pattern = chooseRandom(patterns, patternsChances);

    VersionDependent.applyArmorTrim(world, stack, material, pattern);
  }

  public static void handleRawTrimHandler(RawTrimHandler raw) {
    raw.fixAll();
    patterns = raw.patterns.stream().map(f -> new Identifier(f)).toList();
    materials = raw.materials.stream().map(f -> new Identifier(f)).toList();
    patternsChances = raw.patternsWeights.getChances(materials.size());
    materialsChances = raw.materialsWeights.getChances(materials.size());
    chance = raw.chance;
  }

  public static class RawTrimHandler {
    public List<String> patterns;
    public List<String> materials;
    public WeightList patternsWeights;
    public WeightList materialsWeights;
    public boolean vanillaOnly;
    public float chance;

    public RawTrimHandler(List<String> patterns, List<String> materials,
                          WeightList patternsWeights,
                          WeightList materialsWeights, boolean vanillaOnly,
                          float chance) {
      this.patterns = patterns;
      this.materials = materials;
      this.patternsWeights = patternsWeights;
      this.materialsWeights = materialsWeights;
      this.vanillaOnly = vanillaOnly;
      this.chance = chance;
    }

    public void fixAll() {
      if (this.vanillaOnly) {
        this.patterns =
            this.patterns.stream()
                .map(f -> (new Identifier(f.trim().toLowerCase())).getPath())
                .filter(f -> vanillaPatterns.contains(f))
                .toList();
        this.materials =
            this.materials.stream()
                .map(f -> (new Identifier(f.trim().toLowerCase())).getPath())
                .filter(f -> vanillaMaterials.contains(f))
                .toList();
      } else {
        this.patterns = this.patterns.stream().map(f -> f.trim()).toList();
        this.materials = this.materials.stream().map(f -> f.trim()).toList();
      }
      this.chance = MathHelper.clamp(chance, 0f, 1f);
    }
  }
}
