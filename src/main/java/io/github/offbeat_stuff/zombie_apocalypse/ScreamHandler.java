package io.github.offbeat_stuff.zombie_apocalypse;

import io.github.offbeat_stuff.zombie_apocalypse.config.Config.ScreamConfig;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundNameS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket.Action;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class ScreamHandler {
  private static boolean disabled;
  private static Text message;
  private static Identifier sound;
  private static float volume;
  private static float pitch;

  public static void load(ScreamConfig conf) {
    disabled = !conf.enabled;
    message =
        new LiteralText(conf.message).copy().formatted(Formatting.DARK_RED);
    sound = new Identifier(conf.sound);
    volume = (float)conf.volume;
    pitch = (float)conf.pitch;
  }

  public static void scream(ServerPlayerEntity player) {
    if (disabled) {
      return;
    }
    player.networkHandler.sendPacket(new TitleS2CPacket(Action.TITLE, message));
    var v = new Vec3d(player.x, player.y + player.getEyeHeight(), player.z);
    player.networkHandler.sendPacket(new PlaySoundNameS2CPacket(
        sound, SoundCategory.AMBIENT, v, volume, pitch));
  }
}
