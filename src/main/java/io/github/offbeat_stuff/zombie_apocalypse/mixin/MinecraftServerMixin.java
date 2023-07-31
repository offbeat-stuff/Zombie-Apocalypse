package io.github.offbeat_stuff.zombie_apocalypse.mixin;

import io.github.offbeat_stuff.zombie_apocalypse.ZombieMod;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

  @Inject(method = "method_20324", at = @At("TAIL"))
  private void spawnZombies(CallbackInfo ci) {
    ((MinecraftServer)(Object)this)
        .method_20351()
        .forEach(ZombieMod::spawnZombiesInWorld);
  }
}
