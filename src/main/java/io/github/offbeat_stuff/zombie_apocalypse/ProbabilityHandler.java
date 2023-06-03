package io.github.offbeat_stuff.zombie_apocalypse;

import java.util.List;

public class ProbabilityHandler {

  public static <T> T chooseRandom(List<T> items, List<Float> chances) {
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
}
