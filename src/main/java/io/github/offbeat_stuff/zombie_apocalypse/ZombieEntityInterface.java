package io.github.offbeat_stuff.zombie_apocalypse;

import net.minecraft.nbt.CompoundTag;

public interface ZombieEntityInterface {
  void setKind(ZombieKind kind);

  ZombieKind getKind();

  public static String nbt_key = "apocalypse_variant";

  default void readNbtApocalypse(CompoundTag nbt) {
    if (nbt.contains(nbt_key, 99)) {
      this.setKind(ZombieKind.fromIndex(nbt.getInt(nbt_key)));
    }
  }

  default void writeNbtApocalypse(CompoundTag nbt) {
    nbt.putInt(nbt_key, this.getKind().toInt());
  }
}
