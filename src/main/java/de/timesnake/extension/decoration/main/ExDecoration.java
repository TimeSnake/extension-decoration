/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.decoration.main;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.extension.decoration.deco.DecoCmd;
import de.timesnake.extension.decoration.deco.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ExDecoration extends JavaPlugin {

  private static ExDecoration plugin;

  public static ExDecoration getPlugin() {
    return plugin;
  }

  @Override
  public void onEnable() {
    plugin = this;

    Server.getCommandManager().addCommand(this, "decoration",
        List.of("deco", "decos", "decorations"), new DecoCmd(), Plugin.DECO);

  }
}
