/*
 * Copyright (C) 2023 timesnake
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

  private static final NamespacedKey KEY = new NamespacedKey(ExDecoration.getPlugin(),
      "invisible");

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
