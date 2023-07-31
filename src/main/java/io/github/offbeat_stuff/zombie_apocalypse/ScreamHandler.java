package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ScreamHandler {
  private static boolean shouldScream;

  public static void setDoScream(boolean doScream) { shouldScream = doScream; }

  public static void scream(ServerPlayerEntity player) {
    if (!shouldScream) {
      return;
    }
    player.networkHandler.sendPacket(new TitleS2CPacket(
        Text.literal("Zombies Are Coming").formatted(Formatting.DARK_RED)));
    var v = player.getEyePos().toVector3f();
    player.networkHandler.sendPacket(new PlaySoundS2CPacket(
        VersionDependent.getSound("entity.zombie.ambient"),
        SoundCategory.HOSTILE, v.x, v.y, v.z, 2.0f, 1.0f, XRANDOM.nextLong()));
  }
}
