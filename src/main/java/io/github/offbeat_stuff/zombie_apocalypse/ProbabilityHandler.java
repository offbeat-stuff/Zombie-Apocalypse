package io.github.offbeat_stuff.zombie_apocalypse;

import java.util.List;

public class ProbabilityHandler {

    public static void fillUp(List<Float> f, int len) {
        var parts = len - f.size();
        var start = f.get(f.size() - 1);
        var r = (float) Math.pow(start, -(1f / (float) parts));
        for (int i = 0; i < parts; i++) {
            start *= r;
            f.add(start);
        }
    }

    public static <T> T chooseRandom(List<T> items, List<Float> chances) {
        if (chances.size() < items.size()) {
            fillUp(chances, items.size());
        }
        var f = ZombieMod.XRANDOM.nextFloat();
        for (int i = 0; i < items.size() - 1; i++) {
            if (f < chances.get(i)) {
                return items.get(i);
            }
        }
        return items.get(items.size() - 1);
    }
}
