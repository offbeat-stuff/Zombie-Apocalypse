package io.github.offbeat_stuff.zombie_apocalypse.mixin;

import io.github.offbeat_stuff.zombie_apocalypse.EquipmentHandler;
import io.github.offbeat_stuff.zombie_apocalypse.SpawnHandler;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobEntityMixin {
  @Inject(method = "updateEnchantments", at = @At("HEAD"), cancellable = true)
  private void updateEnchantments(Random random, LocalDifficulty difficulty,
                                    CallbackInfo ci) {
    if (!((Object)this instanceof ZombieEntity)) {
      return;
    }
    var zombie = (ZombieEntity)(Object)this;

    if (!SpawnHandler.isPartOfApocalypse(zombie)) {
      return;
    }

    ci.cancel();
    if (!(zombie.getWorld() instanceof ServerWorld world)) {
      return;
    }

    EquipmentHandler.updateEnchantments(world, zombie);
  }
}
