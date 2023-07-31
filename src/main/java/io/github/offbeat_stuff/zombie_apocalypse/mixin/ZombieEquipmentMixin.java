package io.github.offbeat_stuff.zombie_apocalypse.mixin;

import io.github.offbeat_stuff.zombie_apocalypse.EquipmentHandler;
import io.github.offbeat_stuff.zombie_apocalypse.SpawnHandler;
import java.util.Random;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LocalDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ZombieEntity.class, DrownedEntity.class, ZombifiedPiglinEntity.class})
public abstract class ZombieEquipmentMixin {
  @Inject(method = "initEquipment", at = @At("HEAD"), cancellable = true)
  private void initEquipment(Random random, LocalDifficulty difficulty,
                             CallbackInfo ci) {
    var zombie = (ZombieEntity)(Object)this;

    if (!SpawnHandler.isPartOfApocalypse(zombie)) {
      return;
    }

    ci.cancel();
    if (!(zombie.world instanceof ServerWorld world)) {
      return;
    }

    EquipmentHandler.initEquipment(world, zombie);
  }
}
