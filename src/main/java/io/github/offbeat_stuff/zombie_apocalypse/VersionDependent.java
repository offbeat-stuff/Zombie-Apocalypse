package io.github.offbeat_stuff.zombie_apocalypse;

import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.world.ServerWorld;
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
    if (!(world.getEnabledFeatures().contains(FeatureFlags.UPDATE_1_20))) {
      return;
    }

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
}
