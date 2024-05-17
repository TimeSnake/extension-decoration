/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.decoration.deco;

public class Plugin extends de.timesnake.basic.bukkit.util.chat.Plugin {

  public static final Plugin DECO = new Plugin("Deco", "EXD");

  protected Plugin(String name, String code) {
    super(name, code);
  }
}
