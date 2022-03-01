package de.timesnake.extension.decoration.armorstand;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.ChatColor;
import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickListener;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.extension.decoration.deco.Plugin;
import de.timesnake.extension.decoration.main.ExDecoration;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandEditor implements Listener, UserInventoryInteractListener, UserInventoryClickListener, InventoryHolder {

    private static final Double LEG_HEIGHT = 0.8;
    private static final Double ARM_HEIGHT = 1.4;

    private static final double ANGLE = 0.0625 * (2 * Math.PI);

    private static final HashMap<ExItemStack, EditType> EDIT_TYPES_BY_ITEM = new HashMap<>();

    static {
        EDIT_TYPES_BY_ITEM.put(new ExItemStack(0, Material.FEATHER, "§6Visibility", List.of("§fSet the visibility of an armorstand")), EditType.VISIBLE);
        EDIT_TYPES_BY_ITEM.put(new ExItemStack(1, Material.SMOOTH_STONE_SLAB, "§6Base Plate", List.of("§fSet the base plate visibility of an armorstand")), EditType.BASE_PLATE);
        EDIT_TYPES_BY_ITEM.put(new ExItemStack(2, Material.STICK, "§6Arms", List.of("§fSet the arm visibility of an armorstand")), EditType.ARMS);
        EDIT_TYPES_BY_ITEM.put(new ExItemStack(3, Material.EGG, "§6Small", List.of("§fSet the size of an armorstand")), EditType.SMALL);
        EDIT_TYPES_BY_ITEM.put(new ExItemStack(4, Material.LEVER, "§6Lock", List.of("§fLock the the items of an armorstand")), EditType.LOCK);
        EDIT_TYPES_BY_ITEM.put(new ExItemStack(5, Material.DEAD_BUSH, "§6Slots", List.of("§fSet the items slots of an armorstand")), EditType.SLOTS);
        EDIT_TYPES_BY_ITEM.put(new ExItemStack(6, Material.COMPASS, "§6Reset Rotations", List.of("§fResets the rotations of an armorstand")), EditType.RESET_ROTATION);
        EDIT_TYPES_BY_ITEM.put(new ExItemStack(7, Material.BEDROCK, "§6Gravity", List.of("§fToggles the gravity of an armorstand")), EditType.GRAVITY);
        EDIT_TYPES_BY_ITEM.put(new ExItemStack(9, Material.ARMOR_STAND, "§6Copy", List.of("§fCopy an existent armorstand")), EditType.COPY);
        EDIT_TYPES_BY_ITEM.put(new ExItemStack(10, Material.ARMOR_STAND, "§6Paste", List.of("§fPaste the copied or last edited armorstand on your location")), EditType.PASTE);
    }

    public enum BodyPart {
        BODY, HEAD, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG
    }

    public enum Axis {
        FRONT, SIDE, ROTATION
    }

    public StandEditor(User user) {
        this.user = user;

        this.toolInv = Server.createExInventory(18, "Armorstand Tool", this);

        for (ExItemStack item : EDIT_TYPES_BY_ITEM.keySet()) {
            this.toolInv.setItemStack(item);
        }

        this.itemInv = Server.createExInventory(18, "Armorstand Items", this);
        this.itemInv.setItemStack(0, new ExItemStack(Material.STICK, "§6Left Arm"));
        this.itemInv.setItemStack(1, new ExItemStack(Material.PLAYER_HEAD, "§6Head"));
        this.itemInv.setItemStack(2, new ExItemStack(Material.STICK, "§6Right Arm"));

        for (int i = 3; i < 18; i++) {
            this.itemInv.setItemStack(i, PLACE_HOLDER);
            if (i == 8) {
                i += 3;
            }
        }

        this.bodyPartInv = Server.createExInventory(45, "Armorstand Body Parts", this);

        for (ExItemStack item : BODY_PARTS_BY_ITEM.keySet()) {
            this.bodyPartInv.setItemStack(item);
        }

        for (ExItemStack item : AXIS_BY_ITEM.keySet()) {
            this.bodyPartInv.setItemStack(item);
        }

        Server.getInventoryEventManager().addInteractListener(this, tool, angleTool);
        Server.getInventoryEventManager().addClickListener(this, this);

        Server.registerListener(this, ExDecoration.getPlugin());
    }

    private static final HashMap<ExItemStack, BodyPart> BODY_PARTS_BY_ITEM = new HashMap<>();

    static {
        BODY_PARTS_BY_ITEM.put(new ExItemStack(11, Material.PLAYER_HEAD, "§6Head"), BodyPart.HEAD);
        BODY_PARTS_BY_ITEM.put(new ExItemStack(20, Material.CHAINMAIL_CHESTPLATE, "§6Chest"), BodyPart.BODY);
        BODY_PARTS_BY_ITEM.put(new ExItemStack(21, Material.STICK, "§6Right Arm"), BodyPart.RIGHT_ARM);
        BODY_PARTS_BY_ITEM.put(new ExItemStack(19, Material.STICK, "§6Left Arm"), BodyPart.LEFT_ARM);
        BODY_PARTS_BY_ITEM.put(new ExItemStack(30, Material.RABBIT_FOOT, "§6Right Leg"), BodyPart.RIGHT_LEG);
        BODY_PARTS_BY_ITEM.put(new ExItemStack(28, Material.RABBIT_FOOT, "§6Left Leg"), BodyPart.LEFT_LEG);
    }

    private static final HashMap<ExItemStack, Axis> AXIS_BY_ITEM = new HashMap<>();

    static {
        AXIS_BY_ITEM.put(new ExItemStack(15, Material.PLAYER_HEAD, "§6Front"), Axis.FRONT);
        AXIS_BY_ITEM.put(new ExItemStack(24, Material.STICK, "§6Side"), Axis.SIDE);
        AXIS_BY_ITEM.put(new ExItemStack(33, Material.COMPASS, "§6Rotation"), Axis.ROTATION);
    }

    private static final ExItemStack PLACE_HOLDER = new ExItemStack(Material.GRAY_STAINED_GLASS_PANE, "");


    private final ExItemStack tool = new ExItemStack(Material.EMERALD, "§6Armor Stand Tool");
    private final ExItemStack angleTool = new ExItemStack(Material.FEATHER, "§6Angle Tool");

    private final User user;

    private ArmorStand armorStand;

    private final ExInventory toolInv;
    private final ExInventory itemInv;

    private final ExInventory bodyPartInv;

    private EditType editType = EditType.ARMS;
    private BodyPart bodyPart = BodyPart.HEAD;
    private Axis axis = Axis.FRONT;

    private void edit() {
        switch (this.editType) {
            case VISIBLE:
                this.armorStand.setVisible(!this.armorStand.isVisible());
                user.sendPluginMessage(Plugin.DECO, ChatColor.PERSONAL + "Visible: " + ChatColor.VALUE + this.armorStand.isVisible());
                break;
            case BASE_PLATE:
                this.armorStand.setBasePlate(!this.armorStand.hasBasePlate());
                user.sendPluginMessage(Plugin.DECO, ChatColor.PERSONAL + "Base Plate: " + ChatColor.VALUE + this.armorStand.hasBasePlate());
                break;
            case ARMS:
                this.armorStand.setArms(!this.armorStand.hasArms());
                user.sendPluginMessage(Plugin.DECO, ChatColor.PERSONAL + "Arms: " + ChatColor.VALUE + this.armorStand.hasArms());
                break;
            case SMALL:
                this.armorStand.setSmall(!this.armorStand.isSmall());
                user.sendPluginMessage(Plugin.DECO, ChatColor.PERSONAL + "Small: " + ChatColor.VALUE + this.armorStand.isSmall());
                break;
            case LOCK:
                if (this.armorStand.isSlotDisabled(EquipmentSlot.FEET)) {
                    this.armorStand.removeDisabledSlots(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND);
                } else {
                    this.armorStand.setDisabledSlots(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND);
                }
                user.sendPluginMessage(Plugin.DECO, ChatColor.PERSONAL + "Locked: " + ChatColor.VALUE + this.armorStand.isSlotDisabled(EquipmentSlot.FEET));
                break;
            case SLOTS:
                this.armorStand.setItem(EquipmentSlot.HAND, this.itemInv.getInventory().getItem(11));
                this.armorStand.setItem(EquipmentSlot.OFF_HAND, this.itemInv.getInventory().getItem(9));
                this.armorStand.setItem(EquipmentSlot.HEAD, this.itemInv.getInventory().getItem(10));
                break;
            case COPY:
                user.sendPluginMessage(Plugin.DECO, "Copied");
                break;
            case RESET_ROTATION:
                this.armorStand.setRightArmPose(new EulerAngle(0, 0, 0));
                this.armorStand.setRightLegPose(new EulerAngle(0, 0, 0));
                this.armorStand.setLeftArmPose(new EulerAngle(0, 0, 0));
                this.armorStand.setLeftLegPose(new EulerAngle(0, 0, 0));
                this.armorStand.setHeadPose(new EulerAngle(0, 0, 0));
                this.armorStand.setBodyPose(new EulerAngle(0, 0, 0));
                user.sendPluginMessage(Plugin.DECO, "Reset");
                break;
            case GRAVITY:
                this.armorStand.setGravity(!this.armorStand.hasGravity());
                user.sendPluginMessage(Plugin.DECO, ChatColor.PERSONAL + "Gravity: " + ChatColor.VALUE + this.armorStand.hasGravity());
                break;
            default:
                user.sendPluginMessage(Plugin.DECO, ChatColor.PERSONAL + "No tool selected");

        }
    }

    @Override
    public void onUserInventoryClick(UserInventoryClickEvent event) {
        ExItemStack item = event.getClickedItem();

        if (event.getInventory().equals(this.itemInv.getInventory())) {
            if (event.getSlot() == 9 || event.getSlot() == 10 || event.getSlot() == 11) {
                return;
            }

            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        if (event.getInventory().equals(this.toolInv.getInventory())) {

            for (Map.Entry<ExItemStack, EditType> entry : EDIT_TYPES_BY_ITEM.entrySet()) {
                if (item.equals(entry.getKey())) {
                    this.editType = entry.getValue();

                    user.sendPluginMessage(Plugin.DECO, ChatColor.PERSONAL + "Tool: " + ChatColor.VALUE + this.editType.name().toLowerCase());

                    if (this.editType.equals(EditType.SLOTS)) {
                        this.user.openInventory(this.itemInv);
                        return;
                    }

                    break;
                }
            }
        } else if (event.getInventory().equals(this.bodyPartInv.getInventory())) {
            for (Map.Entry<ExItemStack, BodyPart> entry : BODY_PARTS_BY_ITEM.entrySet()) {
                if (item.equals(entry.getKey())) {
                    this.bodyPart = entry.getValue();
                    break;
                }
            }

            for (Map.Entry<ExItemStack, Axis> entry : AXIS_BY_ITEM.entrySet()) {
                if (item.equals(entry.getKey())) {
                    this.axis = entry.getValue();
                }
            }
        }


        event.getUser().closeInventory();
    }

    @Deprecated
    @Override
    public Inventory getInventory() {
        return this.toolInv.getInventory();
    }

    public ExItemStack getTool() {
        return tool;
    }

    public ExItemStack getAngleTool() {
        return angleTool;
    }

    private void editAngle(boolean invert) {
        BodyPart bodyPart = this.bodyPart;

        double angle = invert ? -ANGLE : ANGLE;

        this.armorStand.setVisible(false);
        switch (bodyPart) {
            case BODY:

                this.armorStand.setBodyPose(this.armorStand.getBodyPose().add(this.axis == Axis.ROTATION ? angle : 0, this.axis == Axis.SIDE ? angle : 0, this.axis == Axis.FRONT ? angle : 0));
                break;
            case HEAD:
                this.armorStand.setHeadPose(this.armorStand.getHeadPose().add(this.axis == Axis.SIDE ? angle : 0, this.axis == Axis.ROTATION ? angle : 0, this.axis == Axis.FRONT ? angle : 0));
                break;
            case LEFT_ARM:
                this.armorStand.setLeftArmPose(this.armorStand.getLeftArmPose().add(this.axis == Axis.SIDE ? angle : 0, this.axis == Axis.ROTATION ? angle : 0, this.axis == Axis.FRONT ? angle : 0));
                break;
            case LEFT_LEG:
                this.armorStand.setLeftLegPose(this.armorStand.getLeftLegPose().add(this.axis == Axis.SIDE ? angle : 0, this.axis == Axis.ROTATION ? angle : 0, this.axis == Axis.FRONT ? angle : 0));
                break;
            case RIGHT_ARM:
                this.armorStand.setRightArmPose(this.armorStand.getRightArmPose().add(this.axis == Axis.SIDE ? angle : 0, this.axis == Axis.ROTATION ? angle : 0, this.axis == Axis.FRONT ? angle : 0));
                break;
            case RIGHT_LEG:
                this.armorStand.setRightLegPose(this.armorStand.getRightLegPose().add(this.axis == Axis.SIDE ? angle : 0, this.axis == Axis.ROTATION ? angle : 0, this.axis == Axis.FRONT ? angle : 0));
                break;
        }
        this.armorStand.setVisible(true);
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public User getUser() {
        return user;
    }

    @EventHandler
    public void onArmorStand(PlayerInteractAtEntityEvent e) {
        if (!e.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
            return;
        }

        User user = Server.getUser(e.getPlayer());

        ExItemStack item = new ExItemStack(user.getInventory().getItemInMainHand());

        if (!user.equals(this.user)) {
            return;
        }

        this.armorStand = ((ArmorStand) e.getRightClicked());

        if (item.equals(angleTool)) {
            e.setCancelled(true);
            this.editAngle(user.getPlayer().isSneaking());
        } else if (item.equals(tool)) {
            e.setCancelled(true);
            this.edit();
        }
    }


    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {

        Action action = event.getAction();
        User user = event.getUser();
        ExItemStack item = event.getClickedItem();

        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {

            if (item.equals(tool)) {
                user.openInventory(this.toolInv);
            } else if (item.equals(angleTool)) {
                user.openInventory(this.bodyPartInv);
            }

        } else if ((action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR))) {

            if (this.editType == EditType.PASTE) {
                ArmorStand armorStand = (org.bukkit.entity.ArmorStand) user.getExWorld().spawnEntity(user.getPlayer().getLocation(), EntityType.ARMOR_STAND);
                armorStand.setVisible(this.armorStand.isVisible());
                armorStand.setArms(this.armorStand.hasArms());
                armorStand.setRightArmPose(this.armorStand.getRightArmPose());
                armorStand.setRightLegPose(this.armorStand.getRightLegPose());
                armorStand.setLeftArmPose(this.armorStand.getLeftArmPose());
                armorStand.setLeftLegPose(this.armorStand.getLeftLegPose());
                armorStand.setHeadPose(this.armorStand.getHeadPose());
                armorStand.setBodyPose(this.armorStand.getBodyPose());
                armorStand.setSmall(this.armorStand.isSmall());
                armorStand.setBasePlate(this.armorStand.hasBasePlate());

                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    armorStand.setItem(slot, this.armorStand.getItem(slot));
                }
            }
        }

        event.setCancelled(true);
    }

    public enum EditType {
        VISIBLE, BASE_PLATE, ARMS, SMALL, COPY, PASTE, LOCK, SLOTS, RESET_ROTATION, GRAVITY
    }

}
