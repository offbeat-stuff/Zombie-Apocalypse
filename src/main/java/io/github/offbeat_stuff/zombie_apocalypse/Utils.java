package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;
import static net.minecraft.util.math.MathHelper.clamp;

import it.unimi.dsi.fastutil.floats.*;
import it.unimi.dsi.fastutil.ints.*;
import java.util.stream.IntStream;

public class Utils {

  public static IntList setSize(IntList list, int len) {
    return IntImmutableList.toList(IntStream.range(0, len).map(
        f -> f < list.size() ? Math.max(0, list.get(f)) : 0));
  }

  public static FloatList correct(FloatList list) {
    return FloatImmutableList.toList(list.stream().map(f -> validate(f)));
  }

  public static FloatList setSize(FloatList list, int len) {
    return FloatImmutableList.toList(IntStream.range(0, len).map(
        f -> f < list.size() ? validate(list.get(i)) : 0f));
  }

  public static T clamp(float chance, float min, float max) {
    if (chance > max)
      return max;
    if (chance < min)
      return min;
    return chance;
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

  public static boolean roll(float chance) {
    return XRANDOM.nextFloat() < chance;
  }
}
