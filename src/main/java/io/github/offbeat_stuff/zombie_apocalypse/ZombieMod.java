package io.github.offbeat_stuff.zombie_apocalypse;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import io.github.offbeat_stuff.zombie_apocalypse.config.Config;
import io.github.offbeat_stuff.zombie_apocalypse.config.ConfigHandler;
import io.github.offbeat_stuff.zombie_apocalypse.config.ScreamHandler;
import io.github.offbeat_stuff.zombie_apocalypse.config.SpawnHandler;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZombieMod implements ModInitializer {
  public static final Logger LOGGER =
      LoggerFactory.getLogger("zombie_apocalypse");
  public static Xoroshiro128PlusPlusRandom XRANDOM =
      new Xoroshiro128PlusPlusRandom(new Random().nextLong());

  private final File settingsFile =
      new File("config", "zombie_apocalypse.toml");

  private void handleConfig() {
    Config config = null;
    if (settingsFile.exists()) {
      try {
        config = new Toml().read(settingsFile).to(Config.class);
      } catch (Exception ex) {
        System.out.println("Error while loading config! Creating a new one!");
        ex.printStackTrace();
      }
    }

    if (config == null) {
      config = new Config();
    }
    ConfigHandler.handleConfig(config);
    if (settingsFile.exists())
      settingsFile.delete();
    try {
      new TomlWriter().write(config, settingsFile);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  public static boolean wasTimeRight = false;

  private void spawnZombiesInWorld(ServerWorld world) {
    if (!(ConfigHandler.allowedDimensions.contains(
            world.getRegistryKey().getValue())))
      return;

    var time = world.getTimeOfDay() % 24000;
    if (!ConfigHandler.isTimeRight.test((int)time)) {
      wasTimeRight = false;
      return;
    }

    if (!wasTimeRight) {
      for (var player : world.getPlayers()) {
        ScreamHandler.scream(player);
      }
    }

    wasTimeRight = true;

    SpawnHandler.spawnZombiesInWorld(world);
  }

  @Override
  public void onInitialize() {
    handleConfig();
    ServerTickEvents.END_SERVER_TICK.register(
        server -> server.getWorlds().forEach(this::spawnZombiesInWorld));
  }
}
