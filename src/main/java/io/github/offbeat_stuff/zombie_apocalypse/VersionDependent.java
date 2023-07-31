package io.github.offbeat_stuff.zombie_apocalypse;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class VersionDependent {
  private static <T> RegistryEntry<T>
  getEntry(ServerWorld world, RegistryKey<Registry<T>> key, Identifier id) {
    var registry = world.getRegistryManager().get(key);
    var value = registry.get(id);
    if (value == null) {
      return null;
    }

    return registry.getEntry(value);
  }

  public static void applyArmorTrim(ServerWorld world, ItemStack stack,
                                    Identifier materialId,
                                    Identifier patternId) {
    var material = getEntry(world, RegistryKeys.TRIM_MATERIAL, materialId);
    if (material == null) {
      return;
    }

    var pattern = getEntry(world, RegistryKeys.TRIM_PATTERN, patternId);
    if (pattern == null) {
      return;
    }

    ArmorTrim.apply(world.getRegistryManager(), stack,
                    new ArmorTrim(material, pattern));
  }

  @SuppressWarnings("unchecked")
  public static boolean isZombie(String id) {
    var entity = Registries.ENTITY_TYPE.get(new Identifier(id));
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
    var entity = Registries.ENTITY_TYPE.get(new Identifier(id));
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
    return Registries.STATUS_EFFECT.containsId(new Identifier(id));
  }

  public static StatusEffect getStatusEffect(String id) {
    return Registries.STATUS_EFFECT.get(new Identifier(id));
  }

  public static RegistryEntry<SoundEvent> getSound(String id) {
    var sound = Registries.SOUND_EVENT.get(new Identifier(id));
    return Registries.SOUND_EVENT.getEntry(sound);
  }
}
