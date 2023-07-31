package io.github.offbeat_stuff.zombie_apocalypse;

import static io.github.offbeat_stuff.zombie_apocalypse.ZombieMod.XRANDOM;

import io.github.offbeat_stuff.zombie_apocalypse.config.Config.ScreamConfig;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ScreamHandler {
  private static boolean disabled;
  private static Text message;
  private static RegistryEntry<SoundEvent> sound;
  private static float volume;
  private static float pitch;

  public static void load(ScreamConfig conf) {
    disabled = !conf.enabled;
    message = Text.literal(conf.message).formatted(Formatting.DARK_RED);
    sound = VersionDependent.getSoundEntry(conf.sound);
    volume = (float)conf.volume;
    pitch = (float)conf.pitch;
  }

  public static void scream(ServerPlayerEntity player) {
    if (disabled) {
      return;
    }
    player.networkHandler.sendPacket(new TitleS2CPacket(message));
    var v = player.getEyePos().toVector3f();
    player.networkHandler.sendPacket(
        new PlaySoundS2CPacket(sound, SoundCategory.AMBIENT, v.x, v.y, v.z,
                               volume, pitch, XRANDOM.nextLong()));
  }
}
