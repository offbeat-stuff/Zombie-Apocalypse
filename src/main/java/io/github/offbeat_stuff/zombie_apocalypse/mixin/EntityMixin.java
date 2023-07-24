package io.github.offbeat_stuff.zombie_apocalypse.mixin;

import io.github.offbeat_stuff.zombie_apocalypse.ZombieEntityInterface;
import io.github.offbeat_stuff.zombie_apocalypse.ZombieKind;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(Entity.class)
public abstract class EntityMixin {

  @Inject(method = "isFireImmune", at = @At("HEAD"), cancellable = true)
  private void fireImmune(CallbackInfoReturnable<Boolean> cir) {
    if (this instanceof ZombieEntityInterface zombie &&
        zombie.getKind().equals(ZombieKind.Flame)) {
      cir.setReturnValue(true);
      cir.cancel();
    }
  }
}
