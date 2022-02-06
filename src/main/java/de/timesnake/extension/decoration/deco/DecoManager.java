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
