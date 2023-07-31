package io.github.offbeat_stuff.zombie_apocalypse;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import io.github.offbeat_stuff.zombie_apocalypse.config.Config;
import io.github.offbeat_stuff.zombie_apocalypse.config.ConfigHandler;
import java.io.IOException;
import java.util.Random;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZombieMod implements ModInitializer {
  public static final Logger LOGGER =
      LoggerFactory.getLogger("zombie_apocalypse");
  public static Random XRANDOM = new Random();

  private void handleConfig() {
    var settingsFile = FabricLoader.getInstance()
                           .getConfigDir()
                           .resolve("zombie_apocalypse.toml")
                           .toFile();
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

    ConfigHandler.correct(config);
    ConfigHandler.load(config);

    if (settingsFile.exists())
      settingsFile.delete();
    try {
      new TomlWriter().write(config, settingsFile);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  public static boolean wasTimeRight = false;

  public static void spawnZombiesInWorld(ServerWorld world) {
    if (!SpawnHandler.checkWorld(world)) {
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
  }
}
