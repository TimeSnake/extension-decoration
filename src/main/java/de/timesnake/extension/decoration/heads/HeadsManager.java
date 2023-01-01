/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.decoration.heads;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.database.util.Database;
import de.timesnake.database.util.decoration.DbHead;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class HeadsManager {

    private final Map<String, Collection<Head>> headsBySection = new HashMap<>();
    private final Map<String, Head> headsByName = new HashMap<>();

    private final Map<Integer, HeadsPage> pagesByNumber = new HashMap<>();
    private final Map<ExItemStack, HeadsPage> pagesByHeader = new HashMap<>();
    private Integer pages;

    public HeadsManager() {
        this.loadHeads();
        this.loadPages();
    }

    private void loadHeads() {
        // init sections
        for (String section : Database.getDecorations().getSections()) {
            this.headsBySection.put(section, new HashSet<>());
        }

        // init heads
        for (DbHead dbHead : Database.getDecorations().getHeads()) {
            Head head = new Head(dbHead);
            this.headsBySection.get(dbHead.getSection()).add(head);
            this.headsByName.put(dbHead.getName(), head);
        }
    }

    private void loadPages() {
        int i = 1;
        for (Map.Entry<String, Collection<Head>> entry : this.headsBySection.entrySet()) {

            Iterator<Head> headIterator = entry.getValue().iterator();

            while (headIterator.hasNext()) {
                Collection<Head> heads = new HashSet<>();
                for (int item = 0; item < 5 * 9 && headIterator.hasNext(); item++) {
                    heads.add(headIterator.next());
                }

                HeadsPage page = new HeadsPage(i, entry.getKey(), heads);
                this.pagesByHeader.put(page.getPageHeader(), page);
                this.pagesByNumber.put(i, page);
                i++;
            }
        }

        this.pages = i - 1;
    }

    public void reloadHeads() {
        this.loadHeads();
        this.loadPages();
    }

    public Map<Integer, HeadsPage> getPagesByNumber() {
        return pagesByNumber;
    }

    public Map<ExItemStack, HeadsPage> getPagesByHeader() {
        return pagesByHeader;
    }

    public Inventory getFirstPageInventory() {
        return this.pagesByNumber.get(1).getInventory();
    }

    public Integer getPages() {
        return pages;
    }
}
