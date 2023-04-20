/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.decoration.deco;

import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.decoration.armorstand.StandEditor;
import de.timesnake.extension.decoration.heads.HeadsManager;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.chat.Plugin;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import java.util.ArrayList;
import java.util.List;

public class DecoCmd implements CommandListener {

    private Code headsPerm;
    private Code reloadPerm;
    private Code armorstandPerm;
    private Code itemframePerm;
    private Code headCreatePerm;

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd,
            Arguments<Argument> args) {
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
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd,
            Arguments<Argument> args) {
        if (args.getLength() == 1) {
            return List.of("heads", "stand", "armorstand", "frame", "itemframe");
        } else if (args.length() == 2) {
            return new ArrayList<>(DecoManager.getInstance().getHeadsManager().getSections());
        } else if (args.length() == 3) {
            return List.of("<name>");
        } else if (args.length() == 4) {
            return List.of("<url>");
        }
        return null;
    }

    @Override
    public void loadCodes(Plugin plugin) {
        this.headsPerm = plugin.createPermssionCode("exdecoration.heads");
        this.reloadPerm = plugin.createPermssionCode("exdecoration.heads.reload");
        this.armorstandPerm = plugin.createPermssionCode("exdecoration.armorstand");
        this.itemframePerm = plugin.createPermssionCode("exdecoration.itemframe");
        this.headCreatePerm = plugin.createPermssionCode("exdeco.heads.create");
    }
}
