package io.github.offbeat_stuff.zombie_apocalypse;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.world.ServerWorld;

public class ArmorTrimHander {

  private static <T> RegistryEntry<T> getRandomEntry(Registry<T> registry) {
    var entries = registry.stream().toList();

    if (entries.isEmpty()) {
      return null; // Handle the case when the registry is empty
    }

    int randomIndex = ZombieMod.XRANDOM.nextInt(entries.size());
    return registry.getEntry(entries.get(randomIndex));
  }

  public static void applyRandomArmorTrim(ServerWorld world, ItemStack istack) {
    if (!(world.getEnabledFeatures().contains(FeatureFlags.UPDATE_1_20))) {
      return;
    }

    if (!(istack.getItem() instanceof ArmorItem)) {
      return;
    }
    var pattern = getRandomEntry(
        world.getRegistryManager().get(RegistryKeys.TRIM_PATTERN));
    var material = getRandomEntry(
        world.getRegistryManager().get(RegistryKeys.TRIM_MATERIAL));
    var trim = new ArmorTrim(material, pattern);
    ArmorTrim.apply(world.getRegistryManager(), istack, trim);
  }
}
