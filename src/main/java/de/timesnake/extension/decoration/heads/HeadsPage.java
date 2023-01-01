/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.decoration.heads;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickListener;
import de.timesnake.extension.decoration.deco.DecoManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class HeadsPage implements UserInventoryClickListener, InventoryHolder {

    public static final String INV_NAME = "Deco Heads";

    private final Integer pageNumber;
    private final String name;
    private final String section;
    private final ExItemStack pageHeader;
    private final ExItemStack previousPage;
    private final ExItemStack nextPage;
    private final ExInventory inventory;

    public HeadsPage(Integer pageNumber, String section, Collection<Head> heads) {
        this.pageNumber = pageNumber;
        this.section = section;
        this.name = INV_NAME + " " + this.section;

        List<ExItemStack> listenerItems = new ArrayList<>();

        this.pageHeader = new ExItemStack(4, Material.PAPER, "§9Page " + this.pageNumber);
        this.previousPage = new ExItemStack(0, Material.MAP, "§9Previous Page");
        this.nextPage = new ExItemStack(8, Material.MAP, "§9Next Page");

        listenerItems.add(pageHeader);
        listenerItems.add(previousPage);
        listenerItems.add(nextPage);

        this.inventory = new ExInventory(6 * 9, Component.text(this.name), this);
        this.inventory.setItemStack(this.pageHeader);
        this.inventory.setItemStack(this.previousPage);
        this.inventory.setItemStack(this.nextPage);

        LinkedList<Head> sortedHeads = new LinkedList<>(heads);
        sortedHeads.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

        for (int i = 0; i < sortedHeads.size(); i++) {
            Head head = sortedHeads.get(i);
            ExItemStack item = new ExItemStack(head.getItem());
            item.setSlot(i + 9);
            item.setDisplayName("§6" + head.getName());
            item.setLore("", "§f" + head.getSection());

            this.inventory.setItemStack(item);
            listenerItems.add(item);
        }

        Server.getInventoryEventManager().addClickListener(this, this);
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public String getSection() {
        return section;
    }

    public ExItemStack getPageHeader() {
        return pageHeader;
    }

    @Override
    public Inventory getInventory() {
        return inventory.getInventory();
    }

    @Override
    public void onUserInventoryClick(UserInventoryClickEvent event) {
        User user = event.getUser();
        ExItemStack item = event.getClickedItem();
        HeadsManager headsManager = DecoManager.getInstance().getHeadsManager();

        event.setCancelled(true);

        if (item.equals(this.pageHeader)) {
            return;
        }

        if (item.equals(this.nextPage)) {
            HeadsPage nextPage = headsManager.getPagesByNumber().get(this.pageNumber + 1);
            if (nextPage == null) {
                user.openInventory(headsManager.getPagesByNumber().get(1).getInventory());
                return;
            }
            user.openInventory(nextPage.getInventory());
        } else if (item.equals(this.previousPage)) {
            HeadsPage previousPage = headsManager.getPagesByNumber().get(this.pageNumber - 1);
            if (previousPage == null) {
                user.openInventory(headsManager.getPagesByNumber().get(headsManager.getPages())
                        .getInventory());
                return;
            }
            user.openInventory(previousPage.getInventory());
        } else {
            ItemStack clonedItem = item.cloneWithoutId();
            user.getPlayer().setItemOnCursor(clonedItem);
        }
    }
}
