package de.timesnake.extension.decoration.deco;

import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.decoration.armorstand.StandEditor;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;

import java.util.List;

public class DecoCmd implements CommandListener {

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (!args.isLengthHigherEquals(1, true)) {
            return;
        }

        if (!sender.isPlayer(true)) {
            return;
        }

        User user = sender.getUser();

        switch (args.getString(0).toLowerCase()) {
            case "heads":
            case "head":
                if (!sender.hasPermission("exdecoration.heads", 2901)) {
                    return;
                }

                if (args.isLengthEquals(2, false) && args.getString(1).equalsIgnoreCase("reload")) {
                    if (!sender.hasPermission("exdecoration.heads.reload", 2902)) {
                        return;
                    }

                    DecoManager.getInstance().getHeadsManager().reloadHeads();
                }

                user.openInventory(DecoManager.getInstance().getHeadsManager().getFirstPageInventory());

                break;
            case "stand":
            case "armorstand":
                if (!sender.hasPermission("exdecoration.armorstand", 2902)) {
                    return;
                }

                StandEditor editor = new StandEditor(user);
                user.addItem(editor.getTool());
                user.addItem(editor.getAngleTool());

                break;
            case "frame":
            case "itemframe":
                if (!sender.hasPermission("exdecoration.itemframe", 2903)) {
                    return;
                }

                user.addItem(DecoManager.getInstance().getItemFrameManager().getItemFrameItem());
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.getLength() == 1) {
            return List.of("heads", "stand", "armorstand", "frame", "itemframe");
        }
        return null;
    }
}
