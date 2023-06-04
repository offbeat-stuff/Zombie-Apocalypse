package io.github.offbeat_stuff.zombie_apocalypse.config;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;

import io.github.offbeat_stuff.zombie_apocalypse.ProbabilityHandler;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class Common {
  public static class SpawnParameters {
    public float chance;
    public int min;
    public int max;

    public SpawnParameters(float chance, int min, int max) {
      this.chance = chance;
      this.min = min;
      this.max = max;
    }

    public int generateExclusive() {
      return XRANDOM.nextBetween(this.min, this.max) *
          ((XRANDOM.nextInt(2) * 2) - 1);
    }

    public int generateInclusive() {
      return XRANDOM.nextBetween(-this.max, this.max);
    }
  }

  public static class Range {
    public int min;
    public int max;

    public Range(int min, int max) {
      this.min = min;
      this.max = max;
    }

    public boolean test(int v) { return v >= this.min && v <= this.max; }

    public int generate() { return XRANDOM.nextBetween(this.min, this.max); }

    public Predicate<Integer> toModPredicate(int m) {
      int min = this.min % m;
      int max = this.max % m;
      if (min < max) {
        return (t) -> (t % m) >= min && (t % m) <= max;
      }
      return (t) -> (t % m) <= max || (t % m) >= min;
    }
  }

  public static Item getItem(String idString) {
    var id = new Identifier(idString);
    if (!Registries.ITEM.containsId(id))
      return null;
    return Registries.ITEM.get(id);
  }

  public static List<Item> getItems(List<String> idStrings) {
    return idStrings.stream()
        .map(Common::getItem)
        .filter(f -> f != null)
        .toList();
  }

  public static ItemStack randomEnchanctedItemStack(List<Item> items,
                                                    List<Float> chances) {
    Item item = ProbabilityHandler.chooseRandom(items, chances);
    if (item == null) {
      return ItemStack.EMPTY;
    }
    return EnchantmentHelper.enchant(
        XRANDOM, item.getDefaultStack(),
        ConfigHandler.enchantmentLevelRange.generate(), true);
  }

  public static String append(String prefix, String suffix) {
    if (prefix.equals("gold")) {
      prefix = "golden";
    }

    return prefix + "_" + suffix;
  }

  public static List<Item> getList(List<String> list, String suffix) {
    return list.stream()
        .map(prefix -> Common.append(prefix, suffix))
        .map(Common::getItem)
        .filter(f -> f != null)
        .toList();
  }
}
