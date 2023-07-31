package io.github.offbeat_stuff.zombie_apocalypse;

import net.minecraft.nbt.NbtCompound;

public interface ZombieEntityInterface {
  void setKind(ZombieKind kind);

  ZombieKind getKind();

  public static String nbt_key = "apocalypse_variant";

  default void readNbtApocalypse(NbtCompound nbt) {
    if (nbt.contains(nbt_key, 99)) {
      this.setKind(ZombieKind.fromIndex(nbt.getInt(nbt_key)));
    }
  }

  default void writeNbtApocalypse(NbtCompound nbt) {
    nbt.putInt(nbt_key, this.getKind().toInt());
  }
}
