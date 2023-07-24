package io.github.offbeat_stuff.zombie_apocalypse;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public interface ZombieEntityInterface {
  void setKind(ZombieKind kind);

  ZombieKind getKind();

  public static String nbt_key = "apocalypse_variant";

  default void readNbt(NbtCompound nbt) {
    if (nbt.contains(nbt_key, NbtElement.INT_TYPE)) {
      this.setKind(ZombieKind.fromIndex(nbt.getInt(nbt_key)));
    }
  }

  default void writeNbt(NbtCompound nbt) {
    nbt.putInt(nbt_key, this.getKind().toInt());
  }
}
