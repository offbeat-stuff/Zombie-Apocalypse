package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;
import java.util.List;
import java.util.stream.IntStream;

public class WeightedList<T> {
  private final List<T> items;
  private final IntList weights;
  private final int sum;
  private final int right;

  public WeightedList(List<List<T>> items, List<IntList> itemWeights,
                      IntList weights) {
    this(IntStream.range(0, items.size())
             .mapToObj(i
                       -> filter(items.get(i), multiply(itemWeights.get(i),
                                                        weights.getInt(i))))
             .flatMap(c -> c.stream())
             .collect(ObjectImmutableList.toList()));
  }

  public WeightedList(List<List<T>> items, IntList itemWeights,
                      IntList weights) {
    this(IntStream.range(0, items.size())
             .mapToObj(i
                       -> filter(items.get(i),
                                 multiply(itemWeights, weights.getInt(i))))
             .flatMap(c -> c.stream())
             .collect(ObjectImmutableList.toList()));
  }

  private static IntList multiply(IntList list, int m) {
    return IntImmutableList.toList(list.intStream().map(i -> i * m));
  }

  public WeightedList(List<T> items, IntList weights) {
    this(filter(items, weights));
  }

  public WeightedList(ObjectList<ObjectIntPair<T>> items) {
    var cumSum = 0;
    var itemsFiltered = new ObjectArrayList<T>(items.size());
    var weightsFiltered = new IntArrayList(items.size());
    for (var item : items) {
      cumSum += item.rightInt();
      weightsFiltered.add(cumSum);
      itemsFiltered.add(item.left());
    }
    this.items = new ObjectImmutableList<T>(itemsFiltered);
    this.weights = new IntImmutableList(weightsFiltered);
    this.sum = cumSum;
    this.right = this.weights.size() - 1;
  }

  private static <T> ObjectList<ObjectIntPair<T>> filter(List<T> items,
                                                         IntList weights) {
    return IntStream.range(0, items.size())
        .filter(i -> weights.getInt(i) != 0)
        .mapToObj(i -> ObjectIntPair.of(items.get(i), weights.getInt(i)))
        .collect(ObjectImmutableList.toList());
  }

  public T spit() {
    if (this.items.isEmpty()) {
      return null;
    }
    int randomNumber = XRANDOM.nextInt(this.sum);
    int index = findIndex(randomNumber);
    return this.items.get(index);
  }

  /*
   * [10,20,30,40,50,60,70,80,90]
   * rand = 44 l,r = 0,8 => 0,4 => 3,4 => 4,4 => 50
   * rand = 55 l,r = 0,8 => 5,8 => 5,6 => 5,5 => 60
   * [10,20,30,40,50,60,70,80]
   * rand = 45 l,r = 0,7 => 3,7 => 3,5 => 3,4 => 4,4 => 50
   * rand = 37 l,r = 0,7 => 0,3 => 1,3 => 2,3 => 3,3 => 40
   */
  private int findIndex(int randomNumber) {
    int left = 0;
    int right = this.right;

    while (left < right) {
      int mid = left + (right - left) / 2;
      if (randomNumber <= this.weights.getInt(mid)) {
        right = mid;
      } else {
        left = mid + 1;
      }
    }

    return left;
  }
}
