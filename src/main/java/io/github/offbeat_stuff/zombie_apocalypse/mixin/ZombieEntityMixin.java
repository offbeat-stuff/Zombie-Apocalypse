package io.github.offbeat_stuff.zombie_apocalypse.mixin;

import io.github.offbeat_stuff.zombie_apocalypse.ZombieEntityInterface;
import io.github.offbeat_stuff.zombie_apocalypse.ZombieKind;
import io.github.offbeat_stuff.zombie_apocalypse.config.ConfigHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin implements ZombieEntityInterface {

  private ZombieKind kind = ZombieKind.Simple;

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
    return this.kind;
  }

  @Inject(method = "tryAttack", at = @At("RETURN"), cancellable = true)
  private void onTryAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
    if (!cir.getReturnValue()) {
      return;
    }
    this.kind.attack(target);
  }

  @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
  private void nbtIn(NbtCompound nbt, CallbackInfo info) {
    this.readNbtApocalypse(nbt);
  }

  @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
  private void nbtOut(NbtCompound nbt, CallbackInfo info) {
    this.writeNbtApocalypse(nbt);
  }
}