package io.github.offbeat_stuff.zombie_apocalypse.mixin;

import io.github.offbeat_stuff.zombie_apocalypse.EquipmentHandler;
import io.github.offbeat_stuff.zombie_apocalypse.ZombieEntityInterface;
import io.github.offbeat_stuff.zombie_apocalypse.ZombieKind;
import io.github.offbeat_stuff.zombie_apocalypse.config.ConfigHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin implements ZombieEntityInterface {

  private ZombieKind kind;

  @Inject(method = "burnsInDaylight", at = @At("HEAD"), cancellable = true)
  void dontBurn(CallbackInfoReturnable<Boolean> cir) {
    cir.setReturnValue(ConfigHandler.zombiesBurnInSunlight);
  }

  @Override
  public void setKind(ZombieKind kind) {
    this.kind = kind;
  }

  @Override
  public ZombieKind getKind() {
    if (this.kind == null) {
      this.kind = ZombieKind.Simple;
    }
    return this.kind;
  }

  @Inject(method = "tryAttack", at = @At("RETURN"), cancellable = true)
  private void onTryAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
    if (!cir.getReturnValue()) {
      return;
    }
    this.kind.attack(target);
  }

  @Inject(
      method = "initialize",
      at = @At(
          value = "INVOKE",
          target =
              "Lnet/minecraft/entity/mob/ZombieEntity;initEquipment(Lnet/minecraft/util/math/random/Random;Lnet/minecraft/world/LocalDifficulty;)V")
      ,
      cancellable = true)
  private void
  handleEquipment(CallbackInfoReturnable<?> ci) {
    ci.cancel();
    var zombie = (ZombieEntity)(Object)this;

    if (!(zombie.getWorld() instanceof ServerWorld world)) {
      return;
    }

    EquipmentHandler.handleZombie(world, zombie);
  }
}
