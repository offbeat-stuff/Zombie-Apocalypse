package io.github.offbeat_stuff.zombie_apocalypse;

import static net.minecraft.util.math.MathHelper.clamp;

import java.util.List;
import java.util.stream.IntStream;

public class ProbabilityHandler {

  public static <T> T chooseRandom(List<T> items, List<Float> chances) {

    if (items.size() == 0) {
      return null;
    }

    var r = Math.min(items.size(), chances.size());
    var f = ZombieMod.XRANDOM.nextFloat();
    for (int i = 0; i < r - 1; i++) {
      f -= chances.get(i);
      if (f <= 0) {
        return items.get(i);
      }
    }
    return items.get(items.size() - 1);
  }

  public static boolean tryChance(float chance) {
    return ZombieMod.XRANDOM.nextFloat() < chance;
  }

  public static class ChanceList {
    public List<Float> chances;
    public float defaultChance;

    public ChanceList(List<Float> chances, float defaultChance) {
      this.defaultChance = defaultChance;
      this.chances = chances;
    }

    public List<Float> getChances(int len) {
      return IntStream.range(0, len)
          .mapToObj(idx
                    -> idx < this.chances.size()
                           ? clamp(this.chances.get(idx), 0f, 1f)
                           : clamp(this.defaultChance, 0f, 1f))
          .toList();
    }
  }

  public static class WeightList {
    public List<Integer> weights;
    public int weightsForExtraEntries;

    public WeightList(List<Integer> weights, int weightsForExtraEntries) {
      this.weights = weights;
      this.weightsForExtraEntries = weightsForExtraEntries;
    }

    private static int getDistributedWeightAt(int idx, int extraWeights,
                                              int extra) {
      return (extraWeights / extra) + (idx < (extraWeights % extra) ? 1 : 0);
    }

    public List<Float> getChances(int len) {
      int size = this.weights.size();
      var chances =
          IntStream.range(0, len)
              .mapToObj(idx
                        -> idx < size
                               ? Math.abs(weights.get(idx))
                               : getDistributedWeightAt(
                                     idx - size,
                                     Math.abs(this.weightsForExtraEntries),
                                     len - size))
              .toList();
      var sum = (float)chances.stream().mapToInt(Integer::intValue).sum();
      return chances.stream().map(f -> (float)f / sum).toList();
    }
  }
}
