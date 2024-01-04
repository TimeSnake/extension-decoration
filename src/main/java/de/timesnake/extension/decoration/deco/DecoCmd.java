/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.decoration.deco;

import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.decoration.armorstand.StandEditor;
import de.timesnake.extension.decoration.heads.HeadsManager;
import de.timesnake.library.chat.Code;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;

public class DecoCmd implements CommandListener {

  private final Code perm = Plugin.DECO.createPermssionCode("exdecoration");
  private final Code headsPerm = Plugin.DECO.createPermssionCode("exdecoration.heads");
  private final Code reloadPerm = Plugin.DECO.createPermssionCode("exdecoration.heads.reload");
  private final Code armorstandPerm = Plugin.DECO.createPermssionCode("exdecoration.armorstand");
  private final Code itemframePerm = Plugin.DECO.createPermssionCode("exdecoration.itemframe");
  private final Code headCreatePerm = Plugin.DECO.createPermssionCode("exdeco.heads.create");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    if (!args.isLengthHigherEquals(1, true)) {
      return;
    }

    if (!sender.isPlayer(true)) {
      return;
    }

    User user = sender.getUser();

    switch (args.getString(0).toLowerCase()) {
      case "heads", "head" -> {
        if (!sender.hasPermission(this.headsPerm)) {
          return;
        }
        if (args.isLengthEquals(2, false) && args.getString(1).equalsIgnoreCase("reload")) {
          if (!sender.hasPermission(this.reloadPerm)) {
            return;
          }

          DecoManager.getInstance().getHeadsManager().reloadHeads();
        } else if (args.isLengthEquals(4, false)) {
          sender.hasPermissionElseExit(this.headCreatePerm);

          String section = args.getString(1);
          String name = args.getString(2);
          String url = args.getString(3);

          if (url.length() != HeadsManager.URL_LENGTH) {
            sender.sendTDMessage("§wURL is malformed (should be 64 characters long)");
            return;
          }

          DecoManager.getInstance().getHeadsManager().addHead(section, name, url);
          sender.sendTDMessage("§sAdded head §v" + name);
        } else {
          user.openInventory(
              DecoManager.getInstance().getHeadsManager().getFirstPageInventory());
        }
      }
      case "stand", "armorstand" -> {
        if (!sender.hasPermission(this.armorstandPerm)) {
          return;
        }
        StandEditor editor = new StandEditor(user);
        user.addItem(editor.getTool());
        user.addItem(editor.getAngleTool());
      }
      case "frame", "itemframe" -> {
        if (!sender.hasPermission(this.itemframePerm)) {
          return;
        }
        user.addItem(DecoManager.getInstance().getItemFrameManager().getItemFrameItem());
      }
    }
  }

  @Override
  public Completion getTabCompletion() {
    return new Completion()
        .addArgument(new Completion(this.headsPerm, "heads")
            .addArgument(new Completion(this.headCreatePerm, "<name>")
                .addArgument(new Completion("<url>"))))
        .addArgument(new Completion(this.armorstandPerm, "stand", "armorstand"))
        .addArgument(new Completion(this.itemframePerm, "frame", "itemframe"));
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }
}
