/*
 * extension-decoration.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package de.timesnake.extension.decoration.deco;

import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.world.entity.MapDisplayBuilder;
import de.timesnake.library.extension.util.cmd.Arguments;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PictureMapManager {

    private final HashMap<User, ExItemStack[][]> mapItemsPerUser = new HashMap<>();

    public ExItemStack[][] convertImage(File file, int width, int height, ScaleType scaleType) {
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            return new ExItemStack[0][0];
        }

        switch (scaleType) {
            case WIDTH -> image = (BufferedImage) image.getScaledInstance(width * 128,
                    (int) (width * 128 / ((double) image.getWidth(null))), Image.SCALE_DEFAULT);
            case HEIGHT ->
                    image = (BufferedImage) image.getScaledInstance((int) (height * 128 / ((double) image.getHeight(null))),
                            height * 128, Image.SCALE_DEFAULT);
            case CUSTOM -> image = (BufferedImage) image.getScaledInstance(width * 128, height * 128, 0);
        }

        MapDisplayBuilder displayBuilder = new MapDisplayBuilder(width, height);

        displayBuilder.drawImage(0, 0, image);

        return displayBuilder.onItems();
    }

    public void handleCmd(Sender sender, Arguments<Argument> args) {
        User user = sender.getUser();


    }

    public enum ScaleType {
        WIDTH,
        HEIGHT,
        CUSTOM
    }
}
