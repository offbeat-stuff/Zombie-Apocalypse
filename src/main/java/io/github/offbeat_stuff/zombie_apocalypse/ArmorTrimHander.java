package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;

import io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.WeightList;
import java.util.List;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ArmorTrimHander {

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

  private static <T> RegistryEntry<T> getEntry(Registry<T> registry,
                                               Identifier s) {
    var entry = registry.get(s);

    if (entry == null) {
      return null;
    }

    return registry.getEntry(entry);
  }

  public static void applyRandomArmorTrim(ServerWorld world, ItemStack istack) {
    if (!(world.getEnabledFeatures().contains(FeatureFlags.UPDATE_1_20))) {
      return;
    }

    if (!(istack.getItem() instanceof ArmorItem)) {
      return;
    }

    if (!ProbabilityHandler.tryChance(chance)) {
      return;
    }

    var pattern = getEntry(
        world.getRegistryManager().get(RegistryKeys.TRIM_PATTERN),
        patterns.get(XRANDOM.nextBetweenExclusive(0, patterns.size())));
    if (pattern == null) {
      return;
    }

    var material = getEntry(
        world.getRegistryManager().get(RegistryKeys.TRIM_MATERIAL),
        materials.get(XRANDOM.nextBetweenExclusive(0, materials.size())));
    if (material == null) {
      return;
    }

    var trim = new ArmorTrim(material, pattern);
    ArmorTrim.apply(world.getRegistryManager(), istack, trim);
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
