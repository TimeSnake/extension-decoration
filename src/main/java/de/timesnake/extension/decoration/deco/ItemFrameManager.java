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

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.extension.decoration.main.ExDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemFrameManager implements Listener {

    private static final NamespacedKey KEY = new NamespacedKey(ExDecoration.getPlugin(), "invisible");

    public ItemFrameManager() {
        Server.registerListener(this, ExDecoration.getPlugin());
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if (item == null || !item.hasItemMeta() || !(event.getEntity() instanceof ItemFrame)) {
            return;
        }

        if (!item.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.BYTE)) {
            return;
        }

        ((ItemFrame) event.getEntity()).setVisible(false);
    }

    public ItemStack getItemFrameItem() {
        ItemStack item = new ItemStack(Material.ITEM_FRAME);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Invisible Item Frame");
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }
}
