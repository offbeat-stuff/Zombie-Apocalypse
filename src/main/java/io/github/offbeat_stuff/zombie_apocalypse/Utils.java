package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;

import it.unimi.dsi.fastutil.chars.CharPredicate;
import it.unimi.dsi.fastutil.doubles.DoubleImmutableList;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class Utils {

  public static List<Integer> withSizeI(List<Integer> list, int len) {
    return IntImmutableList.toList(IntStream.range(0, len).map(
        f -> f < list.size() ? natural(list.get(f)) : 0));
  }

  public static List<Double> chances(List<Double> list) {
    return DoubleImmutableList.toList(
        list.stream().mapToDouble(f -> chance(f)));
  }

  public static List<Double> withSize(List<Double> list, int len) {
    return DoubleImmutableList.toList(IntStream.range(0, len).mapToDouble(
        f -> f < list.size() ? chance(list.get(f)) : 0f));
  }

  public static double chance(double chance) {
    return MathHelper.clamp(chance, 0f, 1f);
  }

  public static int natural(int v) { return v > 0 ? v : 0; }

  public static boolean roll(double chance) {
    return XRANDOM.nextDouble() < chance;
  }

  public static List<Identifier> identified(List<String> ids) {
    return ids.stream()
        .map(id -> new Identifier(id))
        .collect(ObjectImmutableList.toList());
  }

  public static List<String> identifiers(List<String> ids,
                                         Predicate<String> filter) {
    return ids.stream()
        .map(id -> identifier(id))
        .filter(filter)
        .collect(ObjectImmutableList.toList());
  }

  public static List<String> identifiers(List<String> ids) {
    return ids.stream()
        .map(id -> identifier(id))
        .collect(ObjectImmutableList.toList());
  }

  public static String identifier(String id) {
    var i = id.indexOf(':');
    if (i < 0) {
      return path(id);
    }
    if (i == 0) {
      return path(id.substring(i + 1));
    }
    return namespace(id.substring(0, i)) + ':' + path(id.substring(i + 1));
  }

  public static String path(String path) {
    return filter(path.toLowerCase(),
                  c
                  -> c == '_' || c == '-' || c >= 'a' && c <= 'z' ||
                         c >= '0' && c <= '9' || c == '.' || c == '/');
  }

  public static String namespace(String namespace) {
    return filter(namespace.toLowerCase(),
                  c
                  -> c == '_' || c == '-' || c >= 'a' && c <= 'z' ||
                         c >= '0' && c <= '9' || c == '.');
  }

  private static String filter(String str, CharPredicate test) {
    var builder = new StringBuilder(str.length());
    for (int i = 0; i < str.length(); i++) {
      var c = str.charAt(i);
      if (test.test(c)) {
        builder.append(c);
      }
    }
    return builder.toString();
  }
}
