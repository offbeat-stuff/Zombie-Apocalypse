package io.github.offbeat_stuff.zombie_apocalypse;

import net.minecraft.entity.Entity;

public enum ZombieKind {
  Simple,
  Frost,
  Flame;

  public void attack(Entity target) {
    switch (this) {
    case Frost:
      target.setFrozenTicks(target.getMinFreezeDamageTicks() + 40);
      break;
    case Flame:
      target.setOnFire(true);
    default:
      break;
    }
  }
}