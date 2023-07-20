package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.WeaponHandler.allowedMaterials;

import io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.WeightList;
import java.util.List;
import net.minecraft.util.math.MathHelper;

public class WeaponConfig {

  public List<String> swords;
  public List<String> shovels;
  public List<String> pickaxes;
  public List<String> axes;
  public List<String> hoes;
  public WeightList weightsForTools;
  public WeightList commonWeights;

  public float chance;

  public WeaponConfig(List<String> swords, List<String> shovels,
                      List<String> pickaxes, List<String> axes,
                      List<String> hoes, WeightList weights, WeightList common,
                      float chance) {
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
        .map(WeaponConfig::fixPrefix)
        .filter(f -> f != "")
        .toList();
  }

  public void fixAll() {
    this.swords = fixIt(this.swords);
    this.shovels = fixIt(this.shovels);
    this.pickaxes = fixIt(this.pickaxes);
    this.axes = fixIt(this.axes);
    this.hoes = fixIt(this.hoes);

    this.chance = MathHelper.clamp(this.chance, 0f, 1f);
  }
}
