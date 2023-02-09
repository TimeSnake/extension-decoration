/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.decoration.deco;

import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.decoration.armorstand.StandEditor;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.chat.Plugin;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import java.util.List;

public class DecoCmd implements CommandListener {

    private Code headsPerm;
    private Code reloadPerm;
    private Code armorstandPerm;
    private Code itemframePerm;

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
                }
                user.openInventory(
                        DecoManager.getInstance().getHeadsManager().getFirstPageInventory());
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
        }
        return null;
    }

    @Override
    public void loadCodes(Plugin plugin) {
        this.headsPerm = plugin.createPermssionCode("exdecoration.heads");
        this.reloadPerm = plugin.createPermssionCode("exdecoration.heads.reload");
        this.armorstandPerm = plugin.createPermssionCode("exdecoration.armorstand");
        this.itemframePerm = plugin.createPermssionCode("exdecoration.itemframe");
    }
}
