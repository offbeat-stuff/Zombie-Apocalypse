package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;

import it.unimi.dsi.fastutil.doubles.DoubleImmutableList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.stream.IntStream;
import net.minecraft.util.math.MathHelper;

public class Utils {

  public static IntList setSize(IntList list, int len) {
    return IntImmutableList.toList(IntStream.range(0, len).map(
        f -> f < list.size() ? Math.max(0, list.getInt(f)) : 0));
  }

  public static DoubleList correct(DoubleList list) {
    return DoubleImmutableList.toList(list.stream().map(f -> validate(f)));
  }

  public static DoubleList setSize(DoubleList list, int len) {
    return DoubleList.toList(IntStream.range(0, len).mapToObj(
        f -> f < list.size() ? clamp(list.getDouble(f)) : 0f));
  }

  public static double clamp(double chance) {
    return MathHelper.clamp(chance, 0f, 1f);
  }

  public static int validate(int v) { return v > 0 ? v : 0; }

  public static int validate(int v, int min) { return v > min ? v : min; }

  public static int validate(int v, int min, int max) {
    if (v > max)
      return max;
    if (v < min)
      return min;
    return v;
  }

  public static boolean roll(double chance) {
    return XRANDOM.nextDouble() < chance;
  }
}
