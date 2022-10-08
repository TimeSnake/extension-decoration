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

import de.timesnake.extension.decoration.heads.HeadsManager;

public class DecoManager {

    private static DecoManager instance;

    public static DecoManager getInstance() {
        if (instance == null) {
            instance = new DecoManager();
        }
        return instance;
    }

    private final HeadsManager headsManager;
    private final ItemFrameManager itemFrameManager;

    public DecoManager() {
        this.headsManager = new HeadsManager();
        this.itemFrameManager = new ItemFrameManager();
    }

    public HeadsManager getHeadsManager() {
        return headsManager;
    }

    public ItemFrameManager getItemFrameManager() {
        return itemFrameManager;
    }
}
