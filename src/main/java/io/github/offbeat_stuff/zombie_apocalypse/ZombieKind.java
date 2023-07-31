package io.github.offbeat_stuff.zombie_apocalypse;

import net.minecraft.entity.Entity;

public enum ZombieKind {
  Simple,
  Frost,
  Flame;

  public void attack(Entity target) {
    switch (this) {
    case Frost:
      // target.setFrozenTicks(target.getMinFreezeDamageTicks() + 40);
      break;
    case Flame:
      target.setOnFireFor(40);
    default:
      break;
    }
  }

  public int toInt() {
    int i = 0;
    for (var v : values()) {
      if (this.equals(v)) {
        return i;
      }
      i++;
    }
    return 0;
  }

  public static ZombieKind fromIndex(int i) {
    return values()[Math.max(0, i % values().length)];
  }
}