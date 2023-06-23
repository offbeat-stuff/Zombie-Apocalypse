package io.github.offbeat_stuff.zombie_apocalypse.config;

import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ScreamHandler {
  private static boolean shouldScream;

  public static void handle(boolean hmm) { shouldScream = hmm; }

  public static void scream(ServerPlayerEntity player) {
    if (!shouldScream) {
      return;
    }
    player.networkHandler.sendPacket(new TitleS2CPacket(
        Text.literal("Zombies Are Coming").formatted(Formatting.DARK_RED)));
  }
}
