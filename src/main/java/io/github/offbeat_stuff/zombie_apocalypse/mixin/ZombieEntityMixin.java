package io.github.offbeat_stuff.zombie_apocalypse.mixin;

import io.github.offbeat_stuff.zombie_apocalypse.ZombieEntityInterface;
import io.github.offbeat_stuff.zombie_apocalypse.config.ConfigHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public class ZombieEntityMixin implements ZombieEntityInterface {

  private String zombieType;

  @Inject(method = "burnsInDaylight", at = @At("HEAD"), cancellable = true)
  void dontBurn(CallbackInfoReturnable<Boolean> cir) {
    cir.setReturnValue(ConfigHandler.zombiesBurnInSunlight);
  }

  @Override
  public void setZombieType(String zombieType) {
    this.zombieType = zombieType;
  }

  @Override
  public String getZombieType() {
    return this.zombieType;
  }

  @Inject(method = "tryAttack", at = @At("RETURN"), cancellable = true)
  private void onTryAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
    if (!cir.getReturnValue()) {
      return;
    }
    if (this.zombieType == "frost") {
      target.setFrozenTicks(target.getMinFreezeDamageTicks() + 10);
    }
    if (this.zombieType == "fire") {
      target.setOnFire(true);
    }
  }
}
