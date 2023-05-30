package io.github.offbeat_stuff.zombie_apocalypse.mixin;

import io.github.offbeat_stuff.zombie_apocalypse.ZombieMod;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public class ZombieEntityMixin {

  @Inject(method = "burnsInDaylight", at = @At("HEAD"), cancellable = true)
  void dontBurn(CallbackInfoReturnable<Boolean> cir) {
    cir.setReturnValue(ZombieMod.config.zombiesBurnInSunlight);
  }
}
