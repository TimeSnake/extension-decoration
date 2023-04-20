/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.decoration.heads;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.database.util.Database;
import de.timesnake.database.util.decoration.DbHead;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.inventory.Inventory;

public class HeadsManager {

    public static final int URL_LENGTH = 64;

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

    public Collection<String> getSections() {
        return this.headsBySection.keySet();
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

    public void addHead(String section, String name, String url) {
        Database.getDecorations().addHead(url, name, section);
        this.reloadHeads();
    }
}
