package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.ArmorHandler.allowedMaterials;

import io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.ChanceList;
import io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler.WeightList;
import java.util.List;

public class ArmorConfig {
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

  public void fixAll() {
    this.helmets = fixIt(this.helmets);
    this.chestplates = fixIt(this.chestplates);
    this.leggings = fixIt(this.leggings);
    this.boots = fixIt(this.boots);
  }
}
