package io.github.offbeat_stuff.zombie_apocalypse;

import io.github.offbeat_stuff.zombie_apocalypse.config.Config.ScreamConfig;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket.Action;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

public class ScreamHandler {
  private static boolean disabled;
  private static Text message;
  private static SoundEvent sound;
  private static float volume;
  private static float pitch;

  public static void load(ScreamConfig conf) {
    disabled = !conf.enabled;
    message = Text.of(conf.message).copy().formatted(Formatting.DARK_RED);
    sound = VersionDependent.getSound(conf.sound);
    volume = (float)conf.volume;
    pitch = (float)conf.pitch;
  }

  public static void scream(ServerPlayerEntity player) {
    if (disabled) {
      return;
    }
    player.networkHandler.sendPacket(new TitleS2CPacket(Action.TITLE, message));
    var v = new Vec3d(player.getX(), player.getEyeY(), player.getZ());
    player.networkHandler.sendPacket(new PlaySoundS2CPacket(
        sound, SoundCategory.AMBIENT, v.x, v.y, v.z, volume, pitch));
  }
}
