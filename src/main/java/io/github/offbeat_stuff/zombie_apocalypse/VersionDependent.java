package io.github.offbeat_stuff.zombie_apocalypse;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class VersionDependent {

  public static void applyArmorTrim(ServerWorld world, ItemStack stack,
                                    Identifier materialId,
                                    Identifier patternId) {}

  @SuppressWarnings("unchecked")
  public static boolean isZombie(String id) {
    var entity = Registry.ENTITY_TYPE.get(new Identifier(id));
    if (entity == null) {
      return false;
    }

    try {
      var z = (EntityType<? extends ZombieEntity>)entity;
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  public static EntityType<? extends ZombieEntity> getZombie(String id) {
    var entity = Registry.ENTITY_TYPE.get(new Identifier(id));
    if (entity == null) {
      return null;
    }
    try {
      return (EntityType<? extends ZombieEntity>)entity;
    } catch (Exception e) {
      return null;
    }
  }

  public static boolean isStatusEffect(String id) {
    return Registry.STATUS_EFFECT.containsId(new Identifier(id));
  }

  public static StatusEffect getStatusEffect(String id) {
    return Registry.STATUS_EFFECT.get(new Identifier(id));
  }

  public static boolean isSound(String id) {
    return Registry.SOUND_EVENT.get(new Identifier(id)) != null;
  }

  public static SoundEvent getSound(String id) {
    return Registry.SOUND_EVENT.get(new Identifier(id));
  }
}
