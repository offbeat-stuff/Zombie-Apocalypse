package io.github.offbeat_stuff.zombie_apocalypse;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sound;
import net.minecraft.util.Identifier;

public class VersionDependent {

  public static void applyArmorTrim(ServerWorld world, ItemStack stack,
                                    Identifier materialId,
                                    Identifier patternId) {}

  @SuppressWarnings("unchecked")
  public static boolean isZombie(String id) {
    var entity = EntityType.REGISTRY.get(new Identifier(id));
    if (entity == null) {
      return false;
    }

    try {
      var z = (Class<? extends ZombieEntity>)entity;
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  public static Class<? extends ZombieEntity> getZombie(String id) {
    var entity = EntityType.REGISTRY.get(new Identifier(id));
    if (entity == null) {
      return null;
    }
    try {
      return (Class<? extends ZombieEntity>)entity;
    } catch (Exception e) {
      return null;
    }
  }

  public static boolean isStatusEffect(String id) {
    return StatusEffect.REGISTRY.containsKey(new Identifier(id));
  }

  public static StatusEffect getStatusEffect(String id) {
    return StatusEffect.REGISTRY.get(new Identifier(id));
  }

  public static boolean isSound(String id) {
    return Sound.REGISTRY.containsKey(new Identifier(id));
  }

  public static Sound getSound(String id) {
    return Sound.REGISTRY.get(new Identifier(id));
  }
}
